package lazecoding.capture.config;

import lazecoding.capture.model.Address;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 项目 ES 配置
 *
 * @author lazecoding
 */
@Configuration("projectSearchConfig")
@ConfigurationProperties(prefix = "project.search")
public class ProjectSearchConfig {

    private List<Address> addresses;

    private String username;

    private String password;

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ProjectSearchConfig{" +
                "addresses=" + addresses +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
