package com.example.group11practice.repository;


import com.example.group11practice.entity.User;
import com.example.group11practice.utils.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    User findByLoginNameAndDeleted(String loginName, Boolean deleted);

}
