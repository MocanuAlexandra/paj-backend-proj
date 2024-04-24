package org.example.api.services;

import jakarta.ejb.Local;
import org.example.api.entities.UserEntity;

import java.util.List;

@Local
public interface UserService {
    UserEntity getUser(int id);

    List<UserEntity> getAllUsers();

    UserEntity createUser(UserEntity user);

    void deleteUser(int id);
}
