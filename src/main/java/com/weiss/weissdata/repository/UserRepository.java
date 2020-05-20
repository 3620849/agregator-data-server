package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserInfo,String>,UserRepositoryExtended{
      Optional<UserInfo> findByUsername(String username);
      Optional<UserInfo> findByLogin(String login);
}
