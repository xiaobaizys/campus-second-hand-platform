package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> getRoleById(Long id);

    Optional<Role> getRoleByName(String name);

    Role saveRole(Role role);

    void deleteRole(Long id);
}