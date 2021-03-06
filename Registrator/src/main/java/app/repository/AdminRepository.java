package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{

	Admin findByUsername(String username);
}
