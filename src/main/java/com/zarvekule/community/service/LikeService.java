package com.zarvekule.community.service;

import com.zarvekule.community.dto.LikeRequest;
import com.zarvekule.community.enums.TargetType;

public interface LikeService {


    boolean toggleLike(String authenticatedUsername, LikeRequest request);

    long getLikeCount(TargetType targetType, Long targetId);

    void updateTargetEntityLikeCount(TargetType targetType, Long targetId, long newCount);
}