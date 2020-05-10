package com.kk.community.service;

import java.util.Date;

/**
 * @author : K k
 * @date : 20:44 2020/5/8
 */

/*统计用户访问数据模板*/
public interface DataService {
    //将指定ip计入UV
    public void recordUV(String date);
    //统计指定日期范围内的UV
    public long calculateUV(Date start, Date end);
    //将指定用户计入DAU
    public void recordDAU(int userId);
    //统计指定日期范围内的DAU
    public long calculateDAU(Date start,Date end);
}
