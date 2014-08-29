package kofrasa.services.amqp;

/**
 * This {@code AmqpService} class represent an AMQP service used for providing queueing facilities
 * @author: francis
 */
public interface AmqpService {

    /**
     * Creates a new channel
     * @return
     */
    AmqpChannel createChannel();

    /**
     * Creates a new channel bound to a queue
     * @param queue name of queue to bind to
     * @return
     */
    AmqpChannel createChannel(String queue);
}
