package lazecoding.capture.model;

/**
 * LogModel
 *
 * @author lazecoding
 */
public class LogModel {

    /**
     * 错误分类
     */
    private String category = "";

    /**
     * 错误级别
     */
    private String level = "";

    /**
     * 错误信息
     */
    private String logInfo = "";

    /**
     * 时间戳
     */
    private Long ctime = 0L;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return "LogModel{" +
                "category='" + category + '\'' +
                ", level='" + level + '\'' +
                ", logInfo='" + logInfo + '\'' +
                ", ctime=" + ctime +
                '}';
    }
}
