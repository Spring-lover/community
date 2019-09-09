package com.cugb.talkhub.community.controller;


import com.cugb.talkhub.community.Service.NotificationService;
import com.cugb.talkhub.community.Service.QuestionService;
import com.cugb.talkhub.community.dto.PaginationDTO;
import com.cugb.talkhub.community.model.Notification;
import com.cugb.talkhub.community.model.Question;
import com.cugb.talkhub.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        //根据传递的参数来决定进入的页面类型
        if ("questions".equals(action)) {
            //我的问题界面
            PaginationDTO<Question> paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            model.addAttribute("pagination", paginationDTO);
        } else {
            //最新回复
            PaginationDTO<Notification> paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("section", "replies");
            Integer unreadCount = notificationService.unreadCount(user.getId());
            model.addAttribute("sectionName", "最新回复");
            model.addAttribute("pagination", paginationDTO);
        }
        return "profile";
    }
}
