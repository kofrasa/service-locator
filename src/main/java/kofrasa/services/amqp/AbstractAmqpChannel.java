package kofrasa.services.amqp;

import java.io.Serializable;

/**
 * @author: francis
 */
abstract public class AbstractAmqpChannel implements AmqpChannel {

    private volatile AmqpMessageListener listener;
    private volatile String queue;

    public String getQueue() {
        return this.queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public <T extends Serializable> void send(T message) {
        send(message, getQueue());
    }

    public  <T extends Serializable> T receive() {
        return receive(getQueue());
    }

    public void setMessageListener(AmqpMessageListener listener) {
        this.listener = listener;
    }

    public AmqpMessageListener getMessageListener() {
        return this.listener;
    }
}
