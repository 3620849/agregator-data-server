package com.weiss.weissdata.controllers;

import com.weiss.weissdata.model.NominationTime;
import com.weiss.weissdata.model.UserInfo;
import com.weiss.weissdata.services.NominationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class NominationController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    NominationService nominationService;
    @RequestMapping(value = "/nomination",method = RequestMethod.POST)
    public ResponseEntity saveNomination(@RequestBody NominationTime nomination){
        try {
            nominationService.update(nomination);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping(value = "/nomination",method = RequestMethod.GET)
    public ResponseEntity getNomination(){
        NominationTime nt;
        try {
             nt = nominationService.get();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(nt, HttpStatus.OK);
    }
}
