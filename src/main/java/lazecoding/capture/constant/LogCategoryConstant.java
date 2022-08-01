package lazecoding.capture.constant;

import lazecoding.capture.exception.IllegalLogCategoryException;
import lazecoding.capture.exception.IllegalLogLevelException;

/**
 * 日志分类常量
 *
 * @author lazecoding
 */

public enum LogCategoryConstant {
    /**
     * js 错误
     */
    JS_ERROR("js_error", "js 错误"),

    /**
     * 资源引用错误
     */
    RESOURCE_ERROR("resource_error", "资源引用错误"),

    /**
     * Vue 错误
     */
    VUE_ERROR("vue_error", "Vue 错误"),

    /**
     * promise 错误
     */
    PROMISE_ERROR("promise_error", "promise 错误"),

    /**
     * ajax 异步请求错误
     */
    AJAX_ERROR("ajax_error", "ajax 异步请求错误"),

    /**
     * 控制台错误 console.info
     */
    CONSOLE_info("console_info", "控制台错误 console.info"),

    /**
     * 控制台错误 console.warn
     */
    CONSOLE_warn("console_warn", "控制台错误 console.warn"),

    /**
     * 控制台错误 console.error
     */
    CONSOLE_ERROR("console_error", "控制台错误 console.error"),

    /**
     * 跨域 js 错误
     */
    CROSS_SRCIPT_ERROR("cross_srcipt_error", "跨域 js 错误"),

    /**
     * 未知异常
     */
    UNKNOW_ERROR("unknow_error", "未知异常"),

    /**
     * 性能上报
     */
    PERFORMANCE("performance", "性能上报"),

    /**
     * 网速上报
     */
    NETWORK_SPEED("network_speed", "网速上报");

    private LogCategoryConstant(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * 分类名称
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

    /**
     * 不在枚举类型中，统称 other
     */
    private static final String OTHER = "other";

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 写入类型过滤（不在枚举中的，都作为 other）
     *
     * @param categoryName 日志类型
     * @throws IllegalLogLevelException
     */
    public static String writeFilter(String categoryName) {
        for (LogCategoryConstant item : LogCategoryConstant.values()) {
            if (item.name.equals(categoryName)) {
                return categoryName;
            }
        }
        return OTHER;
    }

    /**
     * 读取类型过滤
     *
     * @param categoryName 日志类型
     * @throws IllegalLogLevelException
     */
    public static boolean readFilter(String categoryName) {
        for (LogCategoryConstant item : LogCategoryConstant.values()) {
            if (item.name.equals(categoryName)) {
                return true;
            }
        }
        if (OTHER.equals(categoryName)){
            return true;
        }
        throw new IllegalLogCategoryException();
    }


}
