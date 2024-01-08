package demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;

@Service
public class ListService {

    @Value("${app.default-size-page}")
    public int defaultSizePage;

    private static final int MAX_SIZE_PAGE_ALLOWED = 300;

    public record Request(Integer numPage, Integer sizePage, String filter) {}

    @Autowired
    AnyRepository anyRepository;

    public record ResponseItem(String id, String name) {}
    
    public List<ResponseItem> list(Request request){  
        validateRequest(request);
        request = handleRequestPagination(request);
        List<AnyEntity> anyEntityList = listEntity(request);
        return convertListEntityToListResponse(anyEntityList);
    }
    
    List<ResponseItem> convertListEntityToListResponse(List<AnyEntity> anyEntity) {
        if(anyEntity == null) return new ArrayList<>();
        return anyEntity.stream().map(e ->  new ResponseItem(e.getId(), e.getName())).toList();
    }

    List<AnyEntity> listEntity(Request request) {
        return anyRepository.listByFilter(request.numPage, request.sizePage, request.filter);
    }

    Request handleRequestPagination(Request request) {
        return new Request(
                request.numPage == null ? 0 : request.numPage, 
                request.sizePage == null ?  defaultSizePage : request.sizePage, 
                request.filter
            );
    }

    int maxSizePageAllowed(){
        return MAX_SIZE_PAGE_ALLOWED;
    }

    void validateRequest(Request request) {
        if(request == null) 
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrMessage.REQUEST_CANNOT_BE_NULL.get());
        if(request.numPage != null && request.numPage < 0 ) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NUMPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get());
        if(request.sizePage != null && request.sizePage < 0 ) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.SIZEPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get());

        if(request.sizePage != null && request.sizePage > maxSizePageAllowed() )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.SIZEPAGE_MUST_BE_LESS_THAN_X.get(maxSizePageAllowed()));
    }


}
