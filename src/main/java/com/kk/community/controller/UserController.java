package com.kk.community.controller;

import com.kk.community.annotation.LoginRequired;
import com.kk.community.entity.FastDfsFile;
import com.kk.community.entity.User;
import com.kk.community.service.FollowService;
import com.kk.community.service.LikeService;
import com.kk.community.service.UserService;
import com.kk.community.util.CommunityConstant;
import com.kk.community.util.CommunityUtil;
import com.kk.community.util.FastDfsClient;
import com.kk.community.util.HostHolder;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author : K k
 * @date : 12:46 2020/4/30
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domin;
    @Value("${commuinty.path.filePath}")
    private String filePath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /*上传文件访问路径*/
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        //判断文件格式
        if (headerImage == null) {
            model.addAttribute("error", "您还没选择图片");
            return "/site/setting";
        }
        //全名
        String fileName = headerImage.getOriginalFilename();
        //文件后缀 .jpg, .png
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //后缀名 如jpg、png
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式b不正确");
            return "/site/setting";
        }

        //若正确上传,生成随机文件名
        //fileName = CommunityUtil.generateUUid() + suffix;

        try {
            byte[] content = headerImage.getBytes();
            //创建文件上传的封装实体类 包括文件名字、文件字节、文件格式
            FastDfsFile fastDfsFile = new FastDfsFile(fileName,content,extName);
            //基于工具类进行文件上传,并接受返回参数  String[] 上传至云服务器
            String[] uploadResult = FastDfsClient.upload(fastDfsFile);
            //封装返回结果
            //确定文件存放路径
           /* File dest = new File(uploadPath + "/" + fileName);
            try {
                //存储文件
                headerImage.transferTo(dest);
            } catch (IOException e) {
                logger.error("上传文件失败" + e.getMessage());
                throw new RuntimeException("上传文件失败，服务器发送异常！", e);
            }*/
            //更新当前用户头像路径(web访问路径)
            //http://localhost:8080/community/user/header/xxx.png
            //更改为http://47.98.255.128:8888/group1/M00/00/00/xxx.jpg
            User user = hostHolder.getUser();
            System.out.println(user.toString());
            //存放的是一个对服务器的请求图片路径，服务器拿到里面的图片名字再从本地获取
            String headerUrl = filePath + uploadResult[1];
            userService.updateHeader(user.getId(), headerUrl);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常！", e);
        }
        return "redirect:/index";
    }

    //向浏览器获取图片，使用io流方式 response写出
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (//从硬盘中读取数据并上传到前端模板
             //放在()内是需要自动关闭流的
             OutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(fileName);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            //将字节流读到缓冲区
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取图像失败" + e.getMessage());
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user=userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed=false;
        if (hostHolder.getUser()!=null){
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        model.addAttribute("likeCount",likeCount);
        return "/site/profile";

    }
}
