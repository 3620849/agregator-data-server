package com.weiss.weissdata.services;

import com.weiss.weissdata.model.forum.Post;
import com.weiss.weissdata.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    PostRepository repository;

    public void save(Post post) {
        repository.save(post);
    }

    public List<Post> getListNewPost(){
        return  repository.findAll();
    }
}
