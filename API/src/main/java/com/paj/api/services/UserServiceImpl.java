package com.paj.api.services;

import com.paj.api.dao.UserDao;
import com.paj.api.entities.UserEntity;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class UserServiceImpl implements UserService {
    @EJB
    private UserDao userDao;

    @Override
    public UserEntity getUserById(int id) {
        return userDao.find(id);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userDao.findByEmail(email);
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
