package com.weiss.weissdata.model.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Document
@Data
public class Message  {
    String id;
    long time;
    long responseTime;
    MessageType type;
    String ancestorId;
    String parentPostId;
    List<Like> markList;
    MetaDataSummary summary;
    String userId;
    String userName;
    String userPhoto;
    String clientId;
    String header;
    List<Content> content;
    List<Content> shortContent;
}
