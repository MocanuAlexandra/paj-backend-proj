package com.paj.api.services;

import jakarta.ejb.Local;
import com.paj.api.entities.RoleEntity;

import java.util.List;

@Local
public interface RoleService {
    RoleEntity getRoleById(int id);

    List<RoleEntity> getAllRoles();

    RoleEntity createRole(RoleEntity role);

    void deleteRole(int id);
}
