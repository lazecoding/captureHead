package lazecoding.capture.exception;

/**
 * 不合法的日志级别
 *
 * @author lazecoding
 */
public class IllegalLogLevelException extends RuntimeException {
    public IllegalLogLevelException(String msg) {
        super(msg);
    }

    public IllegalLogLevelException() {
        super("不合法的日志级别");
    }
}
