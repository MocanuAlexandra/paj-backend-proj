package org.example.api.services;

import jakarta.ejb.Local;
import org.example.api.entities.RoleEntity;

import java.util.List;

@Local
public interface RoleService {
    RoleEntity getRole(int id);

    List<RoleEntity> getAllRoles();

    RoleEntity createRole(RoleEntity role);

    void deleteRole(int id);
}
