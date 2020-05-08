package com.weiss.weissdata.services;

import com.weiss.weissdata.model.forum.Like;
import com.weiss.weissdata.model.forum.Message;
import com.weiss.weissdata.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
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
        return repository.likeOrDislike( messageId, like);
    }
}
