package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.dto.PayOrderDTO;

/**
 * <p>
 * 支付记录 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBasePaymentRecordService extends IService<BasePaymentRecord> {

    /**
     * 保存支付信息
     *
     * @param payOrder 支付订单信息
     * @param card     支付卡密信息
     * @return 保存ID，返回0为保存失败
     */
    int save(PayOrderDTO payOrder, BaseCard card);

    boolean isUniqueOrder(PayOrderDTO payOrder);
}
