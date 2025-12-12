package com.gustavo.sakila.repo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, K> {
    List<T> findAll() throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    K insert(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(K id) throws SQLException;
    long count() throws SQLException;
}