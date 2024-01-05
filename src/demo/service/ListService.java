package demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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
        Pageable pageable = buildPageable(request);
        Specification<AnyEntity> specification = buildSpecification(request);
        List<AnyEntity> anyEntityList = listEntity(specification, pageable);
        return convertListEntityToListResponse(anyEntityList);
    }

    Specification<AnyEntity> buildSpecification(Request request) {
        return (root, query, criteriaBuilder) -> {
            if(request.filter() != null && !request.filter().trim().isBlank() ){
                return criteriaBuilder.like(root.get("name"), "%" + request.filter() + "%");
            }else{
                return criteriaBuilder.conjunction();
            }
        };
    }

    List<ResponseItem> convertListEntityToListResponse(List<AnyEntity> anyEntity) {
        return anyEntity.stream().map(e ->  new ResponseItem(e.getId(), e.getName())).toList();
    }

    List<AnyEntity> listEntity(Specification<AnyEntity> specification, Pageable pageable) {
        return anyRepository.findAll(specification, pageable).getContent();
    }

    Pageable buildPageable(Request request) {
        final Sort defaultSortList  = Sort.by(Direction.DESC, "id");
        return PageRequest.of(
                request.numPage == null ? 0 : request.numPage, 
                request.sizePage == null ?  defaultSizePage : request.sizePage, 
                defaultSortList
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
