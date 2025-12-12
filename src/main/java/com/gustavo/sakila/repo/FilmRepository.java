package com.gustavo.sakila.repo;

import com.gustavo.sakila.model.Film;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilmRepository implements CrudRepository<Film, Integer> {
    private final DataSource ds;

    public FilmRepository(DataSource ds) { this.ds = ds; }

    private Film map(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("language_id"),
                rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC).toInstant()
        );
    }

    @Override
    public List<Film> findAll() throws SQLException {
        String sql = "SELECT film_id, title, description, language_id, last_update FROM film ORDER BY film_id LIMIT 1000";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Film> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    @Override
    public Optional<Film> findById(Integer id) throws SQLException {
        String sql = "SELECT film_id, title, description, language_id, last_update FROM film WHERE film_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Integer insert(Film entity) throws SQLException {
        String sql = "INSERT INTO film(title, description, language_id) VALUES (?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.title());
            ps.setString(2, entity.description());
            ps.setInt(3, entity.languageId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("No se generó ID para film");
    }

    @Override
    public boolean update(Film entity) throws SQLException {
        String sql = "UPDATE film SET title=?, description=?, language_id=? WHERE film_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.title());
            ps.setString(2, entity.description());
            ps.setInt(3, entity.languageId());
            ps.setInt(4, entity.filmId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM film WHERE film_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM film";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}