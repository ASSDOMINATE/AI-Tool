package org.dominate.achp.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FreqUtil {

    /**
     * ApiKey 频率记录 Map
     * key ApiKey ,value 次数
     */
    private static final LinkedHashMap<String, Integer> API_KEY_FREQ_RECORD_MAP = new LinkedHashMap<>();
    /**
     * ApiKey 分钟频率限制
     */
    private static final int API_KEY_MINTER_FREQ = 300;
    /**
     * ApiKey 释放时间秒数
     */
    private static final int API_KEY_RELEASE_SECOND = 60;
    /**
     * ApiKey 请求频率的等待时间毫秒数
     */
    private static final long API_KEY_FREQ_SLEEP_MILLIS = 1000 * 5;
    /**
     * ApiKey 请求频率的等待次数
     */
    private static final int API_KEY_WAIT_FREQ_TIMES = 2;

    /**
     * 等待请求频率可用
     * <p>
     * 如果该ApiKey频率未达限制直接返回 true
     * 如果达到限制会进行一定时间等待，等待超过设定时间返回 false
     *
     * @param apiKey ApiKey
     * @return 是否可用
     */
    public static synchronized boolean waitFreqForApiKey(String apiKey) {
        int freq = API_KEY_FREQ_RECORD_MAP.getOrDefault(apiKey, 0);
        int tryCount = 0;
        while (freq >= API_KEY_MINTER_FREQ) {
            tryCount++;
            log.info("ApiKey {} ，请求数量 {}，达到请求频率限制,", apiKey, freq);
            if (tryCount == API_KEY_WAIT_FREQ_TIMES) {
                log.info("ApiKey {} ，请求数量 {} 达到到重试上限", apiKey, freq);
                return false;
            }
            try {
                Thread.sleep(API_KEY_FREQ_SLEEP_MILLIS);
            } catch (InterruptedException e) {
                return false;
            }
            freq = API_KEY_FREQ_RECORD_MAP.getOrDefault(apiKey, 0);
        }
        API_KEY_FREQ_RECORD_MAP.put(apiKey, freq + 1);
        return true;
    }

    /**
     * 释放Apikey
     * 在使用完后执行，将在频率设定时间后执行释放次数
     *
     * @param apiKey ApiKey
     */
    public static synchronized void releaseApiKey(String apiKey) {
        if (!API_KEY_FREQ_RECORD_MAP.containsKey(apiKey)) {
            return;
        }
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            int freq = API_KEY_FREQ_RECORD_MAP.getOrDefault(apiKey, 0);
            if (freq > 0) {
                API_KEY_FREQ_RECORD_MAP.put(apiKey, freq - 1);
            }
        }, API_KEY_RELEASE_SECOND, TimeUnit.SECONDS);
    }
}
