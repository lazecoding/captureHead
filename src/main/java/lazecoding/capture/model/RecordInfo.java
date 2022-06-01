package lazecoding.capture.model;

/**
 * 记录信息
 *
 * @author lazecoding
 */
public class RecordInfo {

    /**
     * 错误分类
     */
    String category = "";

    /**
     * 错误级别
     */
    String level = "";

    /**
     * 错误信息
     */
    String logInfo = "";

    /**
     * 设备信息
     */
    String deviceInfo = "";

    /**
     * 时间戳
     */
    long ctime = 0L;

    /**
     * 应用名
     */
    String app = "";

    /**
     * 应用版本号
     */
    String version = "default";

    /**
     * namespace
     */
    String namespace = "default";

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

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
