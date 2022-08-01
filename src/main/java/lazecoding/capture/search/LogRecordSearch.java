package lazecoding.capture.search;

import lazecoding.capture.constant.IndexConstant;
import lazecoding.capture.constant.LogCategoryConstant;
import lazecoding.capture.constant.LogLevelConstant;
import lazecoding.capture.exception.IllegalLogLevelException;
import lazecoding.capture.exception.NilParamException;
import lazecoding.capture.model.*;
import lazecoding.capture.util.PageInfo;
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
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
        if (source == null) {
            return;
        }
        LogLevelConstant.writeFilter(logModel.getLevel());
        source.put("category", LogCategoryConstant.writeFilter(logModel.getCategory()));
        source.put("level", logModel.getLevel());
        source.put("levelOrder", LogLevelConstant.getLevelOrder(logModel.getLevel()));
        source.put("logInfo", logModel.getLogInfo());
        source.put("ctime", logModel.getCtime());
    }

    /**
     * batch doc to index
     *
     * @return
     */
    public boolean batch(BatchRequest batchRequest) {
        // 没有实际请求，则直接返回 true
        if (ObjectUtils.isEmpty(batchRequest)) {
            return true;
        }
        AppModel appModel = batchRequest.getAppModel();
        if (ObjectUtils.isEmpty(appModel)) {
            return true;
        }
        List<LogModel> logModelList = batchRequest.getLogModelList();
        if (CollectionUtils.isEmpty(logModelList)) {
            return true;
        }
        String index = IndexConstant.LOG_RECORD.getIndex();
        BulkRequest bulkRequest = new BulkRequest();
        Map<String, Object> source = null;
        for (LogModel logModel : logModelList) {
            source = new HashMap<>();
            try {
                conversionRecordToMap(logModel, source);
                source.put("deviceInfo", appModel.getDeviceInfo());
                source.put("clientId", appModel.getClientId());
                source.put("app", appModel.getApp());
                source.put("version", appModel.getVersion());
                source.put("namespace", appModel.getNamespace());
                IndexRequest indexRequest = new IndexRequest(index).source(source);
                bulkRequest.add(indexRequest);
            } catch (IllegalLogLevelException e) {
                // 日志级别不合法
                logger.debug("Log Write Index IllegalLogLevelException app:[{}],client:[{}],version:[{}],namespace:[{}] - level:[{}]"
                        , appModel.getApp(), appModel.getClientId(), appModel.getVersion(), appModel.getNamespace(), logModel.getLevel());
            }
        }

        // 没有实际请求，则直接返回 true
        if (bulkRequest.numberOfActions() == 0) {
            return true;
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
    public LogSearchResponse search(LogQueryParam logQueryParam) {
        String index = IndexConstant.LOG_RECORD.getIndex();
        if (ObjectUtils.isEmpty(logQueryParam)) {
            throw new NilParamException("LogQueryParam is nil");
        }
        // 处理 logQueryParam 默认值
        if (logQueryParam.getPageNum() == null) {
            logQueryParam.setPageNum(0);
        }
        if (logQueryParam.getPageSize() == null) {
            logQueryParam.setPageSize(20);
        }
        if (logQueryParam.getOrderType() == null) {
            logQueryParam.setOrderType(0);
        }

        // 组织 query
        // https://www.cnblogs.com/andywangit/p/15935974.html
        // https://www.cnblogs.com/tanghaorong/p/16344391.html
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.hasText(logQueryParam.getApp())) {
            queryBuilder.must(QueryBuilders.termQuery("app", logQueryParam.getApp()));
        }
        if (StringUtils.hasText(logQueryParam.getClientId())) {
            queryBuilder.must(QueryBuilders.termQuery("clientId", logQueryParam.getClientId()));
        }
        if (StringUtils.hasText(logQueryParam.getVersion())) {
            queryBuilder.must(QueryBuilders.termQuery("version", logQueryParam.getVersion()));
        }
        if (StringUtils.hasText(logQueryParam.getNamespace())) {
            queryBuilder.must(QueryBuilders.termQuery("namespace", logQueryParam.getNamespace()));
        }
        if (StringUtils.hasText(logQueryParam.getCategory())) {
            // 过滤日志分类
            LogCategoryConstant.readFilter(logQueryParam.getCategory());
            queryBuilder.must(QueryBuilders.termQuery("category", logQueryParam.getCategory()));
        }
        if (StringUtils.hasText(logQueryParam.getLevel())) {
            // 日志等级用 levelOrder 查询，需要小于等于这个日志级别
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("levelOrder").lte(LogLevelConstant.getLevelOrder(logQueryParam.getLevel()));
            queryBuilder.filter().add(rangeQueryBuilder);
        }
        if (logQueryParam.getStartTime() != null || logQueryParam.getEndTime() != null) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("ctime");
            if (logQueryParam.getStartTime() != null) {
                rangeQueryBuilder.gte(logQueryParam.getStartTime());
            }
            if (logQueryParam.getEndTime() != null) {
                rangeQueryBuilder.lte(logQueryParam.getEndTime());
            }
            queryBuilder.filter().add(rangeQueryBuilder);
        }

        // count 获取总数
        Long totolNum = 0L;
        try {
            CountRequest countRequest = new CountRequest(index);
            countRequest.query(queryBuilder);
            CountResponse count = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            totolNum = count.getCount();
        } catch (IOException e) {
            throw new RuntimeException("RestHighLevelClient Count ERROR");
        }
        PageInfo pageInfo = new PageInfo(logQueryParam.getPageNum(), logQueryParam.getPageSize(), totolNum.intValue());
        List<LogRecord> logRecordList = new LinkedList<>();
        LogSearchResponse logSearchResponse = new LogSearchResponse(logRecordList, pageInfo);

        // 如果没数据，直接返回
        if (totolNum == 0) {
            return logSearchResponse;
        }

        // search
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        // 翻页
        searchSourceBuilder.from(pageInfo.getStartNum()).size(pageInfo.getPageSize());
        // soft
        searchSourceBuilder.sort(new FieldSortBuilder("ctime").order(logQueryParam.getOrderType() == 1 ? SortOrder.ASC : SortOrder.DESC));
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("RestHighLevelClient Search ERROR");
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits != null && hits.length > 0) {
            LogRecord logRecord;
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                if (CollectionUtils.isEmpty(sourceAsMap)) {
                    continue;
                }
                logRecord = new LogRecord();
                logRecord.setApp((String) sourceAsMap.get("app"));
                logRecord.setDeviceInfo((String) sourceAsMap.get("deviceInfo"));
                logRecord.setClientId((String) sourceAsMap.get("clientId"));
                logRecord.setVersion((String) sourceAsMap.get("version"));
                logRecord.setNamespace((String) sourceAsMap.get("namespace"));
                logRecord.setCategory((String) sourceAsMap.get("category"));
                logRecord.setLevel((String) sourceAsMap.get("level"));
                logRecord.setLogInfo((String) sourceAsMap.get("logInfo"));
                Object ctime = (Object) sourceAsMap.get("ctime");
                if (ctime instanceof Long) {
                    logRecord.setCtime((Long) ctime);
                } else if (ctime instanceof Integer) {
                    logRecord.setCtime(((Integer) ctime).longValue());
                } else {
                    logRecord.setCtime(0L);
                }
                logRecordList.add(logRecord);
            }
        }
        return logSearchResponse;
    }
}
