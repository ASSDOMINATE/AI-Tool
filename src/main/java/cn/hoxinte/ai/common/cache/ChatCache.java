package cn.hoxinte.ai.common.cache;

import cn.hoxinte.ai.entity.dto.ChatDTO;
import cn.hoxinte.ai.entity.dto.ContentDTO;
import cn.hoxinte.ai.entity.dto.GroupCacheDTO;
import cn.hoxinte.tool.clients.redis.RedisClient;
import cn.hoxinte.tool.utils.RandomUtil;
import cn.hoxinte.tool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * 对话缓存工具
 *
 * @author dominate
 * @since 2023-04-27
 */
@Slf4j
public final class ChatCache {

    /**
     * 临时缓存
     * 卡密列表
     */
    private static final String TEMP_CARD_KEY_LIST = "temp:card:key:list";

    /**
     * 临时缓存
     * 对话发送内容 拼接头部+Key
     */
    private static final String TEMP_CHAT_SEND_CACHE_HEAD = "temp:chat:send:cache:";

    /**
     * 临时缓存
     * 对话组 拼接头部+对话组ID
     */
    private static final String TEMP_CHAT_GROUP_CACHE_HEAD = "temp:chat:group:cache:";

    /**
     * 临时缓存
     * 对话场景 拼接头部+主健ID
     */
    private static final String TEMP_CHAT_SCENE_DB_CACHE = "temp:chat:scene:db:cache:";


    private static final int[] TEMP_OUT_TIME = {60 * 3, 60 * 6};



    /**
     * 保存对话组内容 临时缓存
     *
     * @param groupCache 对话组内容
     */
    public static void saveChatGroupTemp(GroupCacheDTO groupCache) {
        RedisClient.set(TEMP_CHAT_GROUP_CACHE_HEAD + groupCache.getId(), groupCache,
                RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
    }

    /**
     * 保存对话内容 临时缓存
     *
     * @param groupId 对话组ID
     * @param content 对话内容
     */
    public static void saveChatGroupContentTemp(String groupId, ContentDTO content) {
        GroupCacheDTO groupCache = getChatGroup(groupId);
        if (null == groupCache) {
            groupCache = new GroupCacheDTO();
            groupCache.setId(groupId);
            groupCache.setContentList(new ArrayList<>());
        }
        groupCache.getContentList().add(content);
        saveChatGroupTemp(groupCache);
    }

    /**
     * 获取对话组内容
     *
     * @param groupId 对话组ID
     * @return 对话组内容
     */
    public static GroupCacheDTO getChatGroup(String groupId) {
        if (!RedisClient.hasKey(TEMP_CHAT_GROUP_CACHE_HEAD + groupId)) {
            return null;
        }
        return RedisClient.get(TEMP_CHAT_GROUP_CACHE_HEAD + groupId, GroupCacheDTO.class);
    }


    /**
     * 保存对话发送内容 临时缓存
     *
     * @param chat 对话发送内容
     * @return 缓存Key
     */
    public static String saveChatSendTemp(ChatDTO chat) {
        String key = RandomUtil.createUniqueCode(24,chat.getChatGroupId());
        RedisClient.set(TEMP_CHAT_SEND_CACHE_HEAD + key, chat,
                RandomUtil.getRandNum(TEMP_OUT_TIME[0], TEMP_OUT_TIME[1]));
        return key;
    }

    /**
     * 获取对话发送内容
     *
     * @param key 缓存Key
     * @return 对话发送内容
     */
    public static ChatDTO getChatSend(String key) {
        if (!RedisClient.hasKey(TEMP_CHAT_SEND_CACHE_HEAD + key)) {
            return null;
        }
        ChatDTO chat = RedisClient.get(TEMP_CHAT_SEND_CACHE_HEAD + key, ChatDTO.class);
        RedisClient.removeKey(TEMP_CHAT_SEND_CACHE_HEAD + key);
        return chat;
    }

    private static <T> T getListTarget(String listKey, String hashTargetKey, Class<T> targetClass) {
        String field = RedisClient.rightPop(listKey, String.class);
        if (StringUtil.isEmpty(field)) {
            return null;
        }
        if (!RedisClient.hHasKey(hashTargetKey, field)) {
            return null;
        }
        return RedisClient.hGet(hashTargetKey, field, targetClass);
    }

}
