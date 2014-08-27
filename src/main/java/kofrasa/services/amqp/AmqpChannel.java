package kofrasa.services.amqp;

import java.io.Closeable;
import java.io.Serializable;

/**
 * The {@code AmqpChannel} class represents an AMQP channel created using an
 * {@link AmqpService} implementation
 *
 * @author: francis
 */
public interface AmqpChannel extends Closeable {

    /**
     * Sends a message on this channel using the bound queue
     * @param message
     */
    <T extends Serializable> void send(T message);

    /**
     * Sends a message on this channel
     * @param queue name of queue
     * @param message the message to send
     */
    <T extends Serializable> void send(T message, String queue);

    /**
     * Receives a message using the queue bound to this channel
     * @return a Serializable object message
     */
    <T extends Serializable> T receive();

    /**
     * Receives a message from this queue on this channel
     * @param queue the queue to bind to and listen
     * @return a Serializable object message
     */
    <T extends Serializable> T receive(String queue);

    /**
     * Returns name of queue this channel is bound to
     * @return
     */
    String getQueue();

    /**
     * Sets the queue bound to this channel
     * @param queue
     */
    void setQueue(String queue);

    /**
     * Set the listener for this channel
     * @param listener
     */
    void setMessageListener(AmqpMessageListener listener);

    /**
     * Returns the message listener for this channel
     * @return
     */
    AmqpMessageListener getMessageListener();
}
