package com.site.blog.mapper


import com.site.blog.model.entity.BlogInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


/**
 *
 *
 * 博客信息表 Mapper 接口
 *
 *
 * @author zhulin
 * @since 2019-08-27
 */
@Repository
interface BlogInfoMapper : JpaRepository<BlogInfo,Long> {
    /**
     * 获取
     *
     * @return [Integer]
     */
    @Select("select sum(blog_views) FROM  blog_info")
    fun getViews(): Int?
}