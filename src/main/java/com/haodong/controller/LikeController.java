package com.haodong.controller;

import com.haodong.async.EventModel;
import com.haodong.async.EventProducer;
import com.haodong.async.EventType;
import com.haodong.model.HostHolder;
import com.haodong.service.CommentService;
import com.haodong.service.LikeService;
import com.haodong.util.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;
    /**
     * 对评论点赞同
     *
     * @param commentId
     * @return
     */
    @RequestMapping(path = "/like", method = RequestMethod.GET)
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return "redirect:/reglogin";
        }

        eventProducer.fireEvent(
                new EventModel(EventType.LIKE)
                        .setActorId(hostHolder.getUser().getId())
                        .setEntityId(commentId)
                        .setEntityType(EntityType.COMMENT_TYPE)
                        .setEntityOwnerId(commentService.getCommentById(commentId).getUserId())
                        .setExt("questionId", String.valueOf(commentService.getCommentById(commentId).getEntityId())));

        likeService.like(hostHolder.getUser().getId(), EntityType.COMMENT_TYPE, commentId);
        return "redirect:/question/" + String.valueOf(commentService.getCommentById(commentId).getEntityId());
    }

    /**
     * 对评论点反对
     * @param commentId
     * @return
     */
    @RequestMapping(path = "/dislike", method = RequestMethod.GET)
    public String disLike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return "redirect:/reglogin";
        }
        eventProducer.fireEvent(
                new EventModel(EventType.LIKE)
                        .setActorId(hostHolder.getUser().getId())
                        .setEntityId(commentId)
                        .setEntityType(EntityType.COMMENT_TYPE)
                        .setEntityOwnerId(commentService.getCommentById(commentId).getUserId())
                        .setExt("questionId", String.valueOf(commentService.getCommentById(commentId).getEntityId())));

        likeService.disLike(hostHolder.getUser().getId(), EntityType.COMMENT_TYPE, commentId);
        return "redirect:/question/" + String.valueOf(commentService.getCommentById(commentId).getEntityId());
    }
}
