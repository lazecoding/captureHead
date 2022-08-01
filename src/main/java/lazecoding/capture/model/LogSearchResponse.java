package lazecoding.capture.model;

import lazecoding.capture.util.PageInfo;

import java.util.List;

/**
 * LogSearchResponse
 *
 * @author lazecoding
 */
public class LogSearchResponse {

    private List<LogRecord> logRecordList;

    private PageInfo pageInfo;

    public LogSearchResponse() {
    }

    public LogSearchResponse(List<LogRecord> logRecordList, PageInfo pageInfo) {
        this.logRecordList = logRecordList;
        this.pageInfo = pageInfo;
    }

    public List<LogRecord> getLogRecordList() {
        return logRecordList;
    }

    public void setLogRecordList(List<LogRecord> logRecordList) {
        this.logRecordList = logRecordList;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public String toString() {
        return "LogSearchResponse{" +
                "logRecordList=" + logRecordList +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
