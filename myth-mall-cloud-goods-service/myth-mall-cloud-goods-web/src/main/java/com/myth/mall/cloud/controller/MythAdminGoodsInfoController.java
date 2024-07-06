
package com.myth.mall.cloud.controller;

import com.myth.mall.cloud.config.annotation.TokenAdminUser;
import com.myth.mall.cloud.controller.param.BatchIdParam;
import com.myth.mall.cloud.controller.param.GoodsAddParam;
import com.myth.mall.cloud.controller.param.GoodsEditParam;
import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.dto.ResultGenerator;
import com.myth.mall.cloud.entity.GoodsCategory;
import com.myth.mall.cloud.entity.LoginAdminUser;
import com.myth.mall.cloud.entity.MythMallGoods;
import com.myth.mall.cloud.enums.ServiceResultEnum;
import com.myth.mall.cloud.service.MythMallCategoryService;
import com.myth.mall.cloud.service.MythMallGoodsService;
import com.myth.mall.cloud.until.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api(value = "v1", tags = "后台管理系统商品模块接口")
@RequestMapping("/goods/admin")
public class MythAdminGoodsInfoController {

    private static final Logger logger = LoggerFactory.getLogger(MythAdminGoodsInfoController.class);

    @Resource
    private MythMallGoodsService mythMallGoodsService;
    @Resource
    private MythMallCategoryService newBeeMallCategoryService;

    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "商品列表", notes = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "商品名称") String goodsName,
                       @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架") Integer goodsSellStatus, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(goodsName)) {
            params.put("goodsName", goodsName);
        }
        if (goodsSellStatus != null) {
            params.put("goodsSellStatus", goodsSellStatus);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mythMallGoodsService.getMythMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增商品信息", notes = "新增商品信息")
    public Result save(@RequestBody @Valid GoodsAddParam goodsAddParam, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        MythMallGoods newBeeMallGoods = new MythMallGoods();
        BeanUtil.copyProperties(goodsAddParam, newBeeMallGoods);
        String result = mythMallGoodsService.saveMythMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "修改商品信息", notes = "修改商品信息")
    public Result update(@RequestBody @Valid GoodsEditParam goodsEditParam, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        MythMallGoods newBeeMallGoods = new MythMallGoods();
        BeanUtil.copyProperties(goodsEditParam, newBeeMallGoods);
        String result = mythMallGoodsService.updateMythMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取单条商品信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        Map goodsInfo = new HashMap(8);
        MythMallGoods goods = mythMallGoodsService.getMythMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        goodsInfo.put("goods", goods);
        GoodsCategory thirdCategory;
        GoodsCategory secondCategory;
        GoodsCategory firstCategory;
        thirdCategory = newBeeMallCategoryService.getGoodsCategoryById(goods.getGoodsCategoryId());
        if (thirdCategory != null) {
            goodsInfo.put("thirdCategory", thirdCategory);
            secondCategory = newBeeMallCategoryService.getGoodsCategoryById(thirdCategory.getParentId());
            if (secondCategory != null) {
                goodsInfo.put("secondCategory", secondCategory);
                firstCategory = newBeeMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                if (firstCategory != null) {
                    goodsInfo.put("firstCategory", firstCategory);
                }
            }
        }
        return ResultGenerator.genSuccessResult(goodsInfo);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/updateStatus/{sellStatus}", method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改销售状态", notes = "批量修改销售状态")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @PathVariable("sellStatus") int sellStatus, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != 0 && sellStatus != 1) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (mythMallGoodsService.batchUpdateSellStatus(batchIdParam.getIds(), sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goodsDetail")
    @ApiOperation(value = "获取单条商品信息", notes = "根据id查询")
    public Result goodsDetail(@RequestParam("goodsId") Long goodsId) {
        MythMallGoods goods = mythMallGoodsService.getMythMallGoodsById(goodsId);
        return ResultGenerator.genSuccessResult(goods);
    }

}