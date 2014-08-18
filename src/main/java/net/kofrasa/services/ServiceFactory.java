package net.kofrasa.services;

/**
 * @author: francis
 */
public interface ServiceFactory<T> {
    T getInstance(Class<?> clazz);
}
