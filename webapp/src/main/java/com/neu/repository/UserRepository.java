package com.neu.repository;

import com.neu.pojo.User;
import com.neu.pojo.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetails, String> {
}
