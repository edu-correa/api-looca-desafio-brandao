package org.example.conexao;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Connection {
    private JdbcTemplate con;

    public Connection(){
        BasicDataSource banco = new BasicDataSource();
        banco.setDriverClassName("com.mysql.cj.jdbc.Driver");
        banco.setUrl("jdbc:mysql://localhost:3306/bankSecure?useTimezone=true&serverTimezone=UTC");
        banco.setUsername("root");
        banco.setPassword("220807");
        con = new JdbcTemplate(banco);
    }

    public JdbcTemplate getCon() {
        return con;
    }
}
