package com.kk.community.controller;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.dao.CommentMapper;
import com.kk.community.entity.Comment;
import com.kk.community.entity.FastDfsFile;
import com.kk.community.util.FastDfsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class HelloController {
    @Resource
    private CommentMapper mapper;

    @GetMapping("/hello/{id}")
    public Comment hello(@PathVariable("id")Integer id){
        Comment comment = mapper.selectByPrimaryKey(id);
        return comment;
    }

    @PostMapping(value = "/search")
    public Page<Comment> getall(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "2") int pageSize)

    {
        PageHelper.startPage(page,pageSize);
        Page<Comment> commentList = mapper.selectList(1,1);
        return commentList;
    }


    @PostMapping("/upload")
    public String uploadFile(MultipartFile file){
        try{
            //判断文件是否存在
            if (file == null){
                throw new RuntimeException("文件不存在");
            }
            //获取文件的完整名称
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)){
                throw new RuntimeException("文件不存在");
            }
            //获取文件的扩展名称  abc.jpg   jpg
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //获取文件内容
            byte[] content = file.getBytes();
            //创建文件上传的封装实体类
            FastDfsFile fastDfsFile = new FastDfsFile(originalFilename,content,extName);
            //基于工具类进行文件上传,并接受返回参数  String[]
            String[] uploadResult = FastDfsClient.upload(fastDfsFile);
            //封装返回结果
            return FastDfsClient.getTrackerUrl()+uploadResult[0]+"/"+uploadResult[1];
        }catch (Exception e){
            e.printStackTrace();
        }
        return "文件上传失败";
    }


}
