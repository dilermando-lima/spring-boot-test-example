package demo.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;

@Service
public class UpdateByIdService {

    @Autowired
    AnyRepository anyRepository;

    public record Request(String name){}

    public void updateById(String id, Request request){
        validateRequest(id, request);
        AnyEntity anyEntity = getEntityById(id);
        anyEntity = prepareEntityBeforeUpdating(anyEntity, request);
        updateEntity(anyEntity);
    }

    AnyEntity updateEntity(AnyEntity anyEntity) {
        return anyRepository.save(anyEntity);
    }

    AnyEntity prepareEntityBeforeUpdating(AnyEntity anyEntity, Request request) {
        anyEntity.setLastUpdated(ZonedDateTime.now());
        anyEntity.setName(request.name);
        return anyEntity;
    }

    AnyEntity convertRequestToEntity(Request request) {
        var anyEntity =  new AnyEntity();
        anyEntity.setName(request.name);
        return anyEntity;
    }

    AnyEntity getEntityById(String id) {
        return anyRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrMessage.ID_NOT_FOUND.get(id)));
    }

    void validateRequest(String id, Request request) {
        if(id == null || id.trim().isBlank()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.ID_IS_REQUIRED.get());
        if(request == null) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.BODY_NOT_FOUND.get());
        if(request.name == null || request.name.trim().isBlank()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NAME_IS_REQUIRED.get());
        if(request.name.length() > 50 )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NAME_MUST_BE_LESS_THAN_50_CARACT.get());
    }
    
}
