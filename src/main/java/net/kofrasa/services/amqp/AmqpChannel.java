package net.kofrasa.services.amqp;

/**
 * @author: francis
 */
public interface AmqpChannel extends AutoCloseable {

    void send(String queue, AmqpMessage message);
}
