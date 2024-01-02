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
public class CreateService {

    @Autowired
    AnyRepository anyRepository;
    
    public record Response(String id) {}
    public record Request(String name){}

    public Response create(Request request){

        validateRequest(request);
        
        AnyEntity anyEntity = null;
        anyEntity = convertRequestToEntity(request);
        anyEntity = prepareEntityBeforeCreating(anyEntity);
        anyEntity = createEntity(anyEntity);

        return convertEntityToResponse(anyEntity);

    }

    Response convertEntityToResponse(AnyEntity anyEntity) {
        return new Response(anyEntity.getId());
    }

    AnyEntity createEntity(AnyEntity anyEntity) {
        return anyRepository.save(anyEntity);
    }

    AnyEntity prepareEntityBeforeCreating(AnyEntity anyEntity) {
        final ZonedDateTime now = ZonedDateTime.now();
        anyEntity.setCreated(now);
        anyEntity.setLastUpdated(now);
        return anyEntity;
    }

    AnyEntity convertRequestToEntity(Request request) {
        var anyEntity =  new AnyEntity();
        anyEntity.setName(request.name);
        return anyEntity;
    }

    void validateRequest(Request request) {
        if(request == null) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.BODY_NOT_FOUND.get());
        if(request.name == null || request.name.trim().isBlank()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NAME_IS_REQUIRED.get());
        if(request.name.length() > 50 )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NAME_MUST_BE_LESS_THAN_50_CARACT.get());
    }
    
}
