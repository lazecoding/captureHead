package lazecoding.capture.model;

/**
 * AppModel
 *
 * @author lazecoding
 */
public class AppModel {

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

    @Override
    public String toString() {
        return "AppModel{" +
                "app='" + app + '\'' +
                ", version='" + version + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", clientId='" + clientId + '\'' +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
