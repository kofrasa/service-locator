package net.kofrasa.services;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * ServiceLocator class is small registry implementation responsible for the initializing, and managing
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
    private volatile Map<String, Object> values = new HashMap<String, Object>();
    private volatile Map<String, Object> services = new HashMap<String, Object>();
    private volatile Map<Class<?>, ServiceFactory> factoryMap = new HashMap<Class<?>, ServiceFactory>();
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
     * @param name  identifier of the variable
     * @param value the value
     */
    synchronized public void register(String name, final Object value) {
        values.put(name, value);
    }

    /**
     * Register a {@code ServiceFactory} to handle creating objects of the given interface
     * @param clsInterface
     * @param factory
     * @param <T>
     */
    public <T> void addFactory(Class<?> clsInterface, ServiceFactory<T> factory) {
        factoryMap.put(clsInterface, requireNonNull(factory));
    }

    /**
     * Loads an initialized service object
     *
     * @param name the service identifier
     * @param <T>
     * @return
     */
    public <T> T load(String name) {
        synchronized (this) {
            if (!services.containsKey(name) && classMap.containsKey(name)) {
                Class clazz = classMap.get(name);
                List<Class<?>> interfaces = getAllInterfaces(clazz);
                T service = null;
                try {
                    boolean loaded = false;
                    if (!factoryMap.isEmpty()) {
                        for (Class<?> cls : factoryMap.keySet()) {
                            if (interfaces.contains(cls)) {
                                ServiceFactory<T> factory = factoryMap.get(cls);
                                System.out.println("Initializing " + clazz.getCanonicalName());
                                service = factory.getInstance(cls);
                            }
                        }
                    }
                    // now attempt with default constructor
                    if (!loaded) {
                        service = (T) clazz.newInstance();
                        services.put(name, service);
                    }
                    services.put(name, requireNonNull(service, "Could not load service " + name));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return requireNonNull((T) services.get(name), "Could not load service " + name);
    }

    /**
     * Returns the registered value of the given key
     *
     * @param name
     * @param <T>
     * @return
     */
    public <T> T value(String name) {
        return (T) values.get(name);
    }

    /**
     * Get all the interfaces implement by the given class
     *
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
