package net.kofrasa.services.amqp;

import java.io.Serializable;

/**
 * An interface to represent an AMQP message object
 * @author: francis
 */
public interface AmqpMessage extends Serializable {
    /**
     * Return the message data as array of bytes
     * @return
     */
    byte[] getBody();
}
