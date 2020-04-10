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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized void addIfUserNotExist(UserInfo userInfo) throws IllegalArgumentException {
        Optional<UserInfo> byUsername = repository.findByLogin(userInfo.getLogin());
        byUsername.ifPresent(s->{throw new IllegalArgumentException("User with name "+ userInfo.getUsername()+" already exist");});
        repository.insert(userInfo);
    }

}
