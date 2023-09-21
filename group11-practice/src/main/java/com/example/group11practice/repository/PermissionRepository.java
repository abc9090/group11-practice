package com.example.group11practice.repository;

import com.example.group11practice.entity.Permission;
import com.example.group11practice.utils.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
}
