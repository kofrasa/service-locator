package net.kofrasa.services.amqp;

/**
 * This {@code AmqpService} class represent an AMQP service used for providing queueing facilities
 * @author: francis
 */
public interface AmqpService {

    /**
     * Gets a new channel to send a message
     * @return
     */
    AmqpChannel createChannel();
}
