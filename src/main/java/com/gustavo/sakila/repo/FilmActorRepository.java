package com.gustavo.sakila.repo;

import com.gustavo.sakila.model.Actor;
import com.gustavo.sakila.model.Film;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmActorRepository {
    private final DataSource ds;

    public FilmActorRepository(DataSource ds) { this.ds = ds; }

    public boolean addLink(int filmId, int actorId) throws SQLException {
        String sql = "INSERT INTO film_actor(film_id, actor_id) VALUES (?, ?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, actorId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeLink(int filmId, int actorId) throws SQLException {
        String sql = "DELETE FROM film_actor WHERE film_id=? AND actor_id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, actorId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Film> findFilmsByActor(int actorId) throws SQLException {
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
                            rs.getTimestamp("last_update").toInstant()
                    ));
                }
                return out;
            }
        }
    }

    public List<Actor> findActorsByFilm(int filmId) throws SQLException {
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
                while (rs.next()) {
                    out.add(new Actor(
                            rs.getInt("actor_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getTimestamp("last_update").toInstant()
                    ));
                }
                return out;
            }
        }
    }
}