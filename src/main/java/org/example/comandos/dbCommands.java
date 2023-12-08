package org.example.comandos;

import com.github.britooo.looca.api.core.Looca;
import org.example.conexao.Connection;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import org.example.DAO.Components;
import org.example.DAO.Machine;
import org.example.conexao.ConnectionSQL;
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
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");

    public dbCommands() {
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
    public void askComponentes(){
        Scanner numScan = new Scanner(System.in);
        Integer resposta  = 0;
        Boolean alreadyProcessador = false, alreadyRam = false, alreadyDisco = false, alreadyAny = false, wannaStop = false;
        do {
            wannaStop = false;
            System.out.println(WHITE_BOLD_BRIGHT + "Quais componentes deseja?" + ANSI_RESET);
            if (!alreadyProcessador && !alreadyRam && !alreadyDisco){
                System.out.println(PURPLE_BOLD_BRIGHT + "1 - Processador" + ANSI_RESET);
                System.out.println(BLUE_BOLD_BRIGHT + "2 - RAM" + ANSI_RESET);
                System.out.println(YELLOW_BOLD_BRIGHT + "3 - Disco" + ANSI_RESET);
            }
            else {
                if (!alreadyProcessador){
                    System.out.println(PURPLE_BOLD_BRIGHT + "1 - Processador" + ANSI_RESET);
                }
                if (!alreadyRam){
                    System.out.println(BLUE_BOLD_BRIGHT + "2 - RAM" + ANSI_RESET);
                }
                if (!alreadyDisco){
                    System.out.println(YELLOW_BOLD_BRIGHT + "3 - Disco" + ANSI_RESET);
                }
                System.out.println(WHITE_BOLD_BRIGHT + "4 - Sair" + ANSI_RESET);
            }
            resposta = numScan.nextInt();
            if (resposta == 1 && !alreadyProcessador){
                inserirProcessador(this.machine.idMaquina());
                alreadyProcessador = true;
                alreadyAny = true;
            } else if (resposta == 2 && !alreadyRam){
                inserirRam(this.machine.idMaquina());
                alreadyRam = true;
                alreadyAny = true;
            } else if (resposta == 3 && !alreadyDisco) {
                inserirDisco(this.machine.idMaquina());
                alreadyDisco = true;
                alreadyAny = true;
            } else if (resposta ==4){
                if (alreadyAny){
                    wannaStop = true;
                }
            }
            System.out.println(resposta != 4 && alreadyAny);

        } while (!wannaStop);

    }
    @Override
    public void insertNewMachine(String macAddress) throws InterruptedException {
        con.update("INSERT INTO maquina VALUES (null, ?, ?, ?, ?, ?, ?)", this.fkAgencia, this.fkTipoMaquina, macAddress, locale, IGeneralDbCommands.getMachineName(), getSo());
        System.out.println( GREEN_BOLD_BRIGHT + "Maquina inserida com sucesso!" + ANSI_RESET);
        List<Machine> resultados = con.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);
        machine = resultados.get(0);
        askComponentes();
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
            inserirServicosAtivos(luquinhas);
            inserirServicosInativos(luquinhas);

            TimeUnit.SECONDS.sleep(2);
        }
    }

    @Override
    public void inserirProcessador(Integer idMaquina) {
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 1);
        System.out.println("Processador inserido");

        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 4);

        System.out.println( PURPLE_BOLD_BRIGHT + "Temperatura adicionada" + ANSI_RESET);
    }



    @Override
    public void inserirRam(Integer idMaquina) {
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 2);
        System.out.println( BLUE_BOLD_BRIGHT + "RAM inserida" + ANSI_RESET);
    }

    @Override
    public void inserirDisco(Integer idMaquina) {
        con.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 3);
        System.out.println(YELLOW_BOLD_BRIGHT + "Disco inserido" + ANSI_RESET);
    }

    public void inserirDadosProcessador(Looca lucas){
        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                1,  (lucas.getProcessador().getUso()));
        System.out.println( PURPLE_BOLD_BRIGHT + "Uso de processador: " + (lucas.getProcessador().getUso()) + ANSI_RESET);
        inserirDadosTemperatura(lucas);
    }

    public void inserirDadosTemperatura(Looca lucas){
        Temperatura temperatura = lucas.getTemperatura();
        Double temperaturaEscrita = temperatura.getTemperatura();

        con.update("INSERT INTO registros(fkMaquina, fkComponente, valor, dataHora)VALUES (?,?,?,now())",
                this.machine.idMaquina(),4,temperaturaEscrita);

        System.out.println(PURPLE_BOLD_BRIGHT + "Temperatura de CPU em ºC: " + temperaturaEscrita + ANSI_RESET);
    }

    public void inserirDadosRAM(Looca lucas){
        Double ramAtual = lucas.getMemoria().getEmUso().doubleValue();
        Double ramTotal = lucas.getMemoria().getTotal().doubleValue();
        Double porcentagem = (ramAtual / ramTotal) * 100;

        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                2,  (porcentagem));
        System.out.println( BLUE_BOLD_BRIGHT + "Uso de Ram: " + (porcentagem) + ANSI_RESET);
    }

    public void inserirDadosDisco(Looca lucas){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Double max = (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824;
        Double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824;
        Double perc = max / commited;
        con.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                3,  (perc));
        System.out.println(YELLOW_BOLD_BRIGHT + "Uso de disco: " + (perc) + ANSI_RESET);
    }

    public String getSo(){
        Looca lucas = new Looca();
        String sistema = lucas.getSistema().toString();

        String frase = sistema;
        String array[] = new String[1];

        array = frase.split(":");
        frase = array[1];
        array = frase.split("F");
//        System.out.println("=".repeat(15));
//        System.out.println(array[0]);

        return array[0];
    }

    public void inserirServicosAtivos(Looca lucas){
        Integer servicoAtivo = lucas.getGrupoDeServicos().getTotalServicosAtivos();
        con.update("INSERT INTO processo VALUES (null, ?, ?, now(), ?)", this.machine.idMaquina(),
                servicoAtivo, "Ativo");
        System.out.println(YELLOW_BOLD_BRIGHT + "Serviços Ativos: " + (servicoAtivo) + ANSI_RESET);
    }

    public void inserirServicosInativos(Looca lucas){
        Integer servicoInativo = lucas.getGrupoDeServicos().getTotalServicosInativos();
        con.update("INSERT INTO processo VALUES (null, ?, ?, now(), ?)", this.machine.idMaquina(),
                servicoInativo, "Inativo");
        System.out.println(YELLOW_BOLD_BRIGHT + "Serviços Inativos: " + (servicoInativo) + ANSI_RESET);
    }

}
