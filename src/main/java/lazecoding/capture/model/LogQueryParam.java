package lazecoding.capture.model;

/**
 * 日志查询参数
 *
 * @author lazecoding
 */
public class LogQueryParam {

    /**
     * 应用名
     */
    String app = "";

    /**
     * 应用版本号
     */
    String version = "";

    /**
     * 客户端 Id
     */
    String clientId = "";

    /**
     * namespace
     */
    String namespace = "";

    /**
     * 错误分类
     */
    private String category = "";

    /**
     * 错误级别
     */
    private String level = "";

    /**
     * 页面定位
     */
    private String location;

    /**
     * 创建时间戳
     */
    private Long ctime;

    /**
     * 排序方式： 0 时间倒序；1 时间正序
     */
    private Integer orderType = 0;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;


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

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LogQueryParam{" +
                "app='" + app + '\'' +
                ", version='" + version + '\'' +
                ", clientId='" + clientId + '\'' +
                ", namespace='" + namespace + '\'' +
                ", category='" + category + '\'' +
                ", level='" + level + '\'' +
                ", location='" + location + '\'' +
                ", ctime=" + ctime +
                ", orderType=" + orderType +
                ", pageSize=" + pageSize +
                '}';
    }
}
