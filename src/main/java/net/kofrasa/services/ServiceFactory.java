package net.kofrasa.services;

/**
 * This interface must be implemented to generate instances of the specified generic type parameter.
 * The implementing class must have a default constructor.
 *
 * @author: francis
 */
public interface ServiceFactory<T> extends Runnable{
    T newInstance();
}
