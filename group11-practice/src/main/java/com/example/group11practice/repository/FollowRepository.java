package com.example.group11practice.repository;

import com.example.group11practice.entity.Follow;
import com.example.group11practice.utils.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends BaseRepository<Follow, Long> {

    List<Follow> findByBeFollowedUserIdAndDeleted(Long beFollowedUserId, Boolean deleted);

    List<Follow> findByFollowingUserIdAndDeleted(Long followingUserId, Boolean deleted);
}
