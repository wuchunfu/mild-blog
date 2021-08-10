package com.site.blog.controller.v2;

import com.site.blog.constants.HttpStatusEnum;
import com.site.blog.model.dto.AjaxResultPage;
import com.site.blog.model.dto.Result;
import com.site.blog.model.entity.BlogConfig;
import com.site.blog.service.BlogConfigService;
import com.site.blog.util.DateUtils;
import com.site.blog.util.ResultGenerator;
import io.swagger.annotations.Api;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yzqde
 * @qq交流群 796794009
 * @qq 1320291471
 * @Description: blog配置controller
 * @create 2020/12/27
 */
@RestController
@RequestMapping("/v2/admin")
@Api(tags = "配置信息")
public class ConfigJsonController {

    @Resource
    private BlogConfigService blogConfigService;



    /**
     * 返回系统配置信息
     * @param
     * @return com.site.blog.pojo.dto.AjaxResultPage<com.site.blog.entity.BlogConfig>
     * @date 2019/8/29 19:30
     */
    @GetMapping("/blogConfig/list")
    public AjaxResultPage<BlogConfig> getBlogConfig(){
        AjaxResultPage<BlogConfig> ajaxResultPage = new AjaxResultPage<>();
        List<BlogConfig> list = blogConfigService.list();
        if (CollectionUtils.isEmpty(list)){
            ajaxResultPage.setCode(500);
            return ajaxResultPage;
        }
        ajaxResultPage.setData(blogConfigService.list());
        return ajaxResultPage;
    }

    /**
     * 修改系统信息
     * @param blogConfig
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/8/29 19:45
     */
    @PostMapping("/blogConfig/edit")
    public Result<String> updateBlogConfig(BlogConfig blogConfig){
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogConfigService.updateById(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }




    /**
     * 新增系统信息项
     * @param blogConfig
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/8/30 10:57
     */
    @PostMapping("/blogConfig/add")
    public Result<String> addBlogConfig(BlogConfig blogConfig){
        blogConfig.setCreateTime(DateUtils.getLocalCurrentDate());
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogConfigService.save(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除配置信息项
     * @param configField
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/8/30 11:21
     */
    @PostMapping("/blogConfig/del")
    public Result<String> delBlogConfig(@RequestParam String configField){
        boolean flag = blogConfigService.removeById(configField);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
