package com.paj.api.services;

import jakarta.ejb.Local;
import com.paj.api.entities.UserEntity;

import java.util.List;

@Local
public interface UserService {
    UserEntity getUserById(int id);

    UserEntity getUserByEmail(String email);

    List<UserEntity> getAllUsers();

    UserEntity createUser(UserEntity user);

    void deleteUser(int id);
}
