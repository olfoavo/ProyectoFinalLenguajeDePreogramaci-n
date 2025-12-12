package com.gustavo.sakila.repo;

import com.gustavo.sakila.model.Language;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LanguageRepository implements CrudRepository<Language, Integer> {
    private final DataSource ds;

    public LanguageRepository(DataSource ds) { this.ds = ds; }

    private Language map(ResultSet rs) throws SQLException {
        return new Language(
                rs.getInt("language_id"),
                rs.getString("name"),
                rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC).toInstant()
        );
    }

    @Override
    public List<Language> findAll() throws SQLException {
        String sql = "SELECT language_id, name, last_update FROM language ORDER BY language_id";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Language> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    @Override
    public Optional<Language> findById(Integer id) throws SQLException {
        String sql = "SELECT language_id, name, last_update FROM language WHERE language_id = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Integer insert(Language entity) throws SQLException {
        String sql = "INSERT INTO language(name) VALUES (?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("No se generó ID para language");
    }

    @Override
    public boolean update(Language entity) throws SQLException {
        String sql = "UPDATE language SET name=? WHERE language_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.name());
            ps.setInt(2, entity.languageId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM language WHERE language_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM language";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    // Helper específico para renombrar
    public boolean rename(int id, String newName) throws SQLException {
        String sql = "UPDATE language SET name=? WHERE language_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}