package lazecoding.capture.constant;

/**
 * Redis Stream
 *
 * @author lazecoding
 */
public enum StreamConstant {

    /**
     * log record
     */
    LOG_RECORD_STREAM("log-record-stream", "log-record-group", "log-record-consumer");

    private StreamConstant(String stream, String group, String consumer) {
        this.stream = stream;
        this.group = group;
        this.consumer = consumer;
    }

    /**
     * stream
     */
    private String stream;

    /**
     * 消费者组
     */
    private String group;

    /**
     * 消费者
     */
    private String consumer;

    public String getStream() {
        return stream;
    }

    public String getGroup() {
        return group;
    }

    public String getConsumer() {
        return consumer;
    }
}
