package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.Like;
import com.weiss.weissdata.model.forum.Message;

import java.util.List;

public interface LikeRepository {
   boolean likeOrDislike(String messageId, Like like) ;

}
