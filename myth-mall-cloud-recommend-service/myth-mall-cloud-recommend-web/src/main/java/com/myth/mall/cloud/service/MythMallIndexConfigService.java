
package com.myth.mall.cloud.service;


import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.dto.PageResult;
import com.myth.mall.cloud.entity.IndexConfig;

public interface MythMallIndexConfigService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    Boolean deleteBatch(Long[] ids);
}
