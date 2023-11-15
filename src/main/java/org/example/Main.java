package org.example;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        dbCommands db = new dbCommands();

        db.searchByMacAddress();

    }
}