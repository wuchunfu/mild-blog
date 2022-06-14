package com.site.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author: 南街
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Tag implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 标签表主键id
     */
    @TableId(value = "tag_id", type = IdType.ASSIGN_ID)
    private Long tagId;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 是否删除 0=否 1=是
     */
    @TableField("show")
    private Boolean show;

    /**
     * 创建时间
     */

    @TableField("create_time")
    private Timestamp createTime;


}
