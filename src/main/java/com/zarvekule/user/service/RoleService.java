package com.zarvekule.user.service;

import com.zarvekule.user.entity.Role;
import com.zarvekule.user.enums.ERole;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(ERole name);

    List<Role> getAllRoles();
}