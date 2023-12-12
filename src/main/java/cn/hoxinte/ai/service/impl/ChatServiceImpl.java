package cn.hoxinte.ai.service.impl;

import cn.hoxinte.ai.common.enums.ChatFailedType;
import cn.hoxinte.ai.common.helper.ChatGptHelper;
import cn.hoxinte.ai.common.utils.FreqUtil;
import cn.hoxinte.ai.common.utils.UniqueCodeUtil;
import cn.hoxinte.ai.entity.dto.ChatDTO;
import cn.hoxinte.ai.entity.dto.ReplyDTO;
import cn.hoxinte.ai.service.ChatService;
import cn.hoxinte.ai.sys.ChatSseEmitter;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


/**
 * Chat 服务类实现
 *
 * @author dominate
 * @since 2023-04-03
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Override
    public SseEmitter startChat(ChatDTO chat) {
        if (StringUtils.isBlank(chat.getChatGroupId())) {
            chat.setChatGroupId(UniqueCodeUtil.createChatId());
        }
        ChatSseEmitter sseEmitter = new ChatSseEmitter(0L);
        // 启用线程池发送消息
        CompletableFuture.runAsync(() -> send(sseEmitter, chat), Executors.newCachedThreadPool());
        return sseEmitter;
    }

    @Override
    public String question(ChatDTO chatDTO) {
        return ChatGptHelper.send(chatDTO.getSentence(), Collections.emptyList()).getReply();
    }

    private void send(ChatSseEmitter sseEmitter, ChatDTO chat) {
        // 1.发送 -> 开始标记
        sendStartSign(chat, sseEmitter);
        // 2.获取当前最合适的 Api-Key  from DB or Cache
        // 3.等待 Api-key 的频率
        if (!FreqUtil.waitFreqForApiKey(chat.getApiKey())) {
            // 等待后仍然超过频率限制
            try {
                sseEmitter.send(ChatGptHelper.createMessage(ChatFailedType.MODEL_OVERLOADED.getResult(), false));
            } catch (IOException e) {
                log.error("ChatService.sendFreqLimit Client SSE is closed => {}", e.getMessage());
            } finally {
                sseEmitter.complete();
            }
            return;
        }
        int sendToken = 0;
        try {
            // 4.发送 -> ChatGPT返回消息，时间较长
            ReplyDTO reply = ChatGptHelper.send(sseEmitter, chat);
            sendToken = reply.getSendTokens();
        } catch (Exception e) {
            log.error("ChatService.startChat send error => {}", e.getMessage());
            String errorMessage = ChatFailedType.parseSign(e.getMessage());
            try {
                // 把ChatGPT的报错消息发送到前端
                sseEmitter.send(ChatGptHelper.createMessage(errorMessage, false));
            } catch (IOException ex) {
                log.error("ChatService.sendError Client SSE is closed => {}", errorMessage);
            }
        } finally {
            // 5.增加 ApiKey 的频率
            FreqUtil.addFreqApiKey(chat.getApiKey(), sendToken);
            sseEmitter.complete();
        }
    }

    private void sendStartSign(ChatDTO chat, ChatSseEmitter sseEmitter) {
        try {
            ChatMessage[] messages = ChatGptHelper.createStartMessages(chat);
            for (ChatMessage message : messages) {
                sseEmitter.send(message);
            }
        } catch (IOException e) {
            log.error("ChatService.sendStartSign Client SSE is closed => {}", e.getMessage());
        }
    }

}
