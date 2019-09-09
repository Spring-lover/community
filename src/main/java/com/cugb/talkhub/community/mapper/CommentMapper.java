package com.cugb.talkhub.community.mapper;

import com.cugb.talkhub.community.model.Comment;
import org.apache.ibatis.annotations.*;


import java.util.List;


@Mapper
public interface CommentMapper {

    @Select("select * from comment where id = #{parentId}")
    Comment findById(@Param(value = "parentId") Integer parentId);

    @Insert("insert into comment (parentId,type,commentator,gmtCreate,gmtModified,content,commentCount) values(#{parentId},#{type},#{commentator},#{gmtCreate},#{gmtModified},#{content},#{commentCount})")
    void insert(Comment comment);


    @Select("select * from comment where parentId = #{id} and type =#{type}")
    List<Comment> FindByIdAndType(@Param("id") Integer id, @Param("type") Integer type);

    @Update("update comment set commentCount = #{commentCount} where id =#{id} ")
    void incCommentCount(@Param("commentCount") Integer commentCount, @Param("id") Integer id);
}
