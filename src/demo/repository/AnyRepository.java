package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import demo.model.AnyEntity;

@Repository
public interface AnyRepository  extends JpaRepository<AnyEntity,String>{
}
