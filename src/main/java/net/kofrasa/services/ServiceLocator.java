package net.kofrasa.services;

import com.google.inject.Guice;
import net.kofrasa.services.amqp.AmqpChannel;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code ServiceLocator} class is small registry implementation responsible for the initializing, and managing
 * externally developed application dependencies from a configuration.
 *
 * @author: francis
 */
public enum ServiceLocator {

    /**
     * The singleton instance of the ServiceLocator class
     */
    instance;

    private final Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    private volatile Map<String, Object> values = new ConcurrentHashMap<String, Object>();
    private volatile Map<String, Object> services = new ConcurrentHashMap<String, Object>();
    private volatile Map<String, ServiceFactory> factoryMap = new ConcurrentHashMap<String, ServiceFactory>();
    private volatile boolean initialized = false;

    /**
     * Initialize the service locator
     *
     * @param configuration the properties configuration for the services
     */
    public void init(final Properties configuration) {
        init(configuration, "");
    }

    /**
     * Initialize the service locator
     *
     * @param configuration the properties configuration for the services
     * @param prefix        a prefix string of properties to load
     */
    synchronized public void init(final Properties configuration, String prefix) {
        if (initialized) return;

        prefix = (prefix == null || "".equals(prefix.trim())) ? "" : prefix.trim() + ".";
        // load all configuration strings beginning with service.<name>.<class>=<module>
        // use <name> as the key and initiate the module class
        for (Map.Entry e : configuration.entrySet()) {
            String name = e.getKey().toString();
            if (prefix.equals("") || name.startsWith(prefix)) {
                name = name.substring(prefix.length());
                final String className = e.getValue().toString();
                try {
                    // eagerly ensure that all provided classes can be loaded
                    Class<?> clazz = Class.forName(className);
                    // ensure class has a default constructor
                    boolean loaded = false;
                    for (Constructor<?> c : clazz.getConstructors()) {
                        if (c.isAccessible() && c.getParameterTypes().length == 0) {
                            classMap.put(name, clazz);
                            loaded = true;
                            break;
                        }
                    }
                    if (!loaded)
                        throw new NoSuchMethodException(
                                "No default constructor for class " + clazz.getCanonicalName() + " found");
                } catch (Exception ex) {
                    System.out.printf("[%s] Could not load class %s", ServiceLocator.class.getName(), className);
                    ex.printStackTrace();
                }
            }
        }
        initialized = true;
    }

    /**
     * Register global values
     *
     * @param key   identifier of the variable
     * @param value the value
     */
    synchronized public void register(String key, final Object value) {
        values.put(key, value);
    }

    /**
     * Create and return a singleton (shared instance) of the service object.
     * If the registered service class is a {@code ServiceFactory} it will be used to create the instance.
     * The object instance is created only on the first call of this method
     * @param name the service identifier
     * @param <T>
     * @return the shared instance of the service after the first call
     */
    public <T> T singleton(String name) {
        synchronized (this) {
            if (!services.containsKey(name) && classMap.containsKey(name)) {
                T service = create(name);
                if (service != null) {
                    services.put(name, service);
                }
            }
        }
        return (T) services.get(name);
    }

    /**
     * Create and return a new instance of the service with the given identifier.
     * If the registered service class is a {@code ServiceFactory} it will be used to create the instance.
     * @param name
     * @param <T>
     * @return
     */
    public <T> T create(String name) {
        Class clazz = classMap.get(name);
        T service = null;
        try {
            // ensure singleton factory instance since we only need one factory
            synchronized (this) {
                if (!factoryMap.containsKey(name)) {
                    List<Class<?>> interfaces = getAllInterfaces(clazz);
                    if (interfaces.contains(ServiceFactory.class)) {
                        ServiceFactory<T> factory = (ServiceFactory<T>) clazz.newInstance();
                        factoryMap.put(name, factory);
                    }
                }
            }
            if (factoryMap.containsKey(name)) {
                // create service using a factory
                ServiceFactory<T> factory = factoryMap.get(name);
                service = factory.newInstance();
            } else {
                // now attempt with default constructor
                service = (T) clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requireNonNull(service, "Could not create service " + name);
    }

    /**
     * Returns the registered global value of the given key
     * @param key
     * @param <T>
     * @return
     */
    public <T> T value(String key) {
        return (T) values.get(key);
    }

    /**
     * Get all the interfaces implemented by the given class
     * @param clazz
     * @return
     */
    private List<Class<?>> getAllInterfaces(final Class<?> clazz) {

        Queue<Class<?>> interfaces = new ArrayDeque<Class<?>>();
        Queue<Class<?>> parents = new ArrayDeque<Class<?>>();
        Set<Class<?>> allInterfaces = new LinkedHashSet<Class<?>>();

        interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
        parents.add(clazz.getSuperclass());

        while (!parents.isEmpty()) {
            Class<?> cls = parents.poll();
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
            cls = cls.getSuperclass();
            if (cls != null) {
                parents.add(cls);
            }
        }

        while (!interfaces.isEmpty()) {
            Class<?> cls = interfaces.poll();
            if (allInterfaces.add(cls)) {
                interfaces.addAll(Arrays.asList(cls.getInterfaces()));
                cls = cls.getSuperclass();
                if (cls != null) {
                    interfaces.add(cls);
                }
            }
        }

        return new ArrayList<Class<?>>(allInterfaces);
    }

    // Java 8 compatible Objects.requireNonNull
    private static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    private static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
}