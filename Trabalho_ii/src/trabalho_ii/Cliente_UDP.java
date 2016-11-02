
package trabalho_ii;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente_UDP implements Runnable {
    


    @Override
    public void run() 
    {
        BufferedReader msgConsola = new BufferedReader(new InputStreamReader(System.in));   // mensagem que é escrita na consola     
        try (DatagramSocket clientSocket = new DatagramSocket()                                     // cria um datagrama para o cliente, ou seja cria um socket para o cliente
        ) {
            InetAddress IPAddress = InetAddress.getByName("localhost");                             // vai buscar o ip do localhost
            
            byte[] msgEnviar = new byte[1024];                                                   // cria o buffer para mandar mensagem
            byte[] receiveData = new byte[1024];                                                // cria o buffer para receber mensagem
            
            String msg = msgConsola.readLine();
          
            msgEnviar = msg.getBytes();
            
            DatagramPacket sendPacket = new DatagramPacket(msgEnviar, msgEnviar.length, IPAddress, 54321);  // prepara o pacote com a mensagem que queremos mandar para o ip e porta que especificamos
            
            clientSocket.send(sendPacket);                                                                  // envia a mensagem
            
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            clientSocket.receive(receivePacket);
            
            String modifiedSentence = new String(receivePacket.getData());
            
            System.out.println("FROM SERVER:" + modifiedSentence);
        } catch (SocketException ex) {
            Logger.getLogger(Cliente_UDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente_UDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}
