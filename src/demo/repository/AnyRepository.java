package demo.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import demo.model.AnyEntity;

@Repository
public interface AnyRepository  extends CrudRepository<AnyEntity,String>, JpaSpecificationExecutor<AnyEntity>{
}
