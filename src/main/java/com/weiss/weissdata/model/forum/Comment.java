package com.weiss.weissdata.model.forum;

import lombok.Data;

import java.util.List;

@Data
public class Comment extends MessageEntity{
    String id;
    List<Content> contentList;
}
