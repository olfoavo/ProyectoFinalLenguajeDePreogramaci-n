package com.gustavo.sakila.repo;

import com.gustavo.sakila.model.Actor;
import com.gustavo.sakila.model.Film;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorRepository implements CrudRepository<Actor, Integer> {
    private final DataSource ds;

    public ActorRepository(DataSource ds) { this.ds = ds; }

    private Actor map(ResultSet rs) throws SQLException {
        return new Actor(
                rs.getInt("actor_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC).toInstant()
        );
    }

    @Override
    public List<Actor> findAll() throws SQLException {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor ORDER BY actor_id";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Actor> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    @Override
    public Optional<Actor> findById(Integer id) throws SQLException {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor WHERE actor_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Integer insert(Actor entity) throws SQLException {
        String sql = "INSERT INTO actor(first_name, last_name) VALUES (?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.firstName());
            ps.setString(2, entity.lastName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("No se generó ID para actor");
    }

    @Override
    public boolean update(Actor entity) throws SQLException {
        String sql = "UPDATE actor SET first_name=?, last_name=? WHERE actor_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.firstName());
            ps.setString(2, entity.lastName());
            ps.setInt(3, entity.actorId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM actor WHERE actor_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM actor";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    // Extras
    public List<Actor> findActorsByFilmId(int filmId) throws SQLException {
        String sql = """
            SELECT a.actor_id, a.first_name, a.last_name, a.last_update
            FROM actor a
            JOIN film_actor fa ON fa.actor_id = a.actor_id
            WHERE fa.film_id = ?
            ORDER BY a.actor_id
        """;
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Actor> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public List<Film> findFilmsByActorId(int actorId) throws SQLException {
        String sql = """
            SELECT f.film_id, f.title, f.description, f.language_id, f.last_update
            FROM film f
            JOIN film_actor fa ON fa.film_id = f.film_id
            WHERE fa.actor_id = ?
            ORDER BY f.film_id
        """;
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, actorId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Film> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Film(
                            rs.getInt("film_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("language_id"),
                            rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC).toInstant()
                    ));
                }
                return out;
            }
        }
    }
}