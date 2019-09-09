package com.cugb.talkhub.community.controller;

import com.cugb.talkhub.community.Service.CommentService;
import com.cugb.talkhub.community.Service.QuestionService;
import com.cugb.talkhub.community.dto.CommentDTO;
import com.cugb.talkhub.community.dto.QuestionDTO;
import com.cugb.talkhub.community.enums.CommentTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments =  commentService.listByTargetId(id, CommentTypeEnum.QUESTION.getType());//id = question.id
        //累加阅读数
        questionService.IncView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
