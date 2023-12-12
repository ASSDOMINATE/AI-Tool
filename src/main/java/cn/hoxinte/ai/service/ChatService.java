package cn.hoxinte.ai.service;

import cn.hoxinte.ai.entity.dto.ChatDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Chat 服务类
 *
 * @author dominate
 * @since 2023-04-03
 */
public interface ChatService {

    /**
     * 开始会话
     *
     * @param chat 对话参数
     * @return SseEmitter
     */
    SseEmitter startChat(ChatDTO chat);

    /**
     * 提问
     *
     * @param chatDTO 对话参数
     * @return 回复
     */
    String question(ChatDTO chatDTO);

}
