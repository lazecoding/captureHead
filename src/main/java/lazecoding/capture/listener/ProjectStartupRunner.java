package lazecoding.capture.listener;

/**
 * @author lazecoding
 */

import lazecoding.capture.search.LogRecordSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统启动后执行
 *
 * @author lazecoding
 */
@Component
public class ProjectStartupRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProjectStartupRunner.class);

    @Autowired
    private LogRecordSearch logRecordSearch;

    @Override
    public void run(ApplicationArguments args) {
        try {
            logRecordSearch.createIndexIfNil();
        } catch (Exception e) {
            logger.error("ProjectStartupRunner Exception", e);
        }
    }
}

