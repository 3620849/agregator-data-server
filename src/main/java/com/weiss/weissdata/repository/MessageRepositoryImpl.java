package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.Message;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

public class MessageRepositoryImpl implements MessageRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${limitOfFeed}")
    private long limitOfFeed;
    @Value("${selectionLimitFeed}")
    private long selectionLimitFeed;

    @Override
    public List<Message> getListNewPost(long skip) {
        MatchOperation match = Aggregation.match(Criteria.where("type").is("POST"));
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        SkipOperation skip1 = Aggregation.skip(skip);
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        LookupOperation lookup = lookupComments();
        ProjectionOperation project =getMessageProject();
        ProjectionOperation project2 = walkArounBugProject();
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,skip1,limit,lookup,project,project2);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }

    private GraphLookupOperation graphComments() {
        return Aggregation.graphLookup("message")
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("ancestorId").maxDepth(0).as("comments");
    }
    private LookupOperation lookupComments() {
        return Aggregation.lookup("message","_id","parentPostId","comments");
    }
    private ProjectionOperation walkArounBugProject() {
        return project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent","parentPostId","ancestorId").
                and("summary.comment").as("summary.comment").and("summary.likeArray").size().as("summary.like")
                .and("summary.dislikeArray").size().as("summary.dislike");
    }

    private ProjectionOperation getMessageProject() {
        return project("id", "time", "type", "content","userId","userName","userPhoto",
                "clientId","header","shortContent","parentPostId","ancestorId")
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
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        long preSkip = 0;
        if(skip>= selectionLimitFeed){
            preSkip=skip/ selectionLimitFeed * selectionLimitFeed;
            skip=skip-preSkip;
        }
        LimitOperation limit = Aggregation.limit(limitOfFeed+skip);
        SkipOperation preSkipOp = Aggregation.skip(preSkip);
        LimitOperation selectionLimit = Aggregation.limit(selectionLimitFeed +preSkip);
        LookupOperation lookup = lookupComments();
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
        SortOperation sortByLike = Aggregation.sort(Sort.Direction.DESC,"summary.likeReg","summary.likeAnon","time");
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,preSkipOp,selectionLimit,lookup,project,project2,sortByLike,skip1,limit,includeAllFieldsProject());
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        return aggregate.getMappedResults();
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
        long monthAgo = new Date().getTime()-2592000000L;
        MatchOperation match = Aggregation.match(Criteria.where("type").is("POST").and("time").gt(monthAgo));
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        LookupOperation lookup = lookupComments();
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
        SortOperation sortByLike = Aggregation.sort(Sort.Direction.DESC,"summary.likeReg","summary.likeAnon","time");
        Aggregation aggregation = Aggregation.newAggregation(match,lookup,project,project2,sortByLike,includeAllFieldsProject(),skip1,limit);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        return aggregate.getMappedResults();
    }

    public List<Message> getMessageListByIdWithMetaData(String[] ids ){
        MatchOperation match = Aggregation.match(new Criteria("_id")
                .in(Streamable.of(ids).stream().collect(StreamUtils.toUnmodifiableList())));
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        LookupOperation lookup = lookupComments();
        ProjectionOperation project =getMessageProject();
        ProjectionOperation project2 = walkArounBugProject();
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,lookup,project,project2);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }

    @Override
    public List<Message> getMessageListByUserId(String userId, long skip) {
        MatchOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        LimitOperation limit = Aggregation.limit(limitOfFeed);
        SkipOperation skip1 = Aggregation.skip(skip);
        LookupOperation lookup = lookupComments();
        ProjectionOperation project =getMessageProject();
        ProjectionOperation project2 = walkArounBugProject();
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,lookup,project,project2,skip1,limit);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        return aggregate.getMappedResults();
    }
    @Override
    public List<Message> getCommentsByParentId(String parentId){
        MatchOperation match = Aggregation.match(new Criteria("parentPostId")
                .in(Streamable.of(new ObjectId(parentId)).stream().collect(StreamUtils.toUnmodifiableList())));
        SortOperation sortByTime = Aggregation.sort(Sort.by("time").descending());
        GraphLookupOperation lookup = graphComments();
        ProjectionOperation project =getMessageProject().and("comments").as("comments");
        ProjectionOperation project2 = walkArounBugProject().and("comments").as("comments");
        Aggregation aggregation = Aggregation.newAggregation(match,sortByTime,lookup,project,project2);
        AggregationResults<Message> aggregate = mongoTemplate.aggregate(aggregation,"message", Message.class);
        List<Message> mappedResults = aggregate.getMappedResults();
        return mappedResults;
    }
}
