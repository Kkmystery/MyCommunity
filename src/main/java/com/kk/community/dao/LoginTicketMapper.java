package com.kk.community.dao;

import com.kk.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated //不推荐使用
public interface LoginTicketMapper {
    int deleteByPrimaryKey(Integer id);

    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) " +
                    "values (#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(LoginTicket record);

    int insertSelective(LoginTicket record);

    LoginTicket selectByPrimaryKey(Integer id);

    @Select({
            "select * from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket,int status);

    int updateByPrimaryKeySelective(LoginTicket record);

    int updateByPrimaryKey(LoginTicket record);
}