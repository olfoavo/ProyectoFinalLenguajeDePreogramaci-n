package com.gustavo.sakila;

import com.gustavo.sakila.db.DB;
import com.gustavo.sakila.manager.FilmActorManager;

import javax.sql.DataSource;

public class AppFilmActorDemo {
    public static void main(String[] args) {
        DataSource ds = DB.dataSource();
        FilmActorManager fam = new FilmActorManager(ds);

        // Ejemplo mínimo (no asume IDs válidos específicos)
        int filmId = 1;  // ajusta según tu BD
        int actorId = 1; // ajusta según tu BD

        boolean linked = fam.link(filmId, actorId);
        System.out.println("Link film-actor => " + linked);

        boolean unlinked = fam.unlink(filmId, actorId);
        System.out.println("Unlink film-actor => " + unlinked);
    }
}