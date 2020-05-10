package com.kk.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket implements Serializable {
    private Integer id;

    private Integer userId;

    private String ticket;

    private Integer status;

    private Date expired;

}