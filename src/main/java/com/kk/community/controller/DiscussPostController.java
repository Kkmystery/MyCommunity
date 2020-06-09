package com.kk.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.community.entity.Comment;
import com.kk.community.entity.DiscussPost;
import com.kk.community.entity.User;
import com.kk.community.event.EventProducer;
import com.kk.community.service.*;
import com.kk.community.util.*;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author : K k
 * @date : 19:44 2020/4/30
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;


    private EmojiUtil emojiUtil;

    //添加一条帖子
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登陆哦！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setCommentCount(0);
        post.setType(0);
        post.setStatus(0);
        post.setScore(0d);
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件，存入elasticsearch
        Event event =new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())  //发布人
                .setEntityType(ENTITY_TYPE_POST)  //帖子类型
                .setEntityId(post.getId());   //帖子id
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey=RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,post.getId());

        //报错情况，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    //得到帖子详情页相关信息
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "8") int pageSize) {
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //查询作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //点赞信息
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        //点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        //包括：评论和回复两部分
        //评论分页信息
        PageHelper.startPage(page, pageSize);
        String pagePath = "/discuss/detail/" + discussPostId;
        //当Entity_Type=1的时候，Entity_id代表是帖子的id
        Page<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId());
        //用于前端分页
        model.addAttribute("page", commentList);
        model.addAttribute("pagePath", pagePath);
        //评论的VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                //评论内容
                comment.setContent(EmojiUtil.emojiConverterUnicodeStr(comment.getContent()));
                commentVo.put("comment", comment);
                //评论人的信息
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //点赞信息
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //回复列表
                PageHelper.startPage(0, Integer.MAX_VALUE);
                /*当Entity_Type=1的时候，Entity_id代表是帖子的id
                  当Entity_Type=2的时候，Entity_id代表是用户的id
                  */
                Page<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                model.addAttribute("replyList", replyList);
                //回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        reply.setContent(EmojiUtil.emojiConverterUnicodeStr(reply.getContent()));
                        replyVo.put("reply", reply);
                        //回复人信息
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        /*User userById = userService.findUserById(reply.getUserId());
                        System.out.println(userById.getUsername()+"-----"+userById.getEmail());*/
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        //点赞信息
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "site/discuss-detail";
    }

    @RequestMapping(path = "/myPost/{userId}",method = RequestMethod.GET)
    public String getMyPost(@PathVariable("userId")int userId, Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "pageSize", required = false, defaultValue = "8") int pageSize)
    {
        User user = userService.findUserById(userId);
        PageHelper.startPage(page,pageSize);
        Page<DiscussPost> myDiscussPost = discussPostService.findMyDiscussPost(userId);
        List<Map<String,Object>> myPostList=new ArrayList<>();
        if (myDiscussPost!=null){
            for (DiscussPost discussPost:myDiscussPost){
                Map<String ,Object> map=new HashMap<>();
                map.put("post",discussPost);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount",likeCount);
                myPostList.add(map);
            }
        }

        int discussPostCount = discussPostService.findMyDiscussPostCount(userId);
        String pagePath = "/detail/myPost/" + userId;
        //用于前端分页
        model.addAttribute("page", myDiscussPost);
        model.addAttribute("pagePath", pagePath);
        model.addAttribute("myPostList",myPostList);
        model.addAttribute("user",user);
        model.addAttribute("discussPostCount",discussPostCount);
        return "site/my-post";
    }

    //置顶
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        User user = hostHolder.getUser();

        discussPostService.updateType(id,1);
        Event event =new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    //加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        User user = hostHolder.getUser();

        discussPostService.updateStatus(id,1);
        Event event =new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey=RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        return CommunityUtil.getJSONString(0);
    }

    //删除
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        User user = hostHolder.getUser();

        discussPostService.updateType(id,1);
        //需要把评论也删除
        /*Page<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, id);
        for (Comment commentVo : commentList) {
            commentService.deleteComment(commentVo.getId(),1);
            Page<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, commentVo.getId());
            for (Comment reply : replyList) {
                commentService.deleteComment(reply.getId(),1);
            }
        }*/

        //删贴事件
        Event event =new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
}
