/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teste_servidorudp;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.SocketException;
import java.net.InetAddress;


public class Cliente_UDP implements Runnable {

    private final int port = 54321;
   
    
    
    @Override
    public void run() {
        try(DatagramSocket clientSocket = new DatagramSocket(port)){
            byte[] buffer = new byte[256];
            while(true)
            {
                DatagramPacket datagramPacket = new DatagramPacket(buffer,0,buffer.length); // está pronto para receber dados
                clientSocket.receive(datagramPacket);
                
                String receivedMessage = new String(datagramPacket.getData());
                System.out.println("Mensagem recebida:");
                System.out.println(receivedMessage);
                
                
                String msg = "Menssagem recebida com sucesso";
                
                byte[] outmsg = msg.getBytes();
                
                InetAddress address = datagramPacket.getAddress();
                int returnport = datagramPacket.getPort();
                DatagramPacket out = new DatagramPacket(outmsg, 0, outmsg.length, address, returnport);
                clientSocket.send(out);
                
                buffer = new byte[256];
                
            }
            
            
        }   catch (SocketException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println("Timeout. Client is closing.");
        }
    
    }
}
