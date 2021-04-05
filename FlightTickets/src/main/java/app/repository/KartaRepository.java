package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.entities.Karta;

public interface KartaRepository extends JpaRepository<Karta, Long> {
	
	List<Karta> findByIdLeta(long idLeta);
	
	@Query("select k.idKorisnika from Karta k where k.idLeta = :idLeta")
	List<Long> findUsersByIdLeta(long idLeta);
	
	@Query("select count(k) from Karta k where k.idLeta = :idLeta")
	int countTicketsByIdLeta(long idLeta);
	
	@Query("select k from Karta k where k.idKorisnika = :idKorisnika order by k.datumKupovine asc")
	List<Karta> findByIdKorisnikaSorted(long idKorisnika);

	
	Karta findById(long id);
	
}
