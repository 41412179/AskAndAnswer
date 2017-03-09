package com.haodong.async.handle;

import com.alibaba.fastjson.JSONObject;
import com.haodong.async.EventHandler;
import com.haodong.async.EventModel;
import com.haodong.async.EventType;
import com.haodong.model.Feed;
import com.haodong.model.Question;
import com.haodong.model.User;
import com.haodong.service.FeedService;
import com.haodong.service.FollowService;
import com.haodong.service.QuestionService;
import com.haodong.service.UserService;
import com.haodong.util.EntityType;
import com.haodong.util.JedisAdapter;
import com.haodong.util.RedisKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 好友动态
 * 我关注的人赞同了回答
 * 我关注的人回答了问题
 * 我关注的人关注了某个问题
 * 我关注的人回答了某个问题
 */
@Component
public class FeedHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(FeedHandler.class);
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            logger.error("actor出现异常");
            return null;
        }
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());
        //如果发生了一条评论或者我关注了一个问题
        if (model.getType() == EventType.COMMENT || (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.QUESTION)) {
            Question question = questionService.queryQuestionById(model.getEntityId());
            if (question == null) {
                logger.error("关注问题时发生异常！");
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", String.valueOf(question.getTitle()));
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandler(EventModel eventModel) {
        Feed feed = new Feed();
        //将事件的触发者设置为0-10之间的某个用户，这样更好看一点
        //eventModel.setActorId(new Random().nextInt(10) + 1);
        eventModel.setActorId(eventModel.getActorId());
        feed.setCreatedDate(new Date());
        feed.setUserId(eventModel.getActorId());
        feed.setType(eventModel.getType().getValue());
        feed.setData(buildFeedData(eventModel));
        //如果出问题，这个事件我们就不处理了
        if (feed.getData() == null) {
            logger.error("feed设置数据时出现错误");
            return;
        }
        //添加到mysql数据库中，支持pull
        boolean success = feedService.addFeed(feed);
        if(!success){
            logger.error("添加feed到数据库的时候出错了");
        }else{
            //向所有的粉丝推送
            //将所有的粉丝取出来
            List<Integer> followers = followService.getFollowers(EntityType.USER, eventModel.getActorId(), Integer.MAX_VALUE);
            //如果粉丝的数量太多，可以每次取100个，取多次推送
//            followers.add(0);
            for (int follower:
                 followers) {
                String timeLineKey = RedisKeyGenerator.getTimeLineKey(follower);
                //添加到redis中，支持push
                jedisAdapter.lpush(timeLineKey, String.valueOf(feed.getId()));
            }
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{
                EventType.COMMENT, EventType.FOLLOW
        });
    }
}