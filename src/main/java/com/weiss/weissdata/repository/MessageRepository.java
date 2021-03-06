package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.Message;

import java.util.List;

public interface MessageRepository {
    List<Message> getListNewPost(long skip);
    List<Message> getListTop(long skip);
    List<Message> getListMonthly(long skip);
    List<Message> getMessageListByIdWithMetaData(String[] ids);
    List<Message> getMessageListByUserId(String userId,long skip);
    List<Message> getCommentsByParentId(String parentId);
}
