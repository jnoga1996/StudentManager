package com.smanager.dao.repositories;

import com.smanager.dao.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
