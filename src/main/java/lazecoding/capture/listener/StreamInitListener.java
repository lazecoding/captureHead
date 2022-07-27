package lazecoding.capture.listener;

import lazecoding.capture.constant.StreamConstant;
import lazecoding.capture.model.BatchRequest;
import lazecoding.capture.search.LogRecordSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * StreamInitListener
 *
 * @author lazecoding
 */
@Component
public class StreamInitListener implements ApplicationRunner, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(StreamInitListener.class);

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LogRecordSearch logRecordSearch;

    /**
     * 全局变量，为了 destroy 时销毁监听
     */
    private StreamMessageListenerContainer<String, ObjectRecord<String, BatchRequest>> streamMessageListenerContainer;

    @Override
    public void run(ApplicationArguments args) throws UnknownHostException {
        // 初始化 LogRecord
        this.initLogRecord();
    }

    @Override
    public void destroy() throws Exception {
        this.streamMessageListenerContainer.stop();
    }

    /**
     * LogRecord
     */
    private void initLogRecord() {
        logger.info("Init LogRecordStream Start");
        /**
         * 这里必须先判空，重复创建组会报错，获取不存在的 key 的组也会报错
         * 所以需要先判断是否存在 key，在判断是否存在组
         * 我这里只有一个组，如果需要创建多个组的话则需要改下逻辑
         */
        if (redisTemplate.hasKey(StreamConstant.LOG_RECORD_STREAM.getStream())) {
            StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(StreamConstant.LOG_RECORD_STREAM.getStream());
            if (groups.isEmpty()) {
                redisTemplate.opsForStream().createGroup(StreamConstant.LOG_RECORD_STREAM.getStream(), StreamConstant.LOG_RECORD_STREAM.getGroup());
                logger.info("初始化 log-record-stream 和 log-record-group");
            }
        } else {
            redisTemplate.opsForStream().createGroup(StreamConstant.LOG_RECORD_STREAM.getStream(), StreamConstant.LOG_RECORD_STREAM.getGroup());
            logger.info("初始化 log-record-stream");
        }

        // 异步线程执行器
        int processors = Math.max(4, Runtime.getRuntime().availableProcessors());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), r -> {
            Thread thread = new Thread(r);
            thread.setName("log-record-consumer");
            thread.setDaemon(true);
            return thread;
        }, new ThreadPoolExecutor.CallerRunsPolicy());

        // 创建配置对象
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, BatchRequest>> streamMessageListenerContainerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                // 一次性最多拉取多少条消息
                .batchSize(10)
                // 执行消息轮询的执行器
                .executor(executor)
                //  Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
                .pollTimeout(Duration.ZERO)
                // 序列化器
                .serializer(new StringRedisSerializer())
                // 将发送到Stream中的Record转换成ObjectRecord，转换成具体的类型是这个地方指定的类型
                .targetType(BatchRequest.class)
                .build();
        // 根据配置对象创建监听容器对象
        StreamMessageListenerContainer<String, ObjectRecord<String, BatchRequest>> streamMessageListenerContainer = StreamMessageListenerContainer
                .create(this.redisConnectionFactory, streamMessageListenerContainerOptions);

        // 通过 StreamListener 定义消费行为
        // StreamListener<String, ObjectRecord<String, BatchRequest>> listener = message -> {};

        // 使用监听容器对象开始监听消费
        streamMessageListenerContainer.receive(Consumer.from(StreamConstant.LOG_RECORD_STREAM.getGroup(), StreamConstant.LOG_RECORD_STREAM.getConsumer()),
                StreamOffset.create(StreamConstant.LOG_RECORD_STREAM.getStream(), ReadOffset.lastConsumed()), message -> {
                    String stream = message.getStream();
                    RecordId id = message.getId();
                    BatchRequest batchRequest = message.getValue();
                    logger.debug("log-record-consumer receive stream:[{}],id:[{}],value:[{}]", stream, id, batchRequest);
                    try {
                        // 业务处理
                        boolean isSuccess = logRecordSearch.batch(batchRequest);
                        if (isSuccess) {
                            // 手动 ACK
                            redisTemplate.opsForStream().acknowledge(stream, StreamConstant.LOG_RECORD_STREAM.getGroup(), id);
                            // 删除消费过的消息
                            redisTemplate.opsForStream().delete(StreamConstant.LOG_RECORD_STREAM.getStream(), id);
                            logger.debug("log-record-consumer cost success stream:[{}],id:[{}]", stream, id);
                        } else {
                            logger.debug("log-record-consumer cost fail stream:[{}],id:[{}]", stream, id);
                        }
                    } catch (Exception e) {
                        logger.debug("log-record-consumer cost fail stream:[{}],id:[{}]", stream, id);
                    }
                });
        // 初始化全局变量
        this.streamMessageListenerContainer = streamMessageListenerContainer;
        // 启动监听
        this.streamMessageListenerContainer.start();
        logger.info("Init LogRecordStream End");
    }
}

