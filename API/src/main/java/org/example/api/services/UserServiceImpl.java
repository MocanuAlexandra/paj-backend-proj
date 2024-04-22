package org.example.api.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.example.api.dao.UserDao;
import org.example.api.entities.UserEntity;

import java.util.List;

@Stateless
public class UserServiceImpl implements UserService {
    @EJB
    private UserDao userDao;

    @Override
    public UserEntity getUser(int id) {
        return userDao.find(id);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return userDao.save(user);
    }

    @Override
    public void deleteUser(int id) {
        UserEntity userEntity = userDao.find(id);
        if (userEntity != null) {
            userDao.delete(userEntity);
        }
    }
}
