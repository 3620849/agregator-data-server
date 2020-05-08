package com.weiss.weissdata.services;

import com.weiss.weissdata.model.UserInfo;
import com.weiss.weissdata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

    public synchronized void addIfUserNotExist(UserInfo userInfo){
        Optional<UserInfo> byUsername = Optional.empty();
        byUsername = repository.findByLogin(userInfo.getLogin());
        if(byUsername.isEmpty()){
            repository.insert(userInfo);
        }else {
            throw new IllegalArgumentException("User already exist");
        }

    }

}
