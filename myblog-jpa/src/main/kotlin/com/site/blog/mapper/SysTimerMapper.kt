package com.site.blog.mapper


import com.site.blog.model.entity.SysTimer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


/**
 * @author yanni
 * @date time 2022/6/17 15:50
 * @modified By:
 */
@Repository
interface SysTimerMapper : JpaRepository<SysTimer,Long>