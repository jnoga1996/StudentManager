package com.smanager.dao.repositories;

import com.smanager.dao.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from users where username = lower(:username)", nativeQuery = true)
    User getUserByUsername(@Param("username") String username);
}
