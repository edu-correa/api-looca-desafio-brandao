package org.example.comandos;

import com.github.britooo.looca.api.core.Looca;
import org.example.DAO.Machine;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public interface IGeneralDbCommands {
    Machine machine = null;
    void insertNewMachine(String macAddress) throws InterruptedException;
    void searchByMacAddress() throws InterruptedException;

    public static String getMacAddress(){
        String mac = "";
        Looca looca = new Looca();
        List<RedeInterface> interfacesRede = new ArrayList<>();
        for(RedeInterface o: looca.getRede().getGrupoDeInterfaces().getInterfaces()){
            if(o.getNome().equalsIgnoreCase("eth0") || o.getNome().equalsIgnoreCase("wlp3s0")){
                mac = o.getEnderecoMac();
            }
        }
        looca.getRede().getGrupoDeInterfaces().getInterfaces();
        return mac;
    }

    public static String getMachineName(){
        try{
            String machineName = InetAddress.getLocalHost().getHostName();
            return machineName;
        }catch (Exception e){
            System.out.println("Exception caught ="+e.getMessage());
        }
        return null;
    }

    //TODO fazer isso funfar
    private void startGathering(){
        System.out.println("ainda em produção!");
    }

    void inserirProcessador(Integer idMaquina);
    void inserirRam(Integer idMaquina);
    void inserirDisco(Integer idMaquina);

}
