package flipkart.cp.convert.chronosQ.impl.rmq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import flipkart.cp.convert.chronosQ.core.SchedulerSink;
import flipkart.cp.convert.chronosQ.exceptions.ErrorCode;
import flipkart.cp.convert.chronosQ.exceptions.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class RmqSchedulerSink implements SchedulerSink {

    private Channel channel;
    private String exchange;
    private String queueName;
    private BasicProperties properties=null;
    static Logger log = LoggerFactory.getLogger(RmqSchedulerSink.class.getName());

    public RmqSchedulerSink(Channel channel, String exchange, String queueName) {
        this.channel = channel;
        this.exchange = exchange;
        this.queueName = queueName;
    }

    public RmqSchedulerSink(Channel channel, String exchange) {
        this.channel = channel;
        this.exchange = exchange;
    }

    public RmqSchedulerSink(Channel channel, String exchange, String queueName, BasicProperties properties) {
        this(channel, exchange, queueName);
        this.properties = properties;
    }

    @Override
    public void giveExpiredForProcessing(String value) throws SchedulerException {
        try {
            log.info("Got message to be published " + value);
            channel.basicPublish(exchange, queueName, null, value.getBytes());
            log.info("Message published -" + value);
        } catch (IOException ex) {
            log.error("Unable to publish message to queue - " + value + "-" + ex.getMessage());
            throw new SchedulerException(ex, ErrorCode.SCHEDULER_SINK_ERROR);
        }
    }

    @Override
    public void giveExpiredListForProcessing(List<String> values) throws SchedulerException {
        for (String value : values)
            giveExpiredForProcessing(value);

    }
}
