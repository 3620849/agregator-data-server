package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.NominationTime;
import com.weiss.weissdata.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NominationTimeRepository extends MongoRepository<NominationTime,String> {
}
