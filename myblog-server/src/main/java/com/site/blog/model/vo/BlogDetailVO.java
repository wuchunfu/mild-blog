package com.site.blog.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.site.blog.model.entity.Category;
import com.site.blog.model.entity.Tag;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogDetailVO {
    private Long blogId;

    private String blogTitle;

    private Category blogCategory;

    private Long commentCount;

    private String blogCategoryIcon;
    private String blogPreface;

    private String blogCoverImage;

    private Long blogViews;

    private List<Tag> blogTags;
    private Integer blogStatus;
    private String blogContent;

    private Integer enableComment;
    private Boolean show;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
