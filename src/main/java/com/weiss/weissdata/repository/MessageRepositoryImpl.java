package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.Message;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

public class MessageRepositoryImpl implements MessageRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${limitOfFeed}")
    private long limitOfFeed;

    @Override
    public List<Message> getListNewPost(long skip) {
        MatchOperation match = Aggregation.match(Criteria.where("type").is("POST"));
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        SkipOperation skip1 = Aggregation.skip(skip);
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        GraphLookupOperation graph = Aggregation.graphLookup("message").
                startWith("$_id").
                connectFrom("_id").
                connectTo("ancestorId").as("comments");
        ProjectionOperation project = project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent")
                .and("comments").size().as("summary.comment").
                        and(
                                context -> {
                                    Document filterExpression = new Document();
                                    filterExpression.put("input", "$markList");
                                    filterExpression.put("as", "list");
                                    filterExpression.put("cond", new Document("$gt", Arrays.<Object>asList("$$list.value", 0)));
                                    return new Document("$filter", filterExpression);
                                }
                        ).as("summary.likeArray").
                        and(context -> {
                            Document filterExpression = new Document();
                            filterExpression.put("input", "$markList");
                            filterExpression.put("as", "list");
                            filterExpression.put("cond", new Document("$lt", Arrays.<Object>asList("$$list.value", 0)));
                            return new Document("$filter", filterExpression);
                        }).as("summary.dislikeArray");
        ProjectionOperation project2 = project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent").
                and("summary.comment").as("summary.comment").
                and("summary.likeArray").size().as("summary.like").
                and("summary.dislikeArray").size().as("summary.dislike");
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,skip1,limit,graph,project,project2);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }

    @Override
    public List<Message> getListTop(long skip) {
        //TODO implement this method
        return getListNewPost(skip);
    }

    @Override
    public List<Message> getListMonthly(long skip) {
        //TODO implement this method
        return getListNewPost(skip);
    }
}
