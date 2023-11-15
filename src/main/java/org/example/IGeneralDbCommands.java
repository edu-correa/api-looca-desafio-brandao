package org.example;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public interface IGeneralDbCommands {
    Machine machine = null;
    void insertNewMachine(String macAddress) throws InterruptedException;
    void searchByMacAddress() throws InterruptedException;

    public static String getMacAddress(){
        InetAddress ip;
        try {

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
        return null;
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
