package com.weiss.weissdata.repository;

import com.mongodb.client.result.UpdateResult;
import com.weiss.weissdata.model.UserInfo;
import com.weiss.weissdata.model.forum.Message;
import com.weiss.weissdata.model.forum.MyMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UserRepositoryExtendedImpl implements UserRepositoryExtended {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public boolean addToMyList(MyMark like) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(like.getUserId()).and("markList.messageId").is(like.getMessageId()));
        Update update = new Update();
        update.set("markList.$", like);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, UserInfo.class);
        long modifiedCount = updateResult.getModifiedCount();
        if(modifiedCount==0L){
            query = new Query();
            query.addCriteria(Criteria.where("id").is(like.getUserId()));
            update = new Update();
            update.push("markList", like);
            updateResult = mongoTemplate.updateFirst(query, update, UserInfo.class);
            modifiedCount=updateResult.getModifiedCount();
        }
        return modifiedCount>0?true:false;
    }
}
