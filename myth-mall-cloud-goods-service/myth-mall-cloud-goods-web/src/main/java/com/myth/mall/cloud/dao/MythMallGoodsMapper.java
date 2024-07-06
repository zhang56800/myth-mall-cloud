
package com.myth.mall.cloud.dao;

import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.entity.MythMallGoods;
import com.myth.mall.cloud.entity.StockNumDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MythMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(MythMallGoods record);

    int insertSelective(MythMallGoods record);

    MythMallGoods selectByPrimaryKey(Long goodsId);

    MythMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(MythMallGoods record);

    int updateByPrimaryKeyWithBLOBs(MythMallGoods record);

    int updateByPrimaryKey(MythMallGoods record);

    List<MythMallGoods> findMythMallGoodsList(PageQueryUtil pageUtil);

    int getTotalMythMallGoods(PageQueryUtil pageUtil);

    List<MythMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<MythMallGoods> findMythMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalMythMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<MythMallGoods> newBeeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}