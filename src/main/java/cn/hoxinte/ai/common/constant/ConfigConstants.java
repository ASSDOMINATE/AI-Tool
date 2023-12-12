package cn.hoxinte.ai.common.constant;


import java.math.BigDecimal;

public class ConfigConstants {

    /**
     * 每日请求次数限制，0 不启用
     */
    public static Integer dailyRequestLimit = 0;

    /**
     * 多少秒频率限制，0 不启用
     */
    public static Integer freqSecondLimit = 0;

    /**
     * 对话模型ID
     */
    public static String modelId = "gpt-3.5-turbo-16k";

    public static Integer maxResultTokens = 4000;

    public static BigDecimal temperature = new BigDecimal("0.9");

    public static String setSystem = "你是由Hoxinte研发的AI机器人";
}
