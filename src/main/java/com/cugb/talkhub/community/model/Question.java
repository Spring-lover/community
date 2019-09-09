package com.cugb.talkhub.community.model;

import lombok.Data;

@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;//这个问题的发起者
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;

}
