package com.gustavo.sakila.repo;

import com.gustavo.sakila.model.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository implements CrudRepository<Category, Integer> {
    private final DataSource ds;

    public CategoryRepository(DataSource ds) { this.ds = ds; }

    private Category map(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC).toInstant()
        );
    }

    @Override
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT category_id, name, last_update FROM category ORDER BY category_id";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Category> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    @Override
    public Optional<Category> findById(Integer id) throws SQLException {
        String sql = "SELECT category_id, name, last_update FROM category WHERE category_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Integer insert(Category entity) throws SQLException {
        String sql = "INSERT INTO category(name) VALUES (?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("No se generó ID para category");
    }

    @Override
    public boolean update(Category entity) throws SQLException {
        String sql = "UPDATE category SET name=? WHERE category_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.name());
            ps.setInt(2, entity.categoryId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM category WHERE category_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM category";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}