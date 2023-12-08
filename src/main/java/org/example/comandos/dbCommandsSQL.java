package org.example.comandos;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import org.example.conexao.ConnectionSQL;
import org.example.comandos.IGeneralDbCommands;
import org.example.DAO.Components;
import org.example.DAO.Machine;
import org.example.main.Terminal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.DataClassRowMapper;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.text.DecimalFormat;

public class dbCommandsSQL implements IGeneralDbCommands {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";

    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";

    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";

    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m";
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE

    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    private JdbcTemplate conSQL;
    private Integer fkAgencia;
    private Integer fkTipoMaquina;
    private String locale;
    private Machine machine;
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");

    public dbCommandsSQL() {
        ConnectionSQL connection = new ConnectionSQL();
        conSQL = connection.getCon();
    }


    public void iniciate() throws InterruptedException {
        Terminal terminal = new Terminal();
        fkAgencia = terminal.askFkAgencia();
        fkTipoMaquina = terminal.askTipoMaquina();
        locale = terminal.askLocal();

        searchByMacAddress();
    }


    @Override
    public void searchByMacAddress() throws InterruptedException {
        String macAddress = IGeneralDbCommands.getMacAddress();

        List<Machine> resultados = conSQL.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);

        Integer contadorDeResultados = resultados.size();
        if (contadorDeResultados == 1){
            System.out.println(PURPLE_BOLD_BRIGHT + "\nConsultando maquina" + ANSI_RESET);
            for (Machine m : resultados){
                this.machine = m;
                System.out.println(m);
                startGathering();
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

    @Override
    public void insertNewMachine(String macAddress) throws InterruptedException {
        Terminal terminal = new Terminal();
        conSQL.update("INSERT INTO maquina VALUES (null, ?, ?, ?, ?, ?)", this.fkAgencia, this.fkTipoMaquina, macAddress, locale, IGeneralDbCommands.getMachineName());
        System.out.println("Maquina inserida com sucesso!");
        List<Machine> resultados = conSQL.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);
        machine = resultados.get(0);

        terminal.askComponentes(this);
        System.out.println(GREEN_BOLD_BRIGHT + "Prosseguindo com teste de inserção!" + ANSI_RESET);
        searchByMacAddress();
    }
    public void startGathering() throws InterruptedException {
        Looca luquinhas = new Looca();
        List<Components> resultados = conSQL.query("SELECT componente.* FROM componente JOIN maquinaComponente on fkComponente = idComponente" +
                        " WHERE fkMaquina = ?",
                new DataClassRowMapper<>(Components.class),
                this.machine.idMaquina());

        while (true){
            for (Components resultado : resultados) {
                if (resultado.idComponente() == 1){
                    inserirDadosProcessador(luquinhas);
                } else if (resultado.idComponente() == 2){
                    inserirDadosRAM(luquinhas);
                } else if (resultado.idComponente() == 3){
                    inserirDadosDisco(luquinhas);
                }
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @Override
    public void inserirProcessador() {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 1);
        System.out.println(GREEN_BOLD_BRIGHT + "Processador inserido" + ANSI_RESET);
    }

    @Override
    public void inserirRam() {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 2);
        System.out.println(GREEN_BOLD_BRIGHT + "RAM inserida" + ANSI_RESET);
    }

    @Override
    public void inserirDisco() {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 3);
        System.out.println( GREEN_BOLD_BRIGHT + "Disco inserido" + ANSI_RESET);
    }

    public void inserirDadosProcessador(Looca lucas){
        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                1,  dfSharp.format(lucas.getProcessador().getUso()));
        System.out.println(CYAN_BOLD_BRIGHT + "Uso de processador: " +dfSharp.format(lucas.getProcessador().getUso()) + ANSI_RESET);
        inserirDadosTemperatura(lucas);
    }

    public void inserirDadosTemperatura(Looca lucas){
        Temperatura temperatura = lucas.getTemperatura();
        String temperaturaEscrita = dfSharp.format(temperatura.getTemperatura());

        conSQL.update("INSERT INTO registros(fkMaquina, fkComponente, valor, dataHora)VALUES (?,?,?,now())",
                this.machine.idMaquina(),4,temperaturaEscrita);

        System.out.println( CYAN_BOLD_BRIGHT + "Temperatura de CPU em ºC: " + temperaturaEscrita + ANSI_RESET);
    }
    public void inserirDadosRAM(Looca lucas){
        Double ramAtual = lucas.getMemoria().getEmUso().doubleValue();
        Double ramTotal = lucas.getMemoria().getTotal().doubleValue();
        Double porcentagem = (ramAtual / ramTotal) * 100;

        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                2,  dfSharp.format(porcentagem));
        System.out.println(GREEN_BOLD_BRIGHT + "Uso de Ram: " + dfSharp.format(porcentagem) + ANSI_RESET);
    }

    public void inserirDadosDisco(Looca lucas){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Double max = (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824;
        Double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824;
        Double perc = max / commited;
        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                3,  dfSharp.format(perc));
        System.out.println( YELLOW_BOLD_BRIGHT + "Uso de disco: " + dfSharp.format(perc) + ANSI_RESET);
    }

}
