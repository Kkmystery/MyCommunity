package com.kk.community.service;

import com.kk.community.entity.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * @author : K k
 * @date : 12:11 2020/5/7
 */

public interface ElasticsearchService {

    public void saveDiscussPost(DiscussPost post);

    public void deleteDiscussPost(int id);

    public Page<DiscussPost> searchDiscussPost(String keyword,int current,int limit);
}
