package de.zappler.bedwars.api.storage.mysql;

import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.api.storage.mysql.impl.MySQLModule;
import lombok.Getter;

import java.sql.*;
@Getter
public class MySQLConnnector {

    private MySQLModule mySQLModule;
    private Connection connection;

    public MySQLConnnector(ModuleManager moduleManager, String dictionary, String file) {
        new MySQLModule(dictionary, file, moduleManager);
        mySQLModule = (MySQLModule) moduleManager.getIModule(MySQLModule.class);
        connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "?autoReconnect=true", mySQLModule.getUSER(), mySQLModule.getPASSWORD());
            System.out.println("Connected to " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE());
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error while connecting to " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "Error: Wrong host, port, database or user/password");
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE());
            }
        } catch (SQLException e) {
            System.out.println("Error while disconnecting from " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "Error: " + e.getMessage());
        }
    }

    public void update(String qry, Object... objects) {
        try {
            PreparedStatement ps = connection.prepareStatement(qry);
            for (int i = 1; i <= objects.length; i++) {
                ps.setObject(i, objects[i - 1]);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet query(String qry, Object... objects) {
        ResultSet rs = null;

        try {
            PreparedStatement ps = connection.prepareStatement(qry);
            for (int i = 1; i <= objects.length; i++) {
                ps.setObject(i, objects[i - 1]);
            }
            rs = ps.executeQuery();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

    public boolean isConnected() {
        try {
            return connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }
}
