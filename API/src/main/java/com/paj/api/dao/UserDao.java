package com.paj.api.dao;

import com.paj.api.entities.UserEntity;

public interface UserDao extends GenericDao<UserEntity, Integer> {

    // Find user by email
    UserEntity findByEmail(String email);
}
