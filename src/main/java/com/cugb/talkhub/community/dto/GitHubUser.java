package com.cugb.talkhub.community.dto;

import lombok.Data;

@Data
public class GitHubUser {
    private String name;
    private String id;
    private String bio;
    private String avatar_url;
}
