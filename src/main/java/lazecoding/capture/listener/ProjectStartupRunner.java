package lazecoding.capture.listener;

import lazecoding.capture.search.RecordInfoSearch;
import lazecoding.capture.util.BeanUtil;
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

    @Autowired
    private RecordInfoSearch recordInfoSearch;

    @Override
    public void run(ApplicationArguments args) {
        recordInfoSearch.createIndexIfNil();
    }
}
