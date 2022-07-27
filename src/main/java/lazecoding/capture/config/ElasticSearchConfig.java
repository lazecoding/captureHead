package lazecoding.capture.config;

import lazecoding.capture.model.Address;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * ElasticSearchConfig
 *
 * @author lazecoding
 */
@Configuration
public class ElasticSearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    private static final String scheme = "http";

    public static final RequestOptions COMMON_OPTIONS;

    @Resource
    private ProjectSearchConfig projectSearchConfig;

    static {
        // RequestOptions 类保存了请求的部分，这些部分应该在同一个应用程序中的许多请求之间共享。
        // 创建一个 singqleton 实例，并在所有请求之间共享它。可以设置请求头之类的一些配置
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 *1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        if (ObjectUtils.isEmpty(projectSearchConfig)) {
            logger.info("project.search is nil");
            return null;
        }
        List<Address> addresses = projectSearchConfig.getAddresses();
        if (CollectionUtils.isEmpty(addresses)) {
            logger.info("project.search.addresses is nil");
            return null;
        }
        HttpHost[] hosts = new HttpHost[addresses.size()];
        int index = 0;
        for (Address address : addresses) {
            hosts[index++] = new HttpHost(address.getIp(), address.getPort(), scheme);
        }

        RestClientBuilder builder = RestClient.builder(hosts);
        CredentialsProvider credentialsProvider = null;
        // 账号密码
        if (StringUtils.hasText(projectSearchConfig.getUsername()) && StringUtils.hasText(projectSearchConfig.getPassword())) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(projectSearchConfig.getUsername(), projectSearchConfig.getPassword()));
        }

        // 构建器
        CredentialsProvider finalCredentialsProvider = credentialsProvider;
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.disableAuthCaching();
                if (!ObjectUtils.isEmpty(finalCredentialsProvider)){
                    httpClientBuilder.setDefaultCredentialsProvider(finalCredentialsProvider);
                }
                return httpClientBuilder;
            }
        });

        return new RestHighLevelClient(builder);
    }

}
