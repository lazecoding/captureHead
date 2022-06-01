package lazecoding.capture.search;

import lazecoding.capture.constant.IndexConstant;
import lazecoding.capture.model.RecordInfo;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecordInfoSearch
 *
 * @author lazecoding
 */
@Service
public class RecordInfoSearch {

    private static final Logger logger = LoggerFactory.getLogger(RecordInfoSearch.class);

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * create index
     *
     * @return
     */
    public boolean createIndexIfNil() {
        String index = IndexConstant.RECORD_INFO.getIndex();
        boolean isExist = false;
        try {
            isExist = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("exists index:[" + index + "] error!", e);
            return false;
        }

        // 如果存在，直接创建
        if (isExist) {
            logger.info("index:[{}] 已存在！", index);
            return true;
        }

        boolean isSuccess = false;
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);

        // mapping
        createIndexRequest.mapping(IndexConstant.RECORD_INFO.getSource(), XContentType.JSON);

        // setting
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );

        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("create index:[" + index + "] error!", e);
            return false;
        }
        isSuccess = createIndexResponse.isAcknowledged();
        if (isSuccess) {
            logger.info("create index:[{}] success!", index);
        } else {
            logger.info("create index:[{}] error!", index);
        }
        return isSuccess;
    }

    /**
     * delete index
     *
     * @return
     */
    public boolean deleteIndex() {
        String index = IndexConstant.RECORD_INFO.getIndex();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse acknowledgedResponse = null;
        try {
            acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("delete index:[" + index + "] error!", e);
            return false;
        }
        boolean isSuccess = acknowledgedResponse.isAcknowledged();
        if (isSuccess) {
            logger.info("delete index:[{}] error!", index);
        } else {
            logger.info("delete index:[{}] error!", index);
        }
        return isSuccess;
    }

    /**
     * add doc to index
     *
     * @param recordInfo
     * @return
     */
    public boolean add(RecordInfo recordInfo) {
        if (ObjectUtils.isEmpty(recordInfo)) {
            return false;
        }
        String index = IndexConstant.RECORD_INFO.getIndex();
        Map<String, Object> source = conversionRecordToMap(recordInfo);
        IndexRequest indexRequest = new IndexRequest(index).source(source);

        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("add doc to index:[" + index + "] error!", e);
            return false;
        } catch (ElasticsearchException e) {
            logger.error("add doc to index:[" + index + "] error!", e);
            return false;
        }
        return true;
    }

    /**
     * 把实体转化为 Map
     *
     * @return
     */
    private Map<String, Object> conversionRecordToMap(RecordInfo recordInfo) {
        Map<String, Object> source = new HashMap<>();
        source.put("category", recordInfo.getCategory());
        source.put("level", recordInfo.getLevel());
        source.put("logInfo", recordInfo.getLogInfo());
        source.put("deviceInfo", recordInfo.getDeviceInfo());
        source.put("ctime", recordInfo.getCtime());
        source.put("app", recordInfo.getApp());
        source.put("version", recordInfo.getVersion());
        source.put("namespace", recordInfo.getNamespace());
        return source;
    }

    /**
     * batch doc to index
     *
     * @return
     */
    public boolean batch(List<RecordInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        String index = IndexConstant.RECORD_INFO.getIndex();
        BulkRequest bulkRequest = new BulkRequest();
        for (RecordInfo recordInfo : list) {
            Map<String, Object> source = conversionRecordToMap(recordInfo);
            IndexRequest indexRequest = new IndexRequest(index).source(source);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkItemResponses = null;
        try {
            bulkItemResponses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("batch add doc to index:[" + index + "] error!", e);
            return false;
        } catch (ElasticsearchException e) {
            logger.error("batch add doc to index:[" + index + "] error!", e);
            return false;
        }
        return true;
    }

    /**
     * search
     *
     * @return
     */
    public SearchHit[] search() {
        String index = IndexConstant.RECORD_INFO.getIndex();
        // search
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SearchHits hits = searchResponse.getHits();
        return hits.getHits();
    }
}
