package com.site.blog.model.entity;

import java.io.Serial;
import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author: 南街
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogConfig implements Serializable {

@Serial
private static final long serialVersionUID=1L;
@TableId(type = IdType.ASSIGN_ID)
private String id;
    /**
     * 字段名
     */
    @TableField("config_field")
    private String configField;

    /**
     * 配置名
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置项的值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Timestamp updateTime;


}
