package cn.hoxinte.ai.entity.dto;

import cn.hoxinte.ai.common.constant.ConfigConstants;
import cn.hoxinte.ai.common.helper.ChatGptHelper;
import cn.hoxinte.ai.entity.req.PreSendReq;
import cn.hoxinte.tool.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 会话对象
 *
 * @author dominate
 * @since 2023-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对话组ID
     */
    private String chatGroupId;

    /**
     * 对话模型ID
     */
    private String modelId;

    private Integer maxResultTokens;

    private Double temperature;

    /**
     * 对话句子
     */
    private String sentence;

    /**
     * 系统设定
     */
    private String system;

    /**
     * 场景ID
     */
    private String apiKey;

    /**
     * 用户账号
     */
    private Integer accountId;

    private List<ContentDTO> contentList;

    public ChatDTO() {
    }

    public ChatDTO(String chatGroupId, String sentence) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.apiKey = StringUtil.EMPTY;
        this.accountId = 0;
        this.system = StringUtil.EMPTY;
        this.modelId = ChatGptHelper.DEFAULT_MODEL_ID;
        this.maxResultTokens = ChatGptHelper.DEFAULT_TOKENS;
        this.temperature = ChatGptHelper.DEFAULT_TEMPERATURE;
    }

    public ChatDTO(String chatGroupId, String sentence, String apiKey) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.apiKey = apiKey;
        this.accountId = 0;
        this.system = ConfigConstants.setSystem;
        this.modelId = ConfigConstants.modelId;
        this.maxResultTokens = ConfigConstants.maxResultTokens;
        this.temperature = ConfigConstants.temperature.doubleValue();
    }

    public ChatDTO(PreSendReq preSendReq) {
        this.chatGroupId = StringUtils.isEmpty(preSendReq.getChatId()) ? StringUtils.EMPTY : preSendReq.getChatId();
        this.apiKey = null == preSendReq.getApiKey() ? "" : preSendReq.getApiKey();
        this.sentence = preSendReq.getSentence();
        this.accountId = 0;
        this.system = ConfigConstants.setSystem;
        this.modelId = ConfigConstants.modelId;
        this.maxResultTokens = ConfigConstants.maxResultTokens;
        this.temperature = ConfigConstants.temperature.doubleValue();
        this.accountId = preSendReq.getAccountId();
    }
}
