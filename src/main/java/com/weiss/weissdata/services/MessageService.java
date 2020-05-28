package com.weiss.weissdata.services;

import com.mongodb.BasicDBObject;
import com.weiss.weissdata.model.forum.*;
import com.weiss.weissdata.repository.PostRepository;
import com.weiss.weissdata.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<Message> getMessageListByUserId(String userId, long skip) {
        return repository.getMessageListByUserId(userId,skip);
    }

    public List<Message> getCommentsByParentId(String parentId) {
        List<Message> commentsByParentId = repository.getCommentsByParentId(parentId);
        //build comment tree
        //for each comment put it in hasmap
        LinkedList<Message> firstLevelChilds = new LinkedList();
        LinkedList<Message> resultList = new LinkedList();
        HashMap<ObjectId,Message>map = new HashMap<ObjectId, Message>(commentsByParentId.size());
        commentsByParentId.stream().forEach(message -> {
            if(message.getComments()!=null && message.getComments().size()>0){
                firstLevelChilds.addAll(message.getComments());
            }
            map.put(new ObjectId(message.getId()),message);
            if(message.getAncestorId().equals(message.getParentPostId())){
                resultList.add(message);
            }
        });
        //bind each component together
        firstLevelChilds.stream().forEach(message -> {
            if(map.containsKey(new ObjectId(message.getId()))){
                Message message1 = map.get(new ObjectId(message.getId()));
                message.setComments(message1.getComments());
            }
        });
        //put inside messages 0 level with ancestor id equals post id
        return resultList;
    }
}
