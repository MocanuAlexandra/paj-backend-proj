package com.paj.api.dao;

import java.util.List;

public interface GenericDao<T, ID> {
    T find(ID id);

    List<T> findAll();

    T save(T entity);

    void delete(T entity);
}
