package net.kofrasa.services.amqp;

import net.kofrasa.services.Service;

/**
 * @author: francis
 */
public interface AmqpService extends Service {

    /**
     * Gets a new channel to send a message
     * @return
     */
    AmqpChannel createChannel();
}
