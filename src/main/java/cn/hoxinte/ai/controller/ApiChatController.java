package cn.hoxinte.ai.controller;

import cn.hoxinte.ai.common.cache.ChatCache;
import cn.hoxinte.ai.entity.dto.ChatDTO;
import cn.hoxinte.ai.entity.req.PreSendReq;
import cn.hoxinte.ai.service.ChatService;
import cn.hoxinte.ai.sys.ChatSseEmitter;
import cn.hoxinte.ai.sys.Response;
import cn.hoxinte.tool.utils.RandomUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 对话相关接口
 *
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ApiChatController {

    private final ChatService chatService;


    /**
     * 发送对话内容
     * Stream 结果返回
     *
     * @return SseEmitter SSE接收器
     */
    @GetMapping(path = "send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter send(
            @RequestParam(name = "account_id", required = false) Integer accountId,
            @RequestParam(name = "chat_id", required = false) String chatId,
            @RequestParam(name = "api_key", required = false) String apiKey,
            @RequestParam(name = "sentence") String sentence) {
        // 用户发消息限制检查
        ChatDTO chat = new ChatDTO(chatId, sentence, apiKey);
        chat.setAccountId(accountId);
        // 记录请求次数
        return chatService.startChat(chat);
    }


    @PostMapping(path = "preSend")
    @ResponseBody
    public Response<String> preSend(
            @Validated @RequestBody PreSendReq preSendReq
    ) {
        // 用户发消息限制检查
        ChatDTO chat = new ChatDTO(preSendReq);
        return Response.data(ChatCache.saveChatSendTemp(chat));
    }

    @GetMapping(path = "sendByPre", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendByPre(
            @RequestParam(name = "key") String key
    ) {
        ChatDTO chat = ChatCache.getChatSend(key);
        return chatService.startChat(chat);
    }



    // 调试用接口

    @GetMapping(path = "question")
    @ResponseBody
    public Response<String> question(
            @RequestParam(name = "chat_id", required = false) String chatId,
            @RequestParam(name = "sentence") String sentence) {
        ChatDTO chat = new ChatDTO(chatId, sentence);
        return Response.data(chatService.question(chat));
    }


    private static final Map<String, SseEmitter> SSE_CACHE_MAP = new HashMap<>();

    @GetMapping(path = "clearSSE")
    @ResponseBody
    public Response<String> clearSSE() {
        Iterator<Map.Entry<String, SseEmitter>> iterator = SSE_CACHE_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().complete();
            iterator.remove();
        }
        return Response.success();
    }

    @GetMapping(path = "sendSSE")
    @ResponseBody
    public Response<String> sendSSE(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "content", required = false) String content
    ) {
        try {
            SSE_CACHE_MAP.get(key).send(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.success();
    }

    @GetMapping(path = "getSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getSSE() {
        ChatSseEmitter sseEmitter = new ChatSseEmitter(0L);
        String key = RandomUtil.getStringRandom(8);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
                sseEmitter.send(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
        SSE_CACHE_MAP.put(key, sseEmitter);
        return sseEmitter;
    }
}
