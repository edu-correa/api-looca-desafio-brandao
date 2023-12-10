package org.example.comandos;

import com.github.britooo.looca.api.core.Looca;
import org.example.DAO.Machine;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import com.github.britooo.looca.api.group.rede.RedeInterface;

public interface IGeneralDbCommands {
    Machine machine = null;
    void insertNewMachine(String macAddress) throws InterruptedException;
    void searchByMacAddress() throws InterruptedException;

    public static String getMacAddress(){
        InetAddress ip;
        return "00:1B:44:11:3A:B7";
        /*try {

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
        return null;*/
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

    void inserirProcessador();
    void inserirRam();
    void inserirDisco();

}
