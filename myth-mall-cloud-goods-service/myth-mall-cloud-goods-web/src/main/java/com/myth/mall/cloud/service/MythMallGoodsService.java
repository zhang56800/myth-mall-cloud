
package com.myth.mall.cloud.service;

import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.dto.PageResult;
import com.myth.mall.cloud.entity.MythMallGoods;

import java.util.List;

public interface MythMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getMythMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveMythMallGoods(MythMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param mythMallGoodsList
     * @return
     */
    void batchSaveMythMallGoods(List<MythMallGoods> mythMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateMythMallGoods(MythMallGoods goods);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    MythMallGoods getMythMallGoodsById(Long id);
}
