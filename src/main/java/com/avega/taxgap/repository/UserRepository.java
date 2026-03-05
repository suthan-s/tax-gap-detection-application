package com.avega.taxgap.repository;

import com.avega.taxgap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findOneByEmailAndPassword(String email, String password);
}
