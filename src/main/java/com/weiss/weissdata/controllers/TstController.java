package com.weiss.weissdata.controllers;
import com.weiss.weissdata.exeptions.ErrorResponse;
import com.weiss.weissdata.exeptions.RecordNotFoundException;
import com.weiss.weissdata.model.UserInfo;
import com.weiss.weissdata.repository.UserRepository;
import com.weiss.weissdata.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TstController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserRepository repository;
    @Autowired
    UserService userService;
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public boolean create(@RequestBody UserInfo user){
        repository.save(user);
        return true;
    }
    @RequestMapping(value = "/getUserById/{id}",method = RequestMethod.GET)
    public ResponseEntity<UserInfo> getUsers(@PathVariable("id")String id){
        UserInfo user = repository.findById(id).orElseThrow(() -> new RecordNotFoundException("no such user"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @RequestMapping(value = "/getUserByName/{name}",method = RequestMethod.GET)
    public ResponseEntity<UserInfo> getUsersByName(@PathVariable("name")String name){
        UserInfo user =  repository.findByUsername(name).orElseThrow(() -> new RecordNotFoundException("no such user"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public ResponseEntity getUsers(@RequestBody UserInfo userInfo){
        try {
            userService.addIfUserNotExist(userInfo);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
           return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    //getUserByLogin
    @RequestMapping(value = "/getUserByLogin/{login}",method = RequestMethod.GET)
    public ResponseEntity<UserInfo> getUsersByLogin(@PathVariable("login")String login){
        Optional<UserInfo> oUser =  repository.findByLogin(login);
        if(oUser.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        };
        return new ResponseEntity<>(oUser.get(), HttpStatus.OK);
    }
}
