package com.weiss.weissdata.services;

import com.weiss.weissdata.model.forum.*;
import com.weiss.weissdata.repository.PostRepository;
import com.weiss.weissdata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MessageService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository repository;

    public void save(Message message) {
        repository.save(message);
    }

    public List<Message> getListNewPost(String type,long skip){
        List<Message> list = null;
        switch (type) {
            case "top": list= repository.getListTop(skip);break;
            case "new":  list=repository.getListNewPost(skip);break;
            case "monthly":list= repository.getListMonthly(skip);break;

        }
        return  list;
    }

    public boolean likeOrDislike(String messageId, Like like) {
        if(like.getLikeType()== LikeType.REGISTERED){
            MyMark myMark = new MyMark();
            myMark.setMessageId(messageId);
            myMark.setClientId(like.getClientId());
            myMark.setUserId(like.getUserId());
            myMark.setValue(like.getValue());
            myMark.setTime(like.getTime());
            userRepository.addToMyList(myMark);
        }
        return repository.likeOrDislike( messageId, like);
    }

    public  List<Message> getMessageListById(ListMessages idList) {
        return repository.getMessageListByIdWithMetaData(idList.getIdsList());
    }
}
