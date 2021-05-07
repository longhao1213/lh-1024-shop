package com.order.mapper;

import com.order.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 龙三
 * @since 2021-04-21
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {

    void insertBatch(@Param("orderItemList") List<ProductOrderItemDO> list);
}
