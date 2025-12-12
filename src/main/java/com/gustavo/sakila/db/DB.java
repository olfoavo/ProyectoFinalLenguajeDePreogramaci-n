package com.gustavo.sakila.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Proveedor de DataSource/Connection sin pool.
 * Lee credenciales desde:
 *  - System properties: sakila.db.url / sakila.db.user / sakila.db.pass
 *  - Variables de entorno: SAKILA_DB_URL / SAKILA_DB_USER / SAKILA_DB_PASS
 * Fallback por defecto a localhost/sakila con usuario root y pass vacío.
 */
public final class DB {

    private static final String URL  = pick("sakila.db.url",  "SAKILA_DB_URL",
            "jdbc:mysql://127.0.0.1:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = pick("sakila.db.user", "SAKILA_DB_USER", "root");
    private static final String PASS = pick("sakila.db.pass", "SAKILA_DB_PASS", "");

    private static final DataSource DS = new SimpleDataSource(URL, USER, PASS);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8+
        } catch (ClassNotFoundException ignore) { /* el driver se carga con SPI si está en el classpath */ }
    }

    private DB() {}

    private static String pick(String sysKey, String envKey, String defVal) {
        String v = System.getProperty(sysKey);
        if (v == null || v.isBlank()) v = System.getenv(envKey);
        return (v == null || v.isBlank()) ? defVal : v;
    }

    /** DataSource (compat) */
    public static DataSource dataSource() { return DS; }

    /** Alias (compat con código previo) */
    public static DataSource get() { return DS; }

    /** Connection directa con checked exception */
    public static Connection getConnection() throws SQLException { return DS.getConnection(); }

    /** Connection directa con RuntimeException (compat con repos antiguos que llamaban DB.con()) */
    public static Connection con() {
        try { return DS.getConnection(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** DataSource simple sin pool */
    private static final class SimpleDataSource implements DataSource {
        private final String url, user, pass;
        private volatile int timeout;
        private volatile java.io.PrintWriter logWriter;

        private SimpleDataSource(String url, String user, String pass) {
            this.url  = Objects.requireNonNull(url);
            this.user = user;
            this.pass = pass;
        }

        @Override public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, pass);
        }
        @Override public Connection getConnection(String u, String p) throws SQLException {
            return DriverManager.getConnection(url, u, p);
        }
        @Override public java.io.PrintWriter getLogWriter() { return logWriter; }
        @Override public void setLogWriter(java.io.PrintWriter out) { this.logWriter = out; }
        @Override public void setLoginTimeout(int seconds) {
            this.timeout = seconds;
            DriverManager.setLoginTimeout(seconds);
        }
        @Override public int getLoginTimeout() { return timeout; }
        @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException("No parent logger");
        }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("Not a wrapper"); }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }
}