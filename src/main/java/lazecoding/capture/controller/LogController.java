package lazecoding.capture.controller;

import lazecoding.capture.exception.NilParamException;
import lazecoding.capture.model.BatchRequest;
import lazecoding.capture.model.LogRecord;
import lazecoding.capture.mvc.ResultBean;
import lazecoding.capture.search.LogRecordSearch;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * LogController
 *
 * @author lazecoding
 */
@RestController
public class LogController {

    @Autowired
    private LogRecordSearch logRecordSearch;

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    /**
     * batch
     */
    @RequestMapping(value = "/api/log/batch", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean batch(@RequestBody BatchRequest batchRequest) {
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        try {
            isSuccess = logRecordSearch.batch(batchRequest);
        } catch (NilParamException e) {
            isSuccess = false;
            logger.error("接口:[/api/log/batch]", e);
            message = e.getMessage();
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/log/batch]", e);
            message = "系统异常";
        }
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

    /**
     * 检索 临时接口
     *
     * https://segmentfault.com/a/1190000016830796
     */
    @RequestMapping(value = "/api/log/search", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean search() {
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        SearchHit[] hits = null;
        List<LogRecord> logRecordList = new LinkedList<>();
        try {
            hits = logRecordSearch.search();
            if (hits != null && hits.length > 0) {
                LogRecord logRecord = null;
                for (SearchHit hit : hits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    if (CollectionUtils.isEmpty(sourceAsMap)) {
                        continue;
                    }
                    logRecord = new LogRecord();
                    logRecord.setApp((String) sourceAsMap.get("app"));
                    logRecord.setDeviceInfo((String) sourceAsMap.get("deviceInfo"));
                    logRecord.setClientId((String) sourceAsMap.get("clientId"));
                    logRecord.setVersion((String) sourceAsMap.get("version"));
                    logRecord.setNamespace((String) sourceAsMap.get("namespace"));
                    logRecord.setCategory((String) sourceAsMap.get("category"));
                    logRecord.setLevel((String) sourceAsMap.get("level"));
                    logRecord.setLogInfo((String) sourceAsMap.get("logInfo"));
                    Object ctime = (Object) sourceAsMap.get("ctime");
                    if (ctime instanceof Long) {
                        logRecord.setCtime((Long) ctime);
                    } else if (ctime instanceof Integer) {
                        logRecord.setCtime(((Integer) ctime).longValue());
                    } else {
                        logRecord.setCtime(0L);
                    }
                    logRecordList.add(logRecord);
                }
            }
            isSuccess = true;
        } catch (NilParamException e) {
            isSuccess = false;
            logger.error("接口:[/api/log/search]", e);
            message = e.getMessage();
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/log/search]", e);
            message = "系统异常";
        }
        resultBean.setValue(logRecordList);
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

}
