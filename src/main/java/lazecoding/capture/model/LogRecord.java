package lazecoding.capture.model;

/**
 * 一条日志记录 (AppModel 和 LogModel 字段总集，用于返回结果)
 *
 * @author lazecoding
 */
public class LogRecord {

    /**
     * 应用名
     */
    String app = "";

    /**
     * 应用版本号
     */
    String version = "default";

    /**
     * 设备信息
     */
    String deviceInfo = "";

    /**
     * 客户端 Id
     */
    String clientId = "";

    /**
     * namespace
     */
    String namespace = "default";

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
     * 页面定位
     */
    private String location = "";

    /**
     * 时间戳
     */
    private Long ctime = 0L;

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

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "app='" + app + '\'' +
                ", version='" + version + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", clientId='" + clientId + '\'' +
                ", namespace='" + namespace + '\'' +
                ", category='" + category + '\'' +
                ", level='" + level + '\'' +
                ", logInfo='" + logInfo + '\'' +
                ", location='" + location + '\'' +
                ", ctime=" + ctime +
                '}';
    }
}
