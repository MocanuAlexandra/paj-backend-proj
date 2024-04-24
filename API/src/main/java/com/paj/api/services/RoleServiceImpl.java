package org.example.api.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.example.api.dao.RoleDao;
import org.example.api.entities.RoleEntity;

import java.util.List;

@Stateless
public class RoleServiceImpl implements RoleService {

    @EJB
    private RoleDao roleDao;

    @Override
    public RoleEntity getRole(int id) {
        return roleDao.find(id);
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleDao.findAll();
    }

    @Override
    public RoleEntity createRole(RoleEntity role) {
        return roleDao.save(role);
    }

    @Override
    public void deleteRole(int id) {
        RoleEntity roleEntity = roleDao.find(id);
        if (roleEntity != null) {
            roleDao.delete(roleEntity);
        }
    }
}
