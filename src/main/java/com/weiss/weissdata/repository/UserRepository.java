package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserInfo,String> {
      Optional<UserInfo> findByUsername(String name);
      Optional<UserInfo> findByLogin(String login);
}
