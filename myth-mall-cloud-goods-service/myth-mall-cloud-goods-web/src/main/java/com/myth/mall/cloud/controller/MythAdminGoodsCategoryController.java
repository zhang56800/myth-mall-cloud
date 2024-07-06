/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2022 程序员十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.myth.mall.cloud.controller;

import com.myth.mall.cloud.config.annotation.TokenAdminUser;
import com.myth.mall.cloud.controller.param.BatchIdParam;
import com.myth.mall.cloud.controller.param.GoodsCategoryAddParam;
import com.myth.mall.cloud.controller.param.GoodsCategoryEditParam;
import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.dto.ResultGenerator;
import com.myth.mall.cloud.entity.GoodsCategory;
import com.myth.mall.cloud.entity.LoginAdminUser;
import com.myth.mall.cloud.enums.MythMallCategoryLevelEnum;
import com.myth.mall.cloud.enums.ServiceResultEnum;
import com.myth.mall.cloud.service.MythMallCategoryService;
import com.myth.mall.cloud.until.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Api(value = "v1", tags = "后台管理系统分类模块接口")
@RequestMapping("/categories/admin")
public class MythAdminGoodsCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(MythAdminGoodsCategoryController.class);

    @Resource
    private MythMallCategoryService mythMallCategoryService;

    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "商品分类列表", notes = "根据级别和上级分类id查询")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "分类级别") Integer categoryLevel,
                       @RequestParam(required = false) @ApiParam(value = "上级分类的id") Long parentId, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10 || categoryLevel == null || categoryLevel < 0 || categoryLevel > 3 || parentId == null || parentId < 0) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("categoryLevel", categoryLevel);
        params.put("parentId", parentId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mythMallCategoryService.getCategorisPage(pageUtil));
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/list4Select", method = RequestMethod.GET)
    @ApiOperation(value = "商品分类列表", notes = "用于三级分类联动效果制作")
    public Result listForSelect(@RequestParam("categoryId") Long categoryId, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("缺少参数！");
        }
        GoodsCategory category = mythMallCategoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == MythMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map categoryResult = new HashMap(4);
        if (category.getCategoryLevel() == MythMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = mythMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MythMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = mythMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), MythMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == MythMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = mythMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MythMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增分类", notes = "新增分类")
    public Result save(@RequestBody @Valid GoodsCategoryAddParam goodsCategoryAddParam, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryAddParam, goodsCategory);
        String result = mythMallCategoryService.saveCategory(goodsCategory);
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
    @ApiOperation(value = "修改分类信息", notes = "修改分类信息")
    public Result update(@RequestBody @Valid GoodsCategoryEditParam goodsCategoryEditParam, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryEditParam, goodsCategory);
        String result = mythMallCategoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条分类信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        GoodsCategory goodsCategory = mythMallCategoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    /**
     * 分类删除
     */
    @RequestMapping(value = "/batchDelete", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除分类信息", notes = "批量删除分类信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenAdminUser LoginAdminUser adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (mythMallCategoryService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}