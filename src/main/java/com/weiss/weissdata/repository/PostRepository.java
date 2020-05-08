package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Message, String>,LikeRepository,MessageRepository {

}
