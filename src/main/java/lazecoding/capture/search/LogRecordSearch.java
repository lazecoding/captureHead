package lazecoding.capture.search;

import lazecoding.capture.constant.IndexConstant;
import lazecoding.capture.exception.NilParamException;
import lazecoding.capture.model.AppModel;
import lazecoding.capture.model.BatchRequest;
import lazecoding.capture.model.LogModel;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * LogRecordSearch
 *
 * @author lazecoding
 */
@Service
public class LogRecordSearch {

    private static final Logger logger = LoggerFactory.getLogger(LogRecordSearch.class);

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * create index
     *
     * @return
     */
    public boolean createIndexIfNil() {
        String index = IndexConstant.LOG_RECORD.getIndex();
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
        createIndexRequest.mapping(IndexConstant.LOG_RECORD.getSource(), XContentType.JSON);

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
        String index = IndexConstant.LOG_RECORD.getIndex();
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
     * 把实体转化为 Map
     *
     * @return
     */
    private void conversionRecordToMap(LogModel logModel, Map<String, Object> source) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }
        source.put("category", logModel.getCategory());
        source.put("level", logModel.getLevel());
        source.put("logInfo", logModel.getLogInfo());
        source.put("ctime", logModel.getCtime());
    }

    /**
     * batch doc to index
     *
     * @return
     */
    public boolean batch(BatchRequest batchRequest) {
        if (ObjectUtils.isEmpty(batchRequest)) {
            throw new NilParamException("BatchRequest is nil");
        }
        AppModel appModel = batchRequest.getAppModel();
        if (ObjectUtils.isEmpty(appModel)) {
            throw new NilParamException("AppModel is nil");
        }
        List<LogModel> logModelList = batchRequest.getLogModelList();
        if (CollectionUtils.isEmpty(logModelList)) {
            throw new NilParamException("LogModelList is nil");
        }
        String index = IndexConstant.LOG_RECORD.getIndex();
        BulkRequest bulkRequest = new BulkRequest();
        Map<String, Object> source = null;
        for (LogModel logModel : logModelList) {
            source = new HashMap<>();
            source.put("deviceInfo", appModel.getDeviceInfo());
            source.put("clientId", appModel.getClientId());
            source.put("app", appModel.getApp());
            source.put("version", appModel.getVersion());
            source.put("namespace", appModel.getNamespace());
            conversionRecordToMap(logModel, source);
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
        String index = IndexConstant.LOG_RECORD.getIndex();
        // search
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort(new FieldSortBuilder("ctime").order(SortOrder.DESC));
        searchSourceBuilder.from(0).size(20);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
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
