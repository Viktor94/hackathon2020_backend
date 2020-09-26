package com.example.hackathon_backend.repositories;


import com.example.hackathon_backend.models.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findUserByUsername(String email);
}
