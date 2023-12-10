package org.example.main;

import org.example.comandos.IGeneralDbCommands;
import org.example.Enum.TipoMaquina;

import java.util.Scanner;

import org.example.comandos.dbCommands;
public class Terminal {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";

    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";

    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";

    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m";
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE

    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public Integer askFkAgencia(){
        System.out.println( PURPLE_BOLD_BRIGHT + ".・。.・゜✭ Desafio de Programação Java: Monitoramento de Sistema com Princípios SOLID .・。.・゜✭ " + ANSI_RESET);
        System.out.println( PURPLE_BOLD_BRIGHT + "by Danielle Munakata e Eduardo Corrêa \n" + ANSI_RESET);

        System.out.println( WHITE_BOLD_BRIGHT + "╭── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╮" + ANSI_RESET);
        System.out.println( YELLOW_BOLD_BRIGHT + "Qual é a fkAgencia? " + ANSI_RESET);
        System.out.println( WHITE_BOLD_BRIGHT + "╰── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╯ \n" + ANSI_RESET);
        Scanner scInteger = new Scanner(System.in);
        Integer fkAgencia = scInteger.nextInt();
        return fkAgencia;
    }

    public Integer askTipoMaquina(){
        System.out.println( WHITE_BOLD_BRIGHT + "╭── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╮" + ANSI_RESET);
        System.out.println( CYAN_BOLD_BRIGHT + "Qual é o seu tipo de maquina? " + ANSI_RESET);
        System.out.println( WHITE_BOLD_BRIGHT + "╰── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╯ \n" + ANSI_RESET);
        for (TipoMaquina tipoMaquina : TipoMaquina.values()){
            System.out.println(String.format( CYAN_BOLD_BRIGHT + " .・。.・゜✭ Digite %d se for %s " + ANSI_RESET, tipoMaquina.ordinal(), tipoMaquina.toString()));
        }
        Scanner nScanner = new Scanner(System.in);

        Integer posicao = nScanner.nextInt();

        System.out.println(String.format( CYAN_BOLD_BRIGHT + " .・。.・゜✭ tipo selecionado: %s" + ANSI_RESET, TipoMaquina.values()[posicao]));
        return posicao + 1;
    }

    public String askLocal(){
        Scanner strScanner = new Scanner(System.in);
        System.out.println( WHITE_BOLD_BRIGHT + "╭── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╮" + ANSI_RESET);
        System.out.println( BLUE_BOLD_BRIGHT + "Qual o cep desta maquina? " + ANSI_RESET);
        System.out.println( WHITE_BOLD_BRIGHT + "╰── ⋅ ⋅ ── ✩ ── ⋅ ⋅ ──╯" + ANSI_RESET);
        String cep = strScanner.nextLine();

        return cep;
    }

    public void askComponentes(IGeneralDbCommands commands){

        Scanner numScan = new Scanner(System.in);
        Integer resposta  = 0;
        Boolean alreadyProcessador = false, alreadyRam = false, alreadyDisco = false, alreadyAny = false, wannaStop = false;
        do {
            wannaStop = false;
            System.out.println(WHITE_BOLD_BRIGHT + "Quais componentes deseja?" + ANSI_RESET);
            if (!alreadyProcessador && !alreadyRam && !alreadyDisco){
                System.out.println( CYAN_BOLD_BRIGHT + "1 - Processador" + ANSI_RESET);
                System.out.println( GREEN_BOLD_BRIGHT + "2 - RAM" + ANSI_RESET);
                System.out.println(YELLOW_BOLD_BRIGHT + "3 - Disco" + ANSI_RESET);
            }
            else {
                if (!alreadyProcessador){
                    System.out.println( CYAN_BOLD_BRIGHT + "1 - Processador" + ANSI_RESET);
                }
                if (!alreadyRam){
                    System.out.println( GREEN_BOLD_BRIGHT + "2 - RAM" + ANSI_RESET);
                }
                if (!alreadyDisco){
                    System.out.println(YELLOW_BOLD_BRIGHT + "3 - Disco" + ANSI_RESET);
                }
                System.out.println( WHITE_BOLD_BRIGHT + "4 - Sair" + ANSI_RESET);
            }
            resposta = numScan.nextInt();
            if (resposta == 1 && !alreadyProcessador){
                commands.inserirProcessador();
                alreadyProcessador = true;
                alreadyAny = true;
            } else if (resposta == 2 && !alreadyRam){
                commands.inserirRam();
                alreadyRam = true;
                alreadyAny = true;
            } else if (resposta == 3 && !alreadyDisco) {
                commands.inserirDisco();
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
}
