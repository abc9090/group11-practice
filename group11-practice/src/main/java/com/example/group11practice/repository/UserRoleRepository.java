package com.example.group11practice.repository;

import com.example.group11practice.entity.User;
import com.example.group11practice.entity.UserRole;
import com.example.group11practice.utils.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, Long> {
    List<UserRole> findByUserIdAndDeleted(Long userId, Boolean deleted);
}
