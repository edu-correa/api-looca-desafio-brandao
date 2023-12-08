package org.example.main;

import org.example.comandos.dbCommands;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        dbCommands db = new dbCommands();

        db.iniciate();

    }
}