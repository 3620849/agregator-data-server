package com.weiss.weissdata.model.forum;

import lombok.Data;

@Data
public class Dislike {
    long time;
    String userId;
    String clientId;
}
