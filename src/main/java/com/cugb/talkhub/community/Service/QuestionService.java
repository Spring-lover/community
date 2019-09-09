package com.cugb.talkhub.community.Service;


import com.cugb.talkhub.community.dto.PaginationDTO;
import com.cugb.talkhub.community.dto.QuestionDTO;
import com.cugb.talkhub.community.exception.CustomizeErrorCode;
import com.cugb.talkhub.community.exception.CustomizeException;
import com.cugb.talkhub.community.mapper.QuestionMapper;
import com.cugb.talkhub.community.mapper.UserMapper;
import com.cugb.talkhub.community.model.Question;
import com.cugb.talkhub.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    //起到一个中间件的作用，同时需要user和question的信息
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    public QuestionDTO getById(Integer id) {

        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);

        return questionDTO;
    }

    /**
     * 关于提问问题的排序的问题
     * 搜索问题的Mapper
     * @param search
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(String search, Integer page, Integer size) {

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        Integer totalCount = questionMapper.count();
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        if (page < 1) {
            page = 1;
        }
        if (page > totalCount) {
            page = totalCount;
        }

        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.list(search,offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setData(questionDTOS);
        return paginationDTO;
    }

    public PaginationDTO list(Integer id, Integer page, Integer size) {

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        Integer totalCount = questionMapper.countByUserId(id);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.listByUserId(id, offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setData(questionDTOS);
        return paginationDTO;
    }

    /**
     * 确定是新建还是插入
     * @param question
     */
    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            //创建问题
            question.setGmtModified(System.currentTimeMillis());
            question.setGmtCreate(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            System.out.println(question);
            System.out.println("我创建了问题");
            questionMapper.insert(question);
        }else{
            //更新
            question.setGmtModified(question.getGmtCreate());
            System.out.println(question);
            questionMapper.update(question);
//            if (updated != 1) {
//                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
//            }
        }
    }
    //这里涉及到高并发的问题，如果多次请求数据库的数据，要在原来的数据库上的sql语句上面解决或者再用乐观锁和悲观锁
    //这里的阅读数的递增也是很简单的递增一下
    public void IncView(Integer id) {
        Question question = questionMapper.getById(id);
        Integer viewCount = question.getViewCount();
        viewCount++;
        questionMapper.IncViewCount(id,viewCount);
    }

    /**
     * 找到具有相同的tag的问题
     * @param queryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(),",");
        //利用tags来进行模糊查询
        List<QuestionDTO> questionDTOS =new ArrayList<>();
        for (String tag : tags){
//            System.out.println(tag);
//            System.out.println(queryDTO.getId());
            List<Question> questions = questionMapper.selectRelated(tag,queryDTO.getId());
            for (Question question : questions) {
                User user = userMapper.findById(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOS.add(questionDTO);
            }
        }
        return questionDTOS;
    }
}
