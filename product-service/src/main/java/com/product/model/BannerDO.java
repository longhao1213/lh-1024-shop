package com.product.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 龙三
 * @since 2021-04-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banner")
public class BannerDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 图
	片
     */
    private String img;

    /**
     * 跳转
	地址
     */
    private String url;

    /**
     * 权重
     */
    private Integer weight;


}
