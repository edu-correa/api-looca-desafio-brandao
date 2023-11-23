package org.example;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConnectionSQL {
    //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
    private JdbcTemplate con;

    public ConnectionSQL(){
        BasicDataSource banco = new BasicDataSource();
        banco.setDriverClassName("com.mysql.cj.jdbc.Driver");
        banco.setUrl("jdbc:sqlserver://banksecure\\banksecure:3306;");
        banco.setUsername("root");
        banco.setPassword("urubu100");
        con = new JdbcTemplate(banco);
    }

    public JdbcTemplate getCon() {
        return con;
    }
}
