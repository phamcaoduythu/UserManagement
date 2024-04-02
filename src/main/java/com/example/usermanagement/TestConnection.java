package com.example.usermanagement;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) throws Exception {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser("sa");
        ds.setPassword("12345");
        ds.setServerName("LAPTOP-P1F3R3OG\\SQLEXPRESS");
        ds.setDatabaseName("FSA_Phase_1");
        ds.setPortNumber(1433);
        try (Connection conn = ds.getConnection()) {
            System.out.println("Connection successful");
            System.out.println(conn.getMetaData());
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        }
    }

}
