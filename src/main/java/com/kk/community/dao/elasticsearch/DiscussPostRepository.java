package com.kk.community.dao.elasticsearch;

import com.kk.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : K k
 * @date : 21:09 2020/5/6
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
