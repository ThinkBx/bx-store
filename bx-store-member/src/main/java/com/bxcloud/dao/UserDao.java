package com.bxcloud.dao;

import com.bxcloud.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {

    @Select("select  id,username,password,phone,email,created,updated from mb_user where id =#{userId}")
    User findUserById(@Param(value = "userId") Long userId);

    @Insert("INSERT  INTO `mb_user` (username,password,phone,email,created,updated) VALUES (#{username}, #{password},#{phone},#{email},#{created},#{updated});")
    Integer insertUser(User user);

    @Select("select  id,username,password,phone,email,created,updated from mb_user where username=#{username} and password=#{password}")
    User login(@Param("username") String username,@Param("password") String password);


    @Select("select  id,username,password,phone,email,created,updated ,openid from mb_user where openid =#{openid}")
    User findUserByOpenId(@Param("openid") String openid);

    @Update("update mb_user set openid=#{openid} where id=#{userId}")
    Integer updateUserByOpenId(@Param("openid") String openid,@Param("userId") Integer userId);


}
