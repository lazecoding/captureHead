package lazecoding.capture.model;

import java.util.List;

/**
 * LogSearchResponse
 *
 * @author lazecoding
 */
public class LogSearchResponse {

    /**
     * Log 记录
     */
    private List<LogRecord> logRecordList;

    /**
     * 下一次查询时间戳
     */
    private Long nextQueryCtime;

    public LogSearchResponse() {
    }


    public LogSearchResponse(List<LogRecord> logRecordList, Long nextQueryCtime) {
        this.logRecordList = logRecordList;
        this.nextQueryCtime = nextQueryCtime;
    }

    public List<LogRecord> getLogRecordList() {
        return logRecordList;
    }

    public void setLogRecordList(List<LogRecord> logRecordList) {
        this.logRecordList = logRecordList;
    }


    public Long getNextQueryCtime() {
        return nextQueryCtime;
    }

    public void setNextQueryCtime(Long nextQueryCtime) {
        this.nextQueryCtime = nextQueryCtime;
    }

    @Override
    public String toString() {
        return "LogSearchResponse{" +
                "logRecordList=" + logRecordList +
                ", nextQueryCtime=" + nextQueryCtime +
                '}';
    }
}
