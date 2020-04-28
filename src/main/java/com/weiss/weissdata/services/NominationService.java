package com.weiss.weissdata.services;

import com.weiss.weissdata.model.NominationTime;
import com.weiss.weissdata.repository.NominationTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NominationService {
    @Autowired
    NominationTimeRepository repository;
    public NominationTime get(){
        List<NominationTime> all = repository.findAll();
        if(all==null || all.size()==0){
            return null;
        }
        return all.get(0);
    }

    public synchronized void update(NominationTime nomination) {
        List<NominationTime> all = repository.findAll();
        if(all==null || all.size()==0){
            repository.insert(nomination);
        }
        NominationTime entityDb = all.get(0);
        nomination.setId(entityDb.getId());
        repository.save(nomination);
    }
}
