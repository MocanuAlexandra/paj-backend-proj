package org.example.api.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.api.entities.RoleEntity;

import java.util.List;

@Stateless
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RoleEntity find(Integer id) {
        return entityManager.find(RoleEntity.class, id);
    }

    @Override
    public List<RoleEntity> findAll() {
        return entityManager.createQuery("SELECT r FROM RoleEntity r", RoleEntity.class).getResultList();
    }

    @Override
    public RoleEntity save(RoleEntity roleEntity) {
        if (roleEntity.getRoleId() == 0) {
            entityManager.persist(roleEntity);
        } else {
            entityManager.merge(roleEntity);
        }
        return roleEntity;
    }

    @Override
    public void delete(RoleEntity roleEntity) {
        entityManager.remove(entityManager.contains(roleEntity) ? roleEntity : entityManager.merge(roleEntity));
    }
}
