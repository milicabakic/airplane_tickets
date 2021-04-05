package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Avion;

@Repository
public interface AvionRepository extends JpaRepository<Avion, Long>{
	
	Avion findByNazivAndKapacitetPutnika(String naziv, int kapacitetPutnika);
	
	Avion findById(long id);

}
