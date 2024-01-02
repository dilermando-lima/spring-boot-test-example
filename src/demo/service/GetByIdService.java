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
public class GetByIdService {

    @Autowired
    AnyRepository anyRepository;

    public record Response(String id, String name, ZonedDateTime created,  ZonedDateTime lastUpdated) {}

    public Response getById(String id){
        validateIdRequest(id);
        AnyEntity anyEntity = getEntityById(id);
        return convertEntityToResponse(anyEntity);
    }

    AnyEntity getEntityById(String id) {
        return anyRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrMessage.ID_NOT_FOUND.get(id)));
    }

    void validateIdRequest(String id) {
        if(id == null || id.trim().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.ID_IS_REQUIRED.get());
    }

    Response convertEntityToResponse(AnyEntity anyEntity) {
        return new Response(
                anyEntity.getId(),
                anyEntity.getName(),
                anyEntity.getCreated(),
                anyEntity.getLastUpdated()
            );
    }
    
}
