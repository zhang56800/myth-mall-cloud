
package com.myth.mall.cloud.service;

import com.myth.mall.cloud.dto.PageQueryUtil;
import com.myth.mall.cloud.dto.PageResult;
import com.myth.mall.cloud.entity.Carousel;

public interface MythMallCarouselService {

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Long[] ids);
}
