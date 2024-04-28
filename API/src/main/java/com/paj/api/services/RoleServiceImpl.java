package com.paj.api.services;

import com.paj.api.dao.RoleDao;
import com.paj.api.entities.RoleEntity;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class RoleServiceImpl implements RoleService {

    @EJB
    private RoleDao roleDao;

    @Override
    public RoleEntity getRoleById(int id) {
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
