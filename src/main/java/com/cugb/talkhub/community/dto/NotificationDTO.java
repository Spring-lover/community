package com.cugb.talkhub.community.dto;

import com.cugb.talkhub.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    //没有receiver 因为就是userId
    private Integer id;
    private User notifier;
    private Integer outerId;
    private Integer type;//处于未读还是处于已读状态
    private Long gmtCreate;
    private Integer status;
    private String notifierName;
    private String outerTitle;
    private String typeName;//评论了问题还是评论了你的回复
}
