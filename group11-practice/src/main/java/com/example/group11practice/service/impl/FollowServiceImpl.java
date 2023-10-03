package com.example.group11practice.service.impl;

import com.example.group11practice.entity.Follow;
import com.example.group11practice.model.FollowModel;
import com.example.group11practice.repository.FollowRepository;
import com.example.group11practice.service.FollowService;
import com.example.group11practice.utils.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FollowServiceImpl extends BaseServiceImpl<FollowModel, Follow, Long> implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Override
    protected Class<FollowModel> getModelType() {
        return FollowModel.class;
    }

    @Override
    protected Class<Follow> getEntityType() {
        return Follow.class;
    }
}
