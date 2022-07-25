package lazecoding.capture.constant;

/**
 * IndexConstant
 *
 * @author lazecoding
 */
public enum IndexConstant {

    /**
     * 记录信息
     */
    LOG_RECORD("log_record", "记录信息",
            "{\n" +
                    "  \"properties\": {\n" +
                    "    \"category\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    },\n" +
                    "    \"level\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    },\n" +
                    "    \"logInfo\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    },\n" +
                    "    \"ctime\": {\n" +
                    "      \"type\": \"long\"\n" +
                    "    },\n" +
                    "    \"app\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    },\n" +
                    "    \"version\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    },\n" +
                    "    \"deviceInfo\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    },\n" +
                    "    \"clientId\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    },\n" +
                    "    \"namespace\": {\n" +
                    "      \"type\": \"keyword\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}");
    private String index;

    private String desc;

    private String source;

    IndexConstant(String index, String desc, String source) {
        this.index = index;
        this.desc = desc;
        this.source = source;
    }

    public String getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }

    public String getSource() {
        return source;
    }
}
