package net.kofrasa.services.amqp;

import net.kofrasa.services.Service;

/**
 * This {@code AmqpService} class represent an AMQP service used for providing queueing facilities
 * @author: francis
 */
public interface AmqpService extends Service {

    /**
     * Gets a new channel to send a message
     * @return
     */
    AmqpChannel createChannel();
}
