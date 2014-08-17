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
    private boolean initialized = false;

    enum Locks {
        INSTANCE;
    }

    /**
     * Initialize the service locator
     * @param configuration the properties configuration for the services
     */
    synchronized public void init(final Properties configuration) {
        init(configuration, "");
    }

    /**
     * Initialize the service locator
     * @param configuration the properties configuration for the services
     * @param prefix a prefix string of properties to load
     */
    synchronized public void init(final Properties configuration, String prefix) {
        if (initialized) return;

        prefix = (prefix == null) ? "" : prefix.trim() + ".";
        // load all configuration strings beginning with service.<name>.<class>=<module>
        // use <name> as the key and initiate the module class
        for (Map.Entry e : configuration.entrySet()) {
            String key = e.getKey().toString();
            if (key.startsWith(prefix)) {
                configure(key.substring(prefix.length()), e.getValue().toString());
            }
        }
        initialized = true;
    }

    /**
     * Register global values
     * @param name identifier of the variable
     * @param value the value
     */
    synchronized public void register(String name, final Object value) {
        values.put(name, value);
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
                try {
                    System.out.println("Initializing " + clazz.getCanonicalName());
                    T service = (T) clazz.newInstance();
                    services.put(name, service);
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
     * Configure the dependency
     *
     * @param className
     */
    private void configure(String name, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            List<Class<?>> interfaces = getAllInterfaces(clazz);
            if (interfaces.contains(Service.class)) {
                // lazy initialize them first demand
                classMap.put(name, clazz);
            } else {
                throw new Exception("Class " + className + " does not implement Service");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get all the interfaces implement by the given class
     * @param clazz
     * @return
     */
    private List<Class<?>> getAllInterfaces(final Class<?> clazz) {

        Queue<Class<?>> interfaces = new ArrayDeque<Class<?>>();
        Queue<Class<?>> parents = new ArrayDeque<Class<?>>();
        Set<Class<?>> allInterfaces = new LinkedHashSet<Class<?>>();

        interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
        parents.add(clazz.getSuperclass());

        while(!parents.isEmpty()) {
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
