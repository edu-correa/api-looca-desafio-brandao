package org.example;

import java.util.Scanner;

public class Terminal {
    public Integer askFkAgencia(){
        System.out.println("Qual é a fkAgencia? ");
        Scanner scInteger = new Scanner(System.in);
        Integer fkAgencia = scInteger.nextInt();
        return fkAgencia;
    }

    public Integer askTipoMaquina(){
        System.out.println("Qual é o seu tipo de maquina? ");
        for (TipoMaquina tipoMaquina : TipoMaquina.values()){
            System.out.println(String.format("Digite %d se for %s ", tipoMaquina.ordinal(), tipoMaquina.toString()));
        }
        Scanner nScanner = new Scanner(System.in);

        Integer posicao = nScanner.nextInt();

        System.out.println(String.format("tipo selecionado: %s", TipoMaquina.values()[posicao]));
        return posicao + 1;
    }

    public String askLocal(){
        Scanner strScanner = new Scanner(System.in);
        System.out.println("Qual o cep desta maquina? ");
        String cep = strScanner.nextLine();

        return cep;
    }
}
