package org.example;

import com.sun.jna.platform.unix.X11;
import org.springframework.jdbc.core.BeanPropertyRowMapper.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.crypto.Mac;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class dbCommands {
    private JdbcTemplate con;
    private Integer fkAgencia;
    private Integer fkTipoMaquina;
    private String locale;
    private Machine machine;
    public dbCommands() {
        Connection connection = new Connection();
        Terminal terminal = new Terminal();
        con = connection.getCon();
        fkAgencia = terminal.askFkAgencia();
        fkTipoMaquina = terminal.askTipoMaquina();
        locale = terminal.askLocal();
    }

    private String getMacAddress(){
        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Endereço de ipv4 atual: " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();


            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            System.out.print("MAC address atual: " + sb.toString());

            return sb.toString();

        } catch (UnknownHostException e) {
            System.out.println("catched");
            e.printStackTrace();
        } catch (SocketException e){
            System.out.println("catched");
            e.printStackTrace();
        }
        return null;
    }

    private String getMachineName(){
        try{
            String machineName = InetAddress.getLocalHost().getHostName();
            return machineName;
        }catch (Exception e){
            System.out.println("Exception caught ="+e.getMessage());
        }
        return null;
    }

    public void searchByMacAddress(){
        String macAddress = getMacAddress();

        List<Machine> resultados = con.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);

        Integer contadorDeResultados = resultados.size();
        if (contadorDeResultados == 1){
            System.out.println("\nConsultando maquina");
            for (Machine m : resultados){
                this.machine = m;
                System.out.println(m);
            }

        } else{
            if (contadorDeResultados > 1){
                System.out.println("Erro de banco, mais de uma máquina com o mesmo Mac Address");
            } else{
                System.out.println("\nMaquina ainda não existe: prosseguindo para criação dela.");
                insertNewMachine(macAddress);
            }
        }
    }

    private void insertNewMachine(String macAddress){
        con.update("INSERT INTO maquina VALUES (null, ?, ?, ?, ?, ?)", this.fkAgencia, this.fkTipoMaquina, macAddress, locale, getMachineName());
        System.out.println("Maquina inserida com sucesso!");
        System.out.println("Prosseguindo com teste de inserção!");
        searchByMacAddress();
    }

    private void startGathering(){
        System.out.println("ainda em produção!");
    }
}
