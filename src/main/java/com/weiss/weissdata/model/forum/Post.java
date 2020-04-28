package com.weiss.weissdata.model.forum;

import lombok.Data;

import java.util.List;

@Data
public class Post extends MessageEntity {
    String id;
    String header;
    List<Content> content;
    List<Content> shortContent;
}
