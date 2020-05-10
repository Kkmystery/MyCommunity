package com.kk.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message implements Serializable {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private Integer status;

    private Date createTime;

    private String content;

}