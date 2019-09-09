package com.cugb.talkhub.community.mapper;

import com.cugb.talkhub.community.dto.QuestionDTO;
import com.cugb.talkhub.community.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmtCreate,gmtModified,creator,tag) values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void insert(Question question);
    //找到所有的questions
    @Select("select * from question where title like CONCAT('%',#{search},'%') order by gmtCreate DESC limit #{offset},#{size}")
    List<Question> list(@Param(value = "search") String search,@Param(value = "offset") Integer offset, @Param(value = "size") Integer size);
    //select * from question  where title like "%spring%" order by gmtCreate DESC limit 1,5


    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator = #{id} order by gmtCreate DESC limit #{offset},#{size}")
    List<Question> listByUserId(@Param(value = "id") Integer id, @Param(value = "offset") Integer offset, @Param(value = "size") Integer size);

    @Select("select count(1) from question where creator = #{id}")
    Integer countByUserId(@Param(value = "id") Integer id);

    @Select("select *from question where id = #{id}")
    Question getById(@Param(value = "id") Integer id);

    @Update("update question set title = #{title},description = #{description},gmtModified =#{gmtModified},tag =#{tag} where id =#{id}")
    void update(Question question);

    @Update("update question set viewCount = #{viewCount} where id =#{id}")
    void IncViewCount(@Param(value = "id") Integer id, @Param(value = "viewCount") Integer viewCount);

    @Update("update question set commentCount = #{commentCount} where id =#{id} ")
    void incCommentCount(@Param(value = "commentCount") Integer commentCount,@Param(value = "id") Integer id);

    @Select("select * from question where tag like CONCAT('%',#{tag},'%') and id !=#{id}")
    List<Question> selectRelated(@Param(value = "tag") String tag, @Param(value = "id") Integer id);

}
