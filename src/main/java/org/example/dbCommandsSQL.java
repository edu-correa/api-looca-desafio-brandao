package org.example;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.DataClassRowMapper;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.text.DecimalFormat;

public class dbCommandsSQL implements IGeneralDbCommands{
    private JdbcTemplate conSQL;
    private Integer fkAgencia;
    private Integer fkTipoMaquina;
    private String locale;
    private Machine machine;
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");

    public dbCommandsSQL() {
        ConnectionSQL connection = new ConnectionSQL();
        Terminal terminal = new Terminal();
        conSQL = connection.getCon();
        fkAgencia = terminal.askFkAgencia();
        fkTipoMaquina = terminal.askTipoMaquina();
        locale = terminal.askLocal();
    }



    @Override
    public void searchByMacAddress() throws InterruptedException {
        String macAddress = IGeneralDbCommands.getMacAddress();

        List<Machine> resultados = conSQL.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);

        Integer contadorDeResultados = resultados.size();
        if (contadorDeResultados == 1){
            System.out.println("\nConsultando maquina");
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
            System.out.println("Quais componentes deseja?");
            if (!alreadyProcessador && !alreadyRam && !alreadyDisco){
                System.out.println("1 - Processador");
                System.out.println("2 - RAM");
                System.out.println("3 - Disco");
            }
            else {
                if (!alreadyProcessador){
                    System.out.println("1 - Processador");
                }
                if (!alreadyRam){
                    System.out.println("2 - RAM");
                }
                if (!alreadyDisco){
                    System.out.println("3 - Disco");
                }
                System.out.println("4 - Sair");
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
        conSQL.update("INSERT INTO maquina VALUES (null, ?, ?, ?, ?, ?)", this.fkAgencia, this.fkTipoMaquina, macAddress, locale, IGeneralDbCommands.getMachineName());
        System.out.println("Maquina inserida com sucesso!");
        List<Machine> resultados = conSQL.query("SELECT * FROM maquina WHERE macAddress = ?",
                new DataClassRowMapper<>(Machine.class),
                macAddress);
        machine = resultados.get(0);
        askComponentes();
        System.out.println("Prosseguindo com teste de inserção!");
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
    public void inserirProcessador(Integer idMaquina) {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 1);
        System.out.println("Processador inserido");
    }

    @Override
    public void inserirRam(Integer idMaquina) {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 2);
        System.out.println("RAM inserida");
    }

    @Override
    public void inserirDisco(Integer idMaquina) {
        conSQL.update("INSERT INTO maquinaComponente VALUES (?, ?)", idMaquina, 3);
        System.out.println("Disco inserido");
    }

    public void inserirDadosProcessador(Looca lucas){
        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                1,  dfSharp.format(lucas.getProcessador().getUso()));
        System.out.println("Uso de processador: " + dfSharp.format(lucas.getProcessador().getUso()));
        inserirDadosTemperatura(lucas);
    }

    public void inserirDadosTemperatura(Looca lucas){
        Temperatura temperatura = lucas.getTemperatura();
        String temperaturaEscrita = dfSharp.format(temperatura.getTemperatura());

        conSQL.update("INSERT INTO registros(fkMaquina, fkComponente, valor, dataHora)VALUES (?,?,?,now())",
                this.machine.idMaquina(),4,temperaturaEscrita);

        System.out.println("Temperatura de CPU em ºC: " + temperaturaEscrita);
    }
    public void inserirDadosRAM(Looca lucas){
        Double ramAtual = lucas.getMemoria().getEmUso().doubleValue();
        Double ramTotal = lucas.getMemoria().getTotal().doubleValue();
        Double porcentagem = (ramAtual / ramTotal) * 100;

        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                2,  dfSharp.format(porcentagem));
        System.out.println("Uso de Ram: " + dfSharp.format(porcentagem));
    }

    public void inserirDadosDisco(Looca lucas){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Double max = (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824;
        Double commited = (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824;
        Double perc = max / commited;
        conSQL.update("INSERT INTO registros VALUES (null, ?, ?, ?, now())", this.machine.idMaquina(),
                3,  dfSharp.format(perc));
        System.out.println("Uso de disco: " + dfSharp.format(perc));
    }

}
