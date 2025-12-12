// src/main/java/com/gustavo/sakila/AppManagersDemo.java
package com.gustavo.sakila;

import com.gustavo.sakila.db.DB;
import com.gustavo.sakila.manager.CategoryManager;
import com.gustavo.sakila.manager.FilmManager;
import com.gustavo.sakila.manager.LanguageManager;

import javax.sql.DataSource;

public class AppManagersDemo {
    public static void main(String[] args) {
        System.out.println("=== Demo Sakila: Managers (Language/Category/Film) ===");

        // Pool Hikari centralizado
        DataSource ds = DB.dataSource();

        // Managers que reciben DataSource
        LanguageManager langMgr = new LanguageManager(ds);
        CategoryManager catMgr  = new CategoryManager(ds);
        FilmManager filmMgr     = new FilmManager(ds);

        System.out.printf(
            "Registros -> language: %d | category: %d | film: %d%n",
            langMgr.findAll().size(),
            catMgr.findAll().size(),
            filmMgr.findAll().size()
        );

        // CRUD de ejemplo sobre language
        int newId       = langMgr.create("NEO_LANG");
        boolean updated = langMgr.rename(newId, "ANDERSON_LANG");
        boolean deleted = langMgr.delete(newId);

        System.out.printf("Creado language_id=%d%n", newId);
        System.out.printf("Actualizado? %s%n", updated);
        System.out.printf("Borrado? %s%n", deleted);
        System.out.println("OK.");
    }
}