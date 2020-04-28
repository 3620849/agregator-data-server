package com.weiss.weissdata.controllers;

import com.weiss.weissdata.model.NominationTime;
import com.weiss.weissdata.model.PostListDto;
import com.weiss.weissdata.model.forum.Post;
import com.weiss.weissdata.services.NominationService;
import com.weiss.weissdata.services.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PostService postService;
    @RequestMapping(value = "/post",method = RequestMethod.POST)
    public ResponseEntity savePost(@RequestBody Post post){
        try {
            postService.save(post);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/post",method = RequestMethod.GET)
    public ResponseEntity<PostListDto> getNewPostList(){
        List<Post> listNewPost = null;
        try {
             listNewPost = postService.getListNewPost();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(PostListDto.builder().postList(listNewPost).build(),HttpStatus.OK);
    }
}
