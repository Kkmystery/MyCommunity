package com.kk.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "discusspost",type = "_doc",shards =6 ,replicas =3 )
public class DiscussPost implements Serializable {
    @Id
    private Integer id;
    @Field(type = FieldType.Integer)
    private Integer userId;                   //存储分词器                   智能分词器
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private Integer commentCount;
    @Field(type = FieldType.Integer)
    private Integer type;
    @Field(type = FieldType.Integer)
    private Integer status;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Double)
    private Double score;

}