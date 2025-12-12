package com.gustavo.sakila.manager;

import com.gustavo.sakila.repo.CrudRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Base genérico para Managers que envuelve un CrudRepository<T, Integer>
 * y expone operaciones comunes con manejo de excepciones.
 */
public abstract class BaseManager<T> {
    protected final CrudRepository<T, Integer> repo;

    protected BaseManager(CrudRepository<T, Integer> repo) {
        this.repo = repo;
    }

    /** Cuenta registros */
    public long count() {
        try {
            return repo.count();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Lista todos los registros (para AppManagersDemo.findAll()) */
    public List<T> findAll() {
        try {
            return repo.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Busca por id (opcional, por si lo necesitas luego) */
    public Optional<T> findById(int id) {
        try {
            return repo.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Elimina por id (genérico) */
    public boolean delete(int id) {
        try {
            return repo.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}