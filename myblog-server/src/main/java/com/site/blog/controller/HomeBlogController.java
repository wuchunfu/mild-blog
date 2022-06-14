package com.site.blog.controller;


import cn.hutool.core.lang.Console;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.site.blog.constants.*;
import com.site.blog.model.dto.*;
import com.site.blog.model.entity.*;
import com.site.blog.model.vo.BlogDetailVO;
import com.site.blog.service.*;
import com.site.blog.util.BeanMapUtil;
import com.site.blog.util.RequestHelper;
import com.site.blog.util.ResultGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 博客前台
 *
 * @author Linn-cn
 * @date 2020/12/7
 */
@RestController
@RequestMapping("/v2/home")
@io.swagger.v3.oas.annotations.tags.Tag(name = "首页数据", description = "首页")
@RequiredArgsConstructor
public class HomeBlogController {

    public static String theme = "amaze";


    private final BlogInfoService blogInfoService;


    private final TagService tagService;

    private final BlogCategoryService blogCategoryService;

    private final CategoryService categoryService;

    private final BlogTagService blogTagService;

    private final BlogConfigService blogConfigService;


    private final BlogService blogService;


    private final CommentService commentService;


    private final LinkService linkService;

    /**
     * 博客首页
     *
     * @author Linn-cn
     * @date 2020/12/7
     */
    @GetMapping({"/", "/index"})
    public Result index() {
        return this.page(new BlogPageCondition()
                .setPageNum(1)
                .setPageName("首页")
        );
    }

    /**
     * 标签
     *
     * @param tagId
     * @return java.lang.String
     * @date 2019/9/6 7:04
     */
    @PostMapping({"/tag/{tagId}"})
    public Result tag(@PathVariable("tagId") String tagId, @RequestBody PageDto pageDto) {

        Page<BlogInfo> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        LambdaQueryWrapper<BlogInfo> sqlWrapper = Wrappers.<BlogInfo>lambdaQuery()


                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getShow, DeleteStatusEnum.SHOW.getStatus());
        //获取tag下的文章
        if (Objects.nonNull(tagId)) {
            List<BlogTag> list = blogService.list(new QueryWrapper<BlogTag>()
                    .lambda().eq(BlogTag::getTagId, tagId));
            if (!CollectionUtils.isEmpty(list)) {
                sqlWrapper.in(BlogInfo::getBlogId, list.stream().map(BlogTag::getBlogId).toArray());
            } else {
                return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, Collections.emptyList());
            }
        }
        sqlWrapper.orderByDesc(BlogInfo::getCreateTime);
        Page<BlogInfo> blogInfoPage = blogInfoService.page(page, sqlWrapper);
        List<BlogDetailVO> blogDetailVOS = toBlogVo(blogInfoPage);

        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, blogDetailVOS);
    }

    /**
     * bloginfo转blogdetailvo
     *
     * @param blogInfoPage
     * @return
     */
    public List<BlogDetailVO> toBlogVo(Page<BlogInfo> blogInfoPage) {

        List<BlogDetailVO> blogDetailVOS = blogInfoPage.getRecords().stream().map(BeanMapUtil::copyBlog).collect(Collectors.toList());

        blogDetailVOS.forEach(post -> {

            QueryWrapper<BlogTag> tagQueryWrapper = new QueryWrapper<BlogTag>().eq("blog_id", post.getBlogId());
            List<Tag> tags = blogTagService.list(tagQueryWrapper).stream().map(item -> tagService.getById(item.getTagId())).collect(Collectors.toList());
            post.setBlogTags(tags);
            String cateId = blogCategoryService.getOne(new QueryWrapper<BlogCategory>().eq("blog_id", post.getBlogId())).getCategoryId();
            if (cateId != null) {
                post.setBlogCategory(categoryService.getById(cateId));
            }
        });
        return blogDetailVOS;
    }

    @PostMapping("/timeline")
    public Result timeline(@RequestBody PageDto pageDto) {
        try {
            QueryWrapper<BlogInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().orderByDesc(BlogInfo::getUpdateTime);
            Page<BlogInfo> ipage = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
            Page<BlogInfo> blogInfoPage = blogInfoService.page(ipage, queryWrapper);

            List<BlogDetailVO> blogDetailVOS = blogInfoPage.getRecords().stream().map(BeanMapUtil::copyBlog).collect(Collectors.toList());

            blogDetailVOS.forEach(post -> {

                QueryWrapper<BlogTag> tagQueryWrapper = new QueryWrapper<BlogTag>().eq("blog_id", post.getBlogId());
                List<Tag> tags = blogTagService.list(tagQueryWrapper).stream().map(item -> tagService.getById(item.getTagId())).collect(Collectors.toList());
                post.setBlogTags(tags);
               String cateId = blogCategoryService.getOne(new QueryWrapper<BlogCategory>().eq("blog_id", post.getBlogId())).getCategoryId();
                if (cateId != null) {
                    post.setBlogCategory(categoryService.getById(cateId));
                }
            });
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, blogDetailVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR, false, "失败了");
        }

    }

    /**
     * 分类
     *
     * @param categoryId
     * @author Linn-cn
     * @date 2020/12/7
     */
    @PostMapping({"/category/{categoryId}"})
    public Result category(@PathVariable("categoryId") String categoryId, @RequestBody PageDto pageDto) {

        Page<BlogInfo> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        LambdaQueryWrapper<BlogInfo> sqlWrapper = Wrappers.<BlogInfo>lambdaQuery()


                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getShow, DeleteStatusEnum.SHOW.getStatus());
        //获取tag下的文章
        if (Objects.nonNull(categoryId)) {
            List<BlogCategory> list = blogCategoryService.list(new QueryWrapper<BlogCategory>()
                    .lambda().eq(BlogCategory::getCategoryId, categoryId));
            System.out.println(list);
            System.out.println("这是list");
            if (!CollectionUtils.isEmpty(list)) {
                sqlWrapper.in(BlogInfo::getBlogId, list.stream().map(BlogCategory::getBlogId).toArray());
            } else {
                return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, Collections.emptyList());
            }
        }
        sqlWrapper.orderByDesc(BlogInfo::getCreateTime);
        Page<BlogInfo> blogInfoPage = blogInfoService.page(page, sqlWrapper);
        List<BlogDetailVO> blogDetailVOS = toBlogVo(blogInfoPage);

        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, blogDetailVOS);
    }

    /**
     * 搜索
     *
     * @param keyword
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/search/{keyword}"})
    public Result search(@PathVariable("keyword") String keyword) {
        try {
            PageDto pageDto = new PageDto(0, 10);
            Page<BlogInfo> ipage = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());

            QueryWrapper<BlogInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(BlogInfo::getBlogTitle, keyword).or().like(BlogInfo::getBlogContent, keyword);
            Page<BlogInfo> blogInfos = blogInfoService.page(ipage, queryWrapper);


            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, toBlogVo(blogInfos));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, null);
    }

    @GetMapping("/tags")
    public Result getTags() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Tag::getIsDeleted, DeleteStatusEnum.SHOW.getStatus());
        List<Tag> list = tagService.list();
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, list);
    }

    @GetMapping("/categories")
    public Result getCate() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Category::getIsDeleted, DeleteStatusEnum.SHOW.getStatus()).orderByDesc(Category::getCreateTime);
        List<Category> list = categoryService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, true, list);
    }


    @GetMapping("/configs")
    public Result<Object> getConfigs() {
        try {

            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, blogConfigService.getAllConfigs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST, "00");
    }

    /**
     * 博客分页
     *
     * @param condition
     * @throws
     * @author Linn-cn
     * @date 2020/12/7
     */
    @GetMapping({"/page"})
    public Result page(BlogPageCondition condition) {
        if (Objects.isNull(condition.getPageNum())) {
            condition.setPageNum(1);
        }
        if (Objects.isNull(condition.getPageSize())) {
            condition.setPageSize(5);
        }
        HashMap<String, Object> result = new HashMap<>();
        Page<BlogInfo> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        PageDto pageDto = new PageDto();
        pageDto.setPageNum(condition.getPageNum());
        pageDto.setPageSize(condition.getPageSize());
        LambdaQueryWrapper<BlogInfo> sqlWrapper = Wrappers.<BlogInfo>lambdaQuery()
                .like(Objects.nonNull(condition.getKeyword()), BlogInfo::getBlogTitle, condition.getKeyword())

                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getShow, DeleteStatusEnum.SHOW.getStatus());
        //获取tag下的文章
        if (Objects.nonNull(condition.getTagId())) {
            List<BlogTag> list = blogService.list(new QueryWrapper<BlogTag>()
                    .lambda().eq(BlogTag::getTagId, condition.getTagId()));
            if (!CollectionUtils.isEmpty(list)) {
                sqlWrapper.in(BlogInfo::getBlogId, list.stream().map(BlogTag::getBlogId).toArray());
            }
        }
        sqlWrapper.orderByDesc(BlogInfo::getCreateTime);
        blogInfoService.page(page, sqlWrapper);


        if (Objects.nonNull(condition.getTagId())) {
            result.put("tagId", condition.getTagId());
        }
        if (Objects.nonNull(condition.getCategoryId())) {
            result.put("categoryName", condition.getCategoryId());
        }
        //start
        QueryWrapper<BlogInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(BlogInfo::getUpdateTime);
        Page<BlogInfo> ipage = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        Page<BlogInfo> blogInfoPage = blogInfoService.page(ipage, queryWrapper);

        List<BlogDetailVO> blogDetailVOS = toBlogVo(blogInfoPage);

        result.put("blogPageResult", blogDetailVOS);
        result.put("pageName", condition.getPageName());
        result.put("newBlogs", blogInfoService.getNewBlog());
        result.put("hotBlogs", blogInfoService.getHotBlog());
        result.put("hotTags", tagService.getBlogTagCountForIndex());
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, result);
    }

    /**
     * 文章详情
     *
     * @param blogId
     * @return java.lang.String
     * @date 2019/9/6 13:09
     */
    @GetMapping({"/blog/{blogId}", "/article/{blogId}"})
    public Result<Object> detail(@PathVariable("blogId") String blogId) {
        // 获得文章info
        BlogInfo blogInfo = blogInfoService.getById(blogId);
        List<BlogTag> blogTags = blogService.list(new QueryWrapper<BlogTag>()
                .lambda()
                .eq(BlogTag::getBlogId, blogId));
        blogInfoService.updateById(new BlogInfo()
                .setBlogId(blogInfo.getBlogId())
                .setBlogViews(blogInfo.getBlogViews() + 1));

        // 获得关联的标签列表
        List<String> tagIds;
        List<Tag> tagList = new ArrayList<>();
        if (!blogTags.isEmpty()) {
            tagIds = blogTags.stream()
                    .map(BlogTag::getTagId).toList();
            tagList = tagService.list(new QueryWrapper<Tag>().lambda().in(Tag::getTagId, tagIds));
        }

        // 关联评论的Count
        long blogCommentCount = commentService.count(new QueryWrapper<Comment>()
                .lambda()
                .eq(Comment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(Comment::getShow, DeleteStatusEnum.SHOW.getStatus())
                .eq(Comment::getBlogId, blogId));
        HashMap<String, Object> result = new HashMap<>();
        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogInfo, blogDetailVO);
        blogDetailVO.setCommentCount(blogCommentCount);
        Console.log("homeblog创建时间{}",blogDetailVO.getCreateTime().toString());
        result.put("blogDetailVO", blogDetailVO);
        result.put("tagList", tagList);
        result.put("pageName", "详情");
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, result);
    }

    /**
     * 评论列表
     *
     * @param ajaxPutPage
     * @param blogId
     * @return com.site.blog.pojo.dto.AjaxResultPage<com.site.blog.entity.BlogComment>
     * @date 2019/11/19 8:42
     */
    @GetMapping("/blog/listComment")
    @ResponseBody
    public AjaxResultPage<Comment> listComment(AjaxPutPage<Comment> ajaxPutPage, String blogId) {
        Page<Comment> page = ajaxPutPage.putPageToPage();
        commentService.page(page, new QueryWrapper<Comment>()
                .lambda()
                .eq(Comment::getBlogId, blogId)
                .eq(Comment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(Comment::getShow, DeleteStatusEnum.SHOW.getStatus())
                .orderByDesc(Comment::getCommentCreateTime));
        AjaxResultPage<Comment> ajaxResultPage = new AjaxResultPage<>();
        ajaxResultPage.setCount(page.getTotal());
        ajaxResultPage.setList(page.getRecords());
        return ajaxResultPage;
    }

    /**
     * 友链界面
     *
     * @return java.lang.String
     * @date 2019/9/6 17:26
     */
    @GetMapping({"/link"})
    public Result link() {
        List<Link> favoriteLinks = linkService.list(new QueryWrapper<Link>()
                .lambda().eq(Link::getLinkType, LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
        );
        List<Link> recommendLinks = linkService.list(new QueryWrapper<Link>()
                .lambda().eq(Link::getLinkType, LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
        );
        List<Link> personalLinks = linkService.list(new QueryWrapper<Link>()
                .lambda().eq(Link::getLinkType, LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
        );
        //判断友链类别并封装数据 0-友链 1-推荐 2-个人网站
        HashMap<String, Object> result = new HashMap<>();
        result.put("pageName", "友情链接");
        result.put("favoriteLinks", favoriteLinks);
        result.put("recommendLinks", recommendLinks);
        result.put("personalLinks", personalLinks);
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, result);
    }

    /**
     * 提交评论
     *
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/9/6 17:40
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public Result<String> comment(Comment comment) {
        HttpServletRequest request = RequestHelper.getHttpServletRequest();
        String ref = request.getHeader("Referer");
        System.out.println(ref);
        System.out.println("这是ref");

        // 对非法字符进行转义，防止xss漏洞
        comment.setCommentBody(StringEscapeUtils.escapeHtml4(comment.getCommentBody()));
        comment.setCommentStatus(1);
        comment.setCommentatorIp(RequestHelper.getRequestIp());
        comment.setUserAgent(RequestHelper.getUa().getBrowser()+RequestHelper.getUa().getVersion());
        comment.setOs(RequestHelper.getUa().getOs().toString());
        comment.setCommentCreateTime(LocalDateTime.now());
        comment.setShow(true);
        //if (!StringUtils.hasText(ref)) {
        //    return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR, "非法请求");
        //}
        boolean flag = commentService.save(comment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

}
