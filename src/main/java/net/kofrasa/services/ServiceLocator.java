package net.kofrasa.services;

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
    private final Map<String, Object> values = new HashMap<String, Object>();
    private final Map<String, Object> services = new HashMap<String, Object>();
    private final Map<Class<?>, ServiceFactory> factoryMap = new HashMap<Class<?>, ServiceFactory>();
    private boolean initialized = false;

    enum Locks {
        INSTANCE;
    }

    /**
     * Initialize the service locator
     *
     * @param configuration the properties configuration for the services
     */
    synchronized public void init(final Properties configuration) {
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
                    classMap.put(name, clazz);
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

    public <T> void addFactory(Class<?> clsInterface, ServiceFactory<T> factory) {
        factoryMap.put(clsInterface, Objects.requireNonNull(factory));
    }

    /**
     * Loads an initialized service object
     *
     * @param name
     * @param <T>
     * @return
     */
    public <T extends Service> T load(String name) {
        synchronized (Locks.INSTANCE) {
            if (!services.containsKey(name) && classMap.containsKey(name)) {
                Class clazz = classMap.get(name);
                List<Class<?>> interfaces = getAllInterfaces(clazz);
                T service = null;
                try {
                    if (interfaces.contains(Service.class)) {
                        System.out.println("Initializing " + clazz.getCanonicalName());
                        service = (T) clazz.newInstance();
                        services.put(name, service);
                    } else {
                        for (Class<?> cls : factoryMap.keySet()) {
                            if (interfaces.contains(cls)) {
                                ServiceFactory<T> factory = factoryMap.get(cls);
                                service = factory.getInstance(cls);
                            }
                        }
                    }
                    services.put(name, Objects.requireNonNull(service, "Could not load service " + name));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Objects.requireNonNull((T) services.get(name), "Could not load service " + name);
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
}
