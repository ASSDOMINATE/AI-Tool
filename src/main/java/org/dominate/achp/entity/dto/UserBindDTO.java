package org.dominate.achp.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户三方绑定
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBindDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer accountId;

    /**
     * 绑定三方类型
     */
    private Integer bindType;

    /**
     * 三方绑定编码 最大长度128
     */
    private String bindCode;

    /**
     * 绑定原始信息
     */
    private String originData;


}
