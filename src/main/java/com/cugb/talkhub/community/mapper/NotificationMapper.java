package com.cugb.talkhub.community.mapper;

import com.cugb.talkhub.community.model.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("insert into notification (notifier,receiver,outerId,type,gmtCreate,status,notifierName,outerTitle) values (#{notifier},#{receiver},#{outerId},#{type},#{gmtCreate},#{status},#{notifierName},#{outerTitle})")
    void insert(Notification notification);

    @Select("select count(1) from notification where receiver = #{id} and status = 0")
    Integer countByUserAndStatus(@Param("id") Integer id);

    @Select("select count(1) from notification where receiver = #{id}")
    Integer countByUserId(@Param("id") Integer id);

    @Select("select * from notification where receiver = #{id} order by gmtCreate DESC limit #{offset},#{size}")
    List<Notification> listByUserId(@Param("id") Integer id,@Param("offset") Integer offset,@Param("size") Integer size);

    @Select("select * from notification where id = #{id}")
    Notification selectById(@Param("id") Integer id);

    @Update("update notification set status = #{notification} where id =#{id} ")
    void updateStatus(@Param("notification") Integer notification,@Param("id") Integer id);
}
