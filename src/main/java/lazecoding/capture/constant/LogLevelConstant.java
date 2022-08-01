package lazecoding.capture.constant;

import lazecoding.capture.exception.IllegalLogLevelException;

/**
 * 日志级别常量
 *
 * @author lazecoding
 */
public enum LogLevelConstant {

    /**
     * ERROR
     */
    ERROR("ERROR", 1, "ERROR"),

    /**
     * WARM
     */
    WARM("WARM", 2, "WARM"),

    /**
     * INFO
     */
    INFO("INFO", 3, "INFO"),

    /**
     * DEBUG
     */
    DEBUG("DEBUG", 4, "DEBUG"),

    /**
     * ALL(查询用，不可写入)
     */
    ALL("ALL", 5, "ALL");

    /**
     * 级别名称
     */
    private String name;

    /**
     * 优先级
     */
    private int order;

    /**
     * 描述
     */
    private String desc;

    private LogLevelConstant(String name, int order, String desc) {
        this.name = name;
        this.order = order;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getOrder() {
        return order;
    }

    /**
     * 写入过滤 （写入不能写入 ALL）
     *
     * @param levelName 日志级别
     * @throws IllegalLogLevelException
     */
    public static boolean writeFilter(String levelName) {
        if (LogLevelConstant.ERROR.getName().equals(levelName)
                || LogLevelConstant.WARM.getName().equals(levelName)
                || LogLevelConstant.INFO.getName().equals(levelName)
                || LogLevelConstant.DEBUG.getName().equals(levelName)) {
            return true;
        }
        throw new IllegalLogLevelException();
    }

    /**
     * 读取类型过滤
     *
     * @param levelName 日志级别
     * @throws IllegalLogLevelException
     */
    public static boolean readFilter(String levelName) {
        for (LogLevelConstant item : LogLevelConstant.values()) {
            if (item.name.equals(levelName)) {
                return true;
            }
        }
        throw new IllegalLogLevelException();
    }

    /**
     * 获取 LevelOrder
     *
     * @param levelName 日志级别
     * @throws IllegalLogLevelException
     */
    public static int getLevelOrder(String levelName) {
        for (LogLevelConstant item : LogLevelConstant.values()) {
            if (item.name.equals(levelName)) {
                return item.order;
            }
        }
        throw new IllegalLogLevelException();
    }

}
