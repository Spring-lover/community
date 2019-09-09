package com.cugb.talkhub.community.controller;
import com.cugb.talkhub.community.Service.QuestionService;
import com.cugb.talkhub.community.dto.PaginationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page",defaultValue = "1")Integer page,
                        @RequestParam(name = "size",defaultValue = "10")Integer size,
                        @RequestParam(name = "search",required = false,defaultValue = "")String search) {

        PaginationDTO pagination = questionService.list(search,page,size);
        model.addAttribute("pagination", pagination);//也就是将questions放在前端的页面上显示
        model.addAttribute("search", search);
        return "index";
    }
}
