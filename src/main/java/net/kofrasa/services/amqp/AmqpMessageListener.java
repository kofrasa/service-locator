package net.kofrasa.services.amqp;

import java.io.Serializable;

/**
 * @author: francis
 */
public interface AmqpMessageListener extends Serializable {

    /**
     * Receives a message from the associated queue
     * @param message
     */
    void onMessage(Serializable message);
}
