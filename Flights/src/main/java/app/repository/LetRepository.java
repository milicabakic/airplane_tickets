package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import app.entities.Avion;
import app.entities.Let;

@Repository
public interface LetRepository extends JpaRepository<Let, Long>, JpaSpecificationExecutor<Let>{
	
	List<Let> findByAvion(Avion avion); 
	
	Let findById(long id);
	
	List<Let> findAll();

}
