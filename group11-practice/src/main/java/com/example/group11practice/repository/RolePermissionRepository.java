package com.example.group11practice.repository;

import com.example.group11practice.entity.RolePermission;
import com.example.group11practice.utils.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, Long> {

    List<RolePermission> findByRoleIdInAndDeleted(List<Long> roleIdList, Boolean deleted);

}
