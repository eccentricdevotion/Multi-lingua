package me.eccentric_nz.plugins.multilingua;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MultilinguaDatabase {

    private static MultilinguaDatabase instance = new MultilinguaDatabase();
    public Connection connection = null;
    public Statement statement = null;
    private Multilingua plugin;

    public static synchronized MultilinguaDatabase getInstance() {
        return instance;
    }

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() {
        try {
            statement = connection.createStatement();
            String queryML = "CREATE TABLE IF NOT EXISTS multilingua (id INTEGER PRIMARY KEY NOT NULL, faction_id TEXT)";
            statement.executeUpdate(queryML);
        } catch (SQLException e) {
            plugin.debug("Create table error: " + e);
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
