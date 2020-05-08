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
import java.util.Date;
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
        GraphLookupOperation graph = graphComments();
        ProjectionOperation project =getMessageProject();
        ProjectionOperation project2 = walkArounBugProject();
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,skip1,limit,graph,project,project2);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }

    private GraphLookupOperation graphComments() {
        return Aggregation.graphLookup("message").
                startWith("$_id").
                connectFrom("_id").
                connectTo("ancestorId").as("comments");
    }

    private ProjectionOperation walkArounBugProject() {
        return project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent").
                and("summary.comment").as("summary.comment").and("summary.likeArray").size().as("summary.like")
                .and("summary.dislikeArray").size().as("summary.dislike");
    }

    private ProjectionOperation getMessageProject() {
        return project("id", "time", "type", "content","userId","userName","userPhoto",
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
    }

    @Override
    public List<Message> getListTop(long skip) {
        MatchOperation match = Aggregation.match(Criteria.where("type").is("POST"));
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        GraphLookupOperation graph = graphComments();
        SkipOperation skip1 = Aggregation.skip(skip);
        ProjectionOperation project =getMessageProject().and(
                context -> {
                    Document filterExpression = new Document();
                    filterExpression.put("input", "$markList");
                    filterExpression.put("as", "list");
                    filterExpression.put("cond", new Document("$and",
                            Arrays.<Object>asList(
                                    new Document("$gt", Arrays.<Object>asList("$$list.value", 0))
                                    ,new Document("$eq",Arrays.<Object>asList("$$list.likeType", "REGISTERED"))))
                    );
                    return new Document("$filter", filterExpression);  }
        ).as("summary.likeRegArr").and(
                context -> {
                    Document filterExpression = new Document();
                    filterExpression.put("input", "$markList");
                    filterExpression.put("as", "list");
                    filterExpression.put("cond", new Document("$and",
                            Arrays.<Object>asList(
                                    new Document("$gt", Arrays.<Object>asList("$$list.value", 0))
                                    ,new Document("$eq",Arrays.<Object>asList("$$list.likeType", "ANONYMOUS"))))
                    );
                    return new Document("$filter", filterExpression);  }
        ).as("summary.likeAnonArr");

        ProjectionOperation project2 = walkArounBugProject().and("summary.likeRegArr").size().as("summary.likeReg")
                .and("summary.likeAnonArr").size().as("summary.likeAnon");
        SortOperation sortByLike = Aggregation.sort(Sort.by("summary.likeReg")
                .descending().and(Sort.by("summary.likeAnon").descending()));
        Aggregation aggregation = Aggregation.newAggregation(match,skip1,limit,graph,project,project2,sortByLike,includeAllFieldsProject());
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }

    private AggregationOperation includeAllFieldsProject() {
        return project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent").
                and("summary.comment").as("summary.comment").
                and("summary.like").as("summary.like").
                and("summary.dislike").as("summary.dislike");
    }


    @Override
    public List<Message> getListMonthly(long skip) {
        //TODO implement this method
        long monthAgo = new Date().getTime()-2592000000l;
        MatchOperation match = Aggregation.match(Criteria.where("type").is("POST").and("time").gt(monthAgo));
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        GraphLookupOperation graph = graphComments();
        SkipOperation skip1 = Aggregation.skip(skip);
        ProjectionOperation project =getMessageProject().and(
                context -> {
                    Document filterExpression = new Document();
                    filterExpression.put("input", "$markList");
                    filterExpression.put("as", "list");
                    filterExpression.put("cond", new Document("$and",
                            Arrays.<Object>asList(
                                    new Document("$gt", Arrays.<Object>asList("$$list.value", 0))
                                    ,new Document("$eq",Arrays.<Object>asList("$$list.likeType", "REGISTERED"))))
                    );
                    return new Document("$filter", filterExpression);  }
        ).as("summary.likeRegArr").and(
                context -> {
                    Document filterExpression = new Document();
                    filterExpression.put("input", "$markList");
                    filterExpression.put("as", "list");
                    filterExpression.put("cond", new Document("$and",
                            Arrays.<Object>asList(
                                    new Document("$gt", Arrays.<Object>asList("$$list.value", 0))
                                    ,new Document("$eq",Arrays.<Object>asList("$$list.likeType", "ANONYMOUS"))))
                    );
                    return new Document("$filter", filterExpression);  }
        ).as("summary.likeAnonArr");

        ProjectionOperation project2 = walkArounBugProject().and("summary.likeRegArr").size().as("summary.likeReg")
                .and("summary.likeAnonArr").size().as("summary.likeAnon");
        SortOperation sortByLike = Aggregation.sort(Sort.by("summary.likeReg")
                .descending().and(Sort.by("summary.likeAnon").descending()));
        Aggregation aggregation = Aggregation.newAggregation(match,skip1,limit,graph,project,project2,sortByLike,includeAllFieldsProject());
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }
}
