package com.weiss.weissdata.repository;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.mongodb.client.result.UpdateResult;
import com.weiss.weissdata.model.forum.Like;
import com.weiss.weissdata.model.forum.Message;
import netscape.javascript.JSObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gt.valueOf;
public class LikeRepositoryImpl implements LikeRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${limitOfFeed}")
    private long limitOfFeed;

    @Override
    public boolean likeOrDislike(String messageId, Like like) {
        boolean res;
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(messageId).and("markList.userId").is(like.getUserId()));
        Update update = new Update();
        update.set("markList.$", like);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Message.class);
        long modifiedCount = updateResult.getModifiedCount();
        if(modifiedCount==0L){
            query = new Query();
            query.addCriteria(Criteria.where("id").is(messageId));//"5eac71e4d95f4f73289f3b4b"
            update = new Update();
            update.push("markList", like);
            updateResult = mongoTemplate.updateFirst(query, update, Message.class);
            modifiedCount=updateResult.getModifiedCount();
        }
        return modifiedCount>0?true:false;
    }


}
