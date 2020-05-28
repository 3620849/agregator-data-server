package com.weiss.weissdata.controllers;

import com.weiss.weissdata.model.forum.Message;
import com.weiss.weissdata.model.forum.MessageListDto;
import com.weiss.weissdata.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MessageService messageService;
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResponseEntity<MessageListDto> getNewPostList(@PathVariable("id") String parentId){
        List<Message> messageList = null;
        try {
            messageList = messageService.getCommentsByParentId(parentId);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(MessageListDto.builder().messageList(messageList).build(),HttpStatus.OK);
    }
}
