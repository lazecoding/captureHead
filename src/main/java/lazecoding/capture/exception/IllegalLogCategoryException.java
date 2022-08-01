package lazecoding.capture.exception;

/**
 * 不合法的日志类型
 *
 * @author lazecoding
 */
public class IllegalLogCategoryException extends RuntimeException {
    public IllegalLogCategoryException(String msg) {
        super(msg);
    }

    public IllegalLogCategoryException() {
        super("不合法的日志类型");
    }
}
