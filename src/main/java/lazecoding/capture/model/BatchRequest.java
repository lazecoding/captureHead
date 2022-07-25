package lazecoding.capture.model;

import java.util.List;

/**
 * 批量请求
 *
 * @author lazecoding
 */
public class BatchRequest {

    /**
     * 日志信息列表
     */
    private List<LogModel> logModelList;

    /**
     * 应用信息
     */
    private AppModel appModel;

    public List<LogModel> getLogModelList() {
        return logModelList;
    }

    public void setLogModelList(List<LogModel> logModelList) {
        this.logModelList = logModelList;
    }

    public AppModel getAppModel() {
        return appModel;
    }

    public void setAppModel(AppModel appModel) {
        this.appModel = appModel;
    }

    @Override
    public String toString() {
        return "BatchRequest{" +
                "logModelList=" + logModelList +
                ", appModel=" + appModel +
                '}';
    }
}
