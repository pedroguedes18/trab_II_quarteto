/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teste_servidorudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UDP_Server implements Runnable {

    private final int clientPort;

    public UDP_Server(int clientPort) {
        this.clientPort = clientPort;
    }
    
    
    
    @Override
    public void run() {
        try(DatagramSocket serverSocket = new DatagramSocket(54323)){
            
            while(true)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));       // para ler de onde escrevemos
                String message = in.readLine();
                DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(),InetAddress.getLocalHost(),clientPort);
                
                serverSocket.send(datagramPacket);
            }   
  
        } catch (SocketException ex) {
            System.out.println(ex);
        } catch (UnknownHostException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
