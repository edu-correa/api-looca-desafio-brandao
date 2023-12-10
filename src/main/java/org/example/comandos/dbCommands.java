package org.example.comandos;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import org.example.Slack.AbrirChamado;
import org.example.conexao.Connection;
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

public class dbCommands implements IGeneralDbCommands {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";

    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";

    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";

    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m";
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE

    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE

    private JdbcTemplate con;
    private Integer fkAgencia;
    private Integer fkTipoMaquina;
    private String locale;
    private Machine machine;

    private AbrirChamado abrirChamado;
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");

    public dbCommands() {
        abrirChamado = new AbrirChamado();
        Connection connection = new Connection();
        con = connection.getCon();

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

        System.out.println(macAddress);
        List<Machine> resultados = con.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);

        Integer contadorDeResultados = resultados.size();
        if (contadorDeResultados == 1){
            System.out.println(GREEN_BOLD_BRIGHT + "\nConsultando maquina" + ANSI_RESET);
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
        con.update("INSERT INTO maquina VALUES (null, ?, ?, ?, ?, ?)", this.fkAgencia, this.fkTipoMaquina, macAddress, locale, IGeneralDbCommands.getMachineName());

        System.out.println( GREEN_BOLD_BRIGHT + "Maquina inserida com sucesso!" + ANSI_RESET);
        List<Machine> resultados = con.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);
        machine = resultados.get(0);
        terminal.askComponentes(this);
        System.out.println(BLUE_BOLD_BRIGHT + "Prosseguindo com teste de inserção!" + ANSI_RESET);
        searchByMacAddress();
    }
    public void startGathering() throws InterruptedException {
        Looca luquinhas = new Looca();
        List<Components> resultados = con.query("SELECT componente.* FROM componente JOIN maquinaComponente on fkComponente = idComponente" +
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
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 1);
        System.out.println("Processador inserido");

        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 4);

        System.out.println( PURPLE_BOLD_BRIGHT + "Temperatura adicionada" + ANSI_RESET);
    }



    @Override
    public void inserirRam() {
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 2);
        System.out.println( BLUE_BOLD_BRIGHT + "RAM inserida" + ANSI_RESET);
    }

    @Override
    public void inserirDisco() {
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", this.machine.idMaquina(), 3);
        System.out.println(YELLOW_BOLD_BRIGHT + "Disco inserido" + ANSI_RESET);
    }

    public void inserirDadosProcessador(Looca lucas){
        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                1,  dfSharp.format(lucas.getProcessador().getUso()));

        if((lucas.getProcessador().getUso() > 70)){
            System.out.println(BLUE_BOLD_BRIGHT +"Processador, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"processador",lucas.getProcessador().getUso(),3);
        }else if(lucas.getProcessador().getUso() > 50){
            System.out.println(BLUE_BOLD_BRIGHT +"Processador, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"processador",lucas.getProcessador().getUso(),2);
        }

        System.out.println( PURPLE_BOLD_BRIGHT + "Uso de processador: " + dfSharp.format(lucas.getProcessador().getUso()) + ANSI_RESET);
        inserirDadosTemperatura(lucas);
    }

    public void inserirDadosTemperatura(Looca lucas){
        Temperatura temperatura = lucas.getTemperatura();
        Double temperaturaEscrita = temperatura.getTemperatura();

        con.update("INSERT INTO registros(fkMaquina, fkComponente, valor, dataHora)VALUES (?,?,?,now())",
                this.machine.idMaquina(),4,temperaturaEscrita);

        if(temperaturaEscrita > 80){
            System.out.println(BLUE_BOLD_BRIGHT +"Temperatura, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"temperatura CPU",temperaturaEscrita,3);
        }else if(temperaturaEscrita > 60){
            System.out.println(BLUE_BOLD_BRIGHT +"Temperatura, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"temperatura CPU",temperaturaEscrita,2);
        }

        System.out.println(PURPLE_BOLD_BRIGHT + "Temperatura de CPU em ºC: " + temperaturaEscrita + ANSI_RESET);
    }

    public void inserirDadosRAM(Looca lucas){
        Double ramAtual = lucas.getMemoria().getEmUso().doubleValue();
        Double ramTotal = lucas.getMemoria().getTotal().doubleValue();
        Double porcentagem = (ramAtual / ramTotal) * 100;

        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                2,  dfSharp.format(porcentagem));

        if(porcentagem > 80){
            System.out.println(BLUE_BOLD_BRIGHT +"RAM, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome()," Memória RAM",porcentagem,3);
        }else if(porcentagem > 60){
            System.out.println(BLUE_BOLD_BRIGHT +"RAM, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"Memória RAM",porcentagem,2);
        }

        System.out.println( BLUE_BOLD_BRIGHT + "Uso de Ram: " + dfSharp.format(porcentagem) + ANSI_RESET);
    }

    public void inserirDadosDisco(Looca lucas){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Double max = (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824;
        Double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824;
        Double perc = max / commited;
        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                3,  dfSharp.format(perc));

        if(perc > 80){
            System.out.println(BLUE_BOLD_BRIGHT +"Disco, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"Disco",perc,3);
        }else if(perc > 60){
            System.out.println(BLUE_BOLD_BRIGHT +"Disco, chamado aberto" + ANSI_RESET);
            abrirChamado.AbrirChamado(machine.nome(),"Disco",perc,2);
        }

        System.out.println(YELLOW_BOLD_BRIGHT + "Uso de disco: " + dfSharp.format(perc) + ANSI_RESET);
    }

}
