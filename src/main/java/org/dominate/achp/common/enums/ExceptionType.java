package org.dominate.achp.common.enums;

/**
 * 异常枚举
 *
 * @author dominate
 * @since 2022/07/11
 */
public enum ExceptionType {

    /**
     * 自定义异常
     */
    UNKNOWN(ResponseType.FAILED),

    INVALID_TOKEN(ResponseType.INVALID_TOKEN),
    LOGIN_STATE_ERROR(ResponseType.LOGIN_STATUS_ERROR),

    PARAM_ERROR(ResponseType.HAS_WRONG_PARAM),

    ERROR(ResponseType.ERROR.getCode(), "请求异常"),

    HAS_CARD_BINDING(ResponseType.ERROR.getCode(), "存在已绑定的会员卡"),
    NOT_FOUND_CARD(ResponseType.ERROR.getCode(), "无效的会员卡"),
    NOT_CARD_USING(ResponseType.ERROR.getCode(), "没有生效的会员卡"),
    USER_INFO_EXISTED(ResponseType.ERROR.getCode(), "用户信息已存在"),
    SEND_SMS_ERROR(ResponseType.ERROR.getCode(), "验证码发送失败，可能手机号已到发送限制"),
    NOT_BIND_PHONE(ResponseType.ERROR.getCode(), "未绑定手机号"),

    PAY_ORDER_TYPE_ERROR(ResponseType.ERROR.getCode(), "支付订单对应支付类型错误"),
    PAY_ORDER_NOT_FOUND(ResponseType.ERROR.getCode(), "支付订单号错误"),
    PAY_ORDER_MUST_SAME_USER(ResponseType.ERROR.getCode(), "支付订单号必须是下单用户确认"),

    PAY_NOT_FOUND_ORDER(ResponseType.ERROR.getCode(), "无此订单信息"),
    PAY_NOT_COMPLETED(ResponseType.ERROR.getCode(), "支付未完成"),
    PAY_PRICE_ERROR(ResponseType.ERROR.getCode(), "支付金额错误"),


    ;

    final int code;
    final String message;

    ExceptionType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ExceptionType(ResponseType responseType) {
        this.code = responseType.getCode();
        this.message = responseType.getMsg();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
