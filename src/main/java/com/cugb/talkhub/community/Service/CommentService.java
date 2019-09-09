package com.cugb.talkhub.community.Service;

import com.cugb.talkhub.community.dto.CommentDTO;
import com.cugb.talkhub.community.enums.CommentTypeEnum;
import com.cugb.talkhub.community.enums.NotificationStatusEnum;
import com.cugb.talkhub.community.enums.NotificationTypeEnum;
import com.cugb.talkhub.community.exception.CustomizeErrorCode;
import com.cugb.talkhub.community.exception.CustomizeException;
import com.cugb.talkhub.community.mapper.CommentMapper;
import com.cugb.talkhub.community.mapper.NotificationMapper;
import com.cugb.talkhub.community.mapper.QuestionMapper;
import com.cugb.talkhub.community.mapper.UserMapper;
import com.cugb.talkhub.community.model.Comment;
import com.cugb.talkhub.community.model.Notification;
import com.cugb.talkhub.community.model.Question;

import com.cugb.talkhub.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional//提交上升为事务
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())) {
            //回复评论
            Comment dbComment = commentMapper.findById(comment.getParentId());//这个是父评论
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Question question = questionMapper.getById(dbComment.getParentId());//先找到回复评论的问题
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            Integer commentCount = dbComment.getCommentCount();
            commentCount++;
            commentMapper.incCommentCount(commentCount, dbComment.getId());
            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question = questionMapper.getById(comment.getParentId());//先找到回复评论的问题
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            Integer commentCount = question.getCommentCount();
            commentCount++;
            questionMapper.incCommentCount(commentCount, question.getId());
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    /**
     * 创建通知
     *
     * @param comment
     * @param receiver
     */
    private void createNotify(Comment comment, Integer receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Integer outerId) {
//        if (receiver == comment.getCommentator()) {
//            return;
//        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);//问题的id
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        System.out.println(notification);
        notificationMapper.insert(notification);
    }

    /**
     * 先根据questionId和type来获取question
     * question的id 对应着一些type=1的comments
     * comment.parentId = question
     * comment的子评论对应着一些type=2的comments
     * comment.parentId = comment
     * 返回
     * 1：对于问题的回复
     * 2：对于评论的回复
     *
     * @param id
     * @return
     */
    public List<CommentDTO> listByTargetId(Integer id, Integer type) {
        List<Comment> comments = commentMapper.FindByIdAndType(id, type);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        //map方法用于映射每个元素到对应的结果
        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        Map<Integer, User> userMap = new HashMap<>();
        //获取评论人并转化为Map
        for (Integer TmpId : userIds) {
            User TmpUser = userMapper.findById(TmpId);
            userMap.put(TmpId, TmpUser);
        }

        //转换comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        Collections.sort(commentDTOS, new Comparator<CommentDTO>() {//这里手工的实现一下倒序排列
            @Override
            public int compare(CommentDTO t1, CommentDTO t2) {
                if (t1.getGmtCreate().compareTo(t2.getGmtCreate()) > 0) {
                    return -1;
                } else if (t1.getGmtCreate().compareTo(t2.getGmtCreate()) < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return commentDTOS;
    }
}

