package demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;

@Service
public class ListService {

    @Autowired
    AnyRepository anyRepository;

    public record ResponseItem(String id, String name) {}
    
    public List<ResponseItem> list(Integer numPage, Integer sizePage){
        validateRequest(numPage, sizePage);
        Pageable pageable = buildPageable(numPage, sizePage);
        List<AnyEntity> anyEntity = listEntity(pageable);
        return convertListEntityToListResponse(anyEntity);
    }

    List<ResponseItem> convertListEntityToListResponse(List<AnyEntity> anyEntity) {
        return anyEntity.stream().map(e ->  new ResponseItem(e.getId(), e.getName())).toList();
    }

    List<AnyEntity> listEntity(Pageable pageable) {
        return anyRepository.findAll(pageable).getContent();
    }

    Pageable buildPageable(Integer numPage, Integer sizePage) {
        final Sort defaultSortList  = Sort.by(Direction.DESC, "id");
        final int defaultSizePage = 10;
        numPage = numPage == null ? 0 : numPage;
        sizePage = sizePage == null ?  defaultSizePage : sizePage;
        return PageRequest.of(numPage, sizePage, defaultSortList);
    }

    void validateRequest(Integer numPage, Integer sizePage) {
        if(numPage != null && numPage < 0 ) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.NUMPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get());
        if(sizePage != null && sizePage < 0 ) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.SIZEPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get());

        final int maxSizePageAllowed = 300;
        if(sizePage != null && sizePage > maxSizePageAllowed )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrMessage.SIZEPAGE_MUST_BE_LESS_THAN_X.get(maxSizePageAllowed));
    }


}
