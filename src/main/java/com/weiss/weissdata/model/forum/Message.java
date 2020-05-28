package com.weiss.weissdata.model.forum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Document
@Data
public class Message  {
    @Id
    String id;
    long time;
    long responseTime;
    MessageType type;
    @JsonSerialize(using= ToStringSerializer.class)
    ObjectId ancestorId;
    @JsonSerialize(using= ToStringSerializer.class)
    ObjectId parentPostId;
    List<Like> markList;
    MetaDataSummary summary;
    String userId;
    String userName;
    String userPhoto;
    String clientId;
    String header;
    List<Content> content;
    List<Content> shortContent;
    List<Message> comments;
}
