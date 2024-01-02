package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.repository.AnyRepository;

@Service
public class RemoveByIdService {

    @Autowired
    AnyRepository anyRepository;

    public void removeById(String id){
        validateIdRequest(id);
        validateIdExists(id);
        remove(id);
    }

    void validateIdExists(String id) {
        if(!anyRepository.existsById(id)) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrMessage.ID_NOT_FOUND.get(id));
    }

    void remove(String id) {
        anyRepository.deleteById(id);
    }

    void validateIdRequest(String id) {
        if(id == null || id.trim().isBlank()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.ID_IS_REQUIRED.get());
    }
    
}
