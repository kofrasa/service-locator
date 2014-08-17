package net.kofrasa.services.amqp;

import javax.inject.Provider;

/**
 * @author: francis
 */
public interface AmqpProvider<T extends AmqpService> extends Provider<T> {

}
