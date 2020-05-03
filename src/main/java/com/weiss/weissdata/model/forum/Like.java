package com.weiss.weissdata.model.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Like {
    long time;
    String userId;
    String clientId;
    byte value;
    LikeType likeType;
}
