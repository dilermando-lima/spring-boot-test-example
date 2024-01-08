package demo.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import demo.model.AnyEntity;

@Repository
public interface AnyRepository  extends CrudRepository<AnyEntity,String>, JpaSpecificationExecutor<AnyEntity>{

    default List<AnyEntity> listByFilter(Integer numPage, Integer sizePage, String filter){

        final Specification<AnyEntity> filterSpecification = (root, query, criteriaBuilder) -> {
            if(filter != null && !filter.trim().isBlank() ){
                return criteriaBuilder.like(root.get("name"), "%" + filter + "%");
            }else{
                return criteriaBuilder.conjunction();
            }
        };

        return this.findAll(filterSpecification, PageRequest.of(numPage, sizePage, Sort.by(Direction.DESC, "id"))).getContent();

    }
}
