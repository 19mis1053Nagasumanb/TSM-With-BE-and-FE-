package com.example.taskmangementsystem.g.repositorys;

import com.example.taskmangementsystem.g.models.App_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<App_User, Long> {

    @Query("SELECT u FROM App_User u WHERE LOWER(u.username) = LOWER(:username)")
    App_User findByUsernameIgnoreCase(@Param("username") String username);


    App_User findByEmail(String email);

    App_User findByEmailIgnoreCase(String username);

//    App_User findByUsername(String username);
}

