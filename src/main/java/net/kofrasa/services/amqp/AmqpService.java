package net.kofrasa.services.amqp;

import com.rancard.ext.configuration.Service;

/**
 * @author: francis
 */
public interface AmqpService extends Service {

    /**
     * Gets a new channel to send a message
     * @return
     */
    AmqpChannel createChannel();

    /**
     *
     * @param queue
     * @param message
     */
//    default void send(String queue, AmqpMessage message) {
//        createChannel().send(queue, message);
//    }
}
