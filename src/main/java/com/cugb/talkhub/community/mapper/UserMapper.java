package com.cugb.talkhub.community.mapper;


import com.cugb.talkhub.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name,accountId,token,gmtCreate,gmtModified,avatarUrl) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    // //不使用@Param注解时，参数只能有一个，并且是Javabean。在SQL语句里可以引用JavaBean的属性，而且只能引用JavaBean的属性。
    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);

    @Select("select * from user where accountId = #{accountId}")
    User findByAccountId(@Param(value = "accountId") String accountId);

    @Update("update user set name = #{name},token=#{token},gmtModified = #{gmtModified} , avatarUrl = #{avatarUrl} where id = #{id}")
    void update(User user);
}
