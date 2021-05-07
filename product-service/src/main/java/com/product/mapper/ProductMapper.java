package com.product.mapper;

import com.product.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 龙三
 * @since 2021-04-09
 */
public interface ProductMapper extends BaseMapper<ProductDO> {
    /**
     * 锁定商品库存
     * @param productId
     * @param buyNum
     * @return
     */
    int lockProductStock(@Param("productId") long productId, @Param("buyNum") int buyNum);

    /**
     * 解锁商品存储
     * @param productId
     * @param buyNum
     */
    void unlockProductStock(@Param("productId")Long productId, @Param("buyNum")Integer buyNum);
}
