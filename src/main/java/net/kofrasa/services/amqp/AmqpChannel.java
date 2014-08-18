package net.kofrasa.services.amqp;

import java.io.Serializable;

/**
 * The {@code AmqpChannel} class represents an AMQP channel created using an
 * {@link AmqpService} implementation
 *
 * @author: francis
 */
public interface AmqpChannel extends AutoCloseable {

    /**
     * Sends a message on this channel
     * @param queue name of queue
     * @param message the message to send
     */
    void send(String queue, Serializable message);
}
