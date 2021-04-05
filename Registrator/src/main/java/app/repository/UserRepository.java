package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	boolean existsByEmail(String email);
	
	User findById(long id);
	
	@Query("select distinct u from User u left join fetch u.kartice where u.email like :email")
	User findUserWithCreditCards(String email);
}
