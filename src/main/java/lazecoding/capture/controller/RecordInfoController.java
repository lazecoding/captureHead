package lazecoding.capture.controller;

import lazecoding.capture.exception.NilParamException;
import lazecoding.capture.model.RecordInfo;
import lazecoding.capture.mvc.ResultBean;
import lazecoding.capture.search.RecordInfoSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RecordInfoController
 *
 * @author lazecoding
 */
@RestController
public class RecordInfoController {

    @Autowired
    private RecordInfoSearch recordInfoSearch;

    private static final Logger logger = LoggerFactory.getLogger(RecordInfoController.class);

    /**
     * add
     */
    @RequestMapping(value = "/api/project/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean add(@RequestBody RecordInfo recordInfo) {
        if (ObjectUtils.isEmpty(recordInfo)) {
            throw new NilParamException("RecordInfo is nil");
        }
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        try {
            isSuccess = recordInfoSearch.add(recordInfo);
            message = "获取成功";
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/project/add] 获取失败", e);
            message = "系统异常，获取失败";
        }
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

    /**
     * add
     */
    @RequestMapping(value = "/api/project/batch", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean batch(@RequestBody List<RecordInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new NilParamException("list is nil");
        }
        boolean isSuccess = false;
        ResultBean resultBean = new ResultBean();
        String message = "";
        try {
            isSuccess = recordInfoSearch.batch(list);
            message = "获取成功";
        } catch (Exception e) {
            isSuccess = false;
            logger.error("接口:[/api/project/batch] 获取失败", e);
            message = "系统异常，获取失败";
        }
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

}
