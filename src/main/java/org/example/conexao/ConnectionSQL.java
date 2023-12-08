package org.example.conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import  java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.DriverManager;

public class ConnectionSQL {
    String url = "jdbc:sqlserver://52.5.117.13:1433;databaseName=bankSecure";
    private JdbcTemplate conexao;

    public ConnectionSQL(){

        BasicDataSource banco = new BasicDataSource();
        banco.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        banco.setUrl(url);
        banco.setUsername("sa");
        banco.setPassword("urubu100");
        conexao = new JdbcTemplate(banco);

    }

    public JdbcTemplate getCon() {
        return conexao;
    }
}
