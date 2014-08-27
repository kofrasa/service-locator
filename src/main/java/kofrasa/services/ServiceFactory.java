package kofrasa.services;

/**
 * @author: francis
 */
public interface ServiceFactory<T> {
    T newInstance();
}
