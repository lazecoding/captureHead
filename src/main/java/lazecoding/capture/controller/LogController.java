package lazecoding.capture.controller;

import lazecoding.capture.constant.StreamConstant;
import lazecoding.capture.exception.IllegalLogCategoryException;
import lazecoding.capture.exception.IllegalLogLevelException;
import lazecoding.capture.exception.NilParamException;
import lazecoding.capture.model.*;
import lazecoding.capture.mvc.ResultBean;
import lazecoding.capture.search.LogRecordSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * LogController
 *
 * @author lazecoding
 */
@RestController
public class LogController {

    @Autowired
    private LogRecordSearch logRecordSearch;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    /**
     * batch
     * <p>
     * https://juejin.cn/post/7029302992364896270
     * https://blog.csdn.net/qq_38688267/article/details/114920558
     */
    @RequestMapping(value = "/api/log/batch", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean batch(@RequestBody BatchRequest batchRequest) {
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        RecordId recordId = null;
        try {
            if (ObjectUtils.isEmpty(batchRequest)) {
                throw new NilParamException("BatchRequest is nil");
            }
            AppModel appModel = batchRequest.getAppModel();
            if (ObjectUtils.isEmpty(appModel)) {
                throw new NilParamException("AppModel is nil");
            }
            if (!StringUtils.hasText(appModel.getApp())){
                throw new NilParamException("AppModel:[app] is nil");
            }
            if (!StringUtils.hasText(appModel.getVersion())){
                appModel.setVersion("default");
            }
            if (!StringUtils.hasText(appModel.getNamespace())){
                appModel.setNamespace("default");
            }
            List<LogModel> logModelList = batchRequest.getLogModelList();
            if (CollectionUtils.isEmpty(logModelList)) {
                throw new NilParamException("LogModelList is nil");
            }
            ObjectRecord<String, BatchRequest> record = StreamRecords.newRecord()
                    .in(StreamConstant.LOG_RECORD_STREAM.getStream())
                    .ofObject(batchRequest)
                    .withId(RecordId.autoGenerate());
            recordId = redisTemplate.opsForStream().add(record);
            isSuccess = true;
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
        resultBean.setValue(recordId);
        return resultBean;
    }

    /**
     * 总数
     */
    @RequestMapping(value = "/api/log/count", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean count(LogQueryParam logQueryParam) {
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        Long count = 0L;
        try {
            count = logRecordSearch.count(logQueryParam);
            isSuccess = true;
        } catch (NilParamException | IllegalLogLevelException | IllegalLogCategoryException e) {
            isSuccess = false;
            logger.error("接口:[/api/log/count]", e);
            message = e.getMessage();
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/log/count]", e);
            message = "系统异常";
        }
        resultBean.setValue(count);
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }


    /**
     * 检索 临时接口
     * <p>
     * https://segmentfault.com/a/1190000016830796
     */
    @RequestMapping(value = "/api/log/search", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean search(LogQueryParam logQueryParam) {
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        LogSearchResponse logSearchResponse = new LogSearchResponse();
        try {
            logSearchResponse = logRecordSearch.search(logQueryParam);
            isSuccess = true;
        } catch (NilParamException | IllegalLogLevelException | IllegalLogCategoryException e) {
            isSuccess = false;
            logger.error("接口:[/api/log/search]", e);
            message = e.getMessage();
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/log/search]", e);
            message = "系统异常";
        }
        resultBean.setValue(logSearchResponse);
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

}
