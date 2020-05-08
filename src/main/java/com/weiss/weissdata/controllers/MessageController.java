package com.weiss.weissdata.controllers;

import com.weiss.weissdata.model.forum.*;
import com.weiss.weissdata.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MessageService messageService;
    @RequestMapping(value = "/message",method = RequestMethod.POST)
    public ResponseEntity savePost(@RequestBody Message message){
        try {
            sanitize(message);
            messageService.save(message);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private void sanitize(Message message) {
        message.setTime(new Date().getTime());
        message.setMarkList(new LinkedList<>());
        message.setSummary(new MetaDataSummary());
    }

    @RequestMapping(value = "/message",method = RequestMethod.GET)
    public ResponseEntity<MessageListDto> getNewPostList(@RequestParam("type") String type, @RequestParam("skip") long skip){
        List<Message> messageList = null;
        try {
              messageList = messageService.getListNewPost(type,skip);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(MessageListDto.builder().messageList(messageList).build(),HttpStatus.OK);
    }
    @RequestMapping(value = "/likeOrDislike",method = RequestMethod.GET)
    public ResponseEntity likeOrDislike(@RequestParam("messageId") String messageId,
                                        @RequestParam(value = "userId" ,required = false) String userId,
                                        @RequestParam(value = "clientId" ) String clientId,
                                        @RequestParam("value")byte value){
        boolean res;
        LikeType type=LikeType.REGISTERED;
        if(userId==null || userId.isBlank()){
            userId=clientId;
            type=LikeType.ANONYMOUS;};
        try {
            Like like = Like.builder().
                    userId(userId).
                    clientId(clientId).
                    time(new Date().getTime()).
                    likeType(type).
                    value(value>0?(byte)1:(byte)-1).build();
            res= messageService.likeOrDislike(messageId,like);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(res,HttpStatus.OK);
    }
}