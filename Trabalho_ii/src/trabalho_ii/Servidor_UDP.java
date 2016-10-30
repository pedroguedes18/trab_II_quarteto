
package trabalho_ii;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor_UDP implements Runnable{
    
    @Override
    public void run() {
                    int porta = 54321;                                           // porta que o servidor vai ficar a escutar 
          
            DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(porta);                    // fica a escutar da porta 54321
 
            byte[] msgRecebida = new byte[256];                         // prepara buffer para recebimento de mensagens dos clientes
            byte[] msgEnviada = new byte[1024];                          // prepara buffer para enviar mensagens para os clientes
            
            System.out.println("A ouvir a porta: " + porta);
            
            while(true)
               {
                  DatagramPacket msgRecebidaPacket = new DatagramPacket(msgRecebida, msgRecebida.length);   // prepara o pacote de dados a ser recebido
                       
                  try {
                            serverSocket.receive(msgRecebidaPacket);                          // Recebe a mensagem
                  
                  } catch (IOException ex) {
                                                Logger.getLogger(Servidor_UDP.class.getName()).log(Level.SEVERE, null, ex);
                                           }
                  
                  String mensagem = new String( msgRecebidaPacket.getData());
                  System.out.println("RECEIVED: " + mensagem);
                  
                  //-----------------------------------------------------------------------
                  
                  Gestor_Producao gestor_producao = Gestor_Producao.getInstance();          // vai buscar a instancia do Gestor de Producao
                  gestor_producao.insere_vetor_pedidos_pedentes(mensagem);                  // envia a mensagem que recebeu para o G_P
                  
                  //-----------------------------------------------------------------------
                  
                  InetAddress IPAddress = msgRecebidaPacket.getAddress();           // vê o endereço IP de quem lhe mandou a mensagem para lhe poder responder   
                  
                  int port = msgRecebidaPacket.getPort();                           // guarda a porta do cliente
                  
                  //String capitalizedSentence = mensagem.toUpperCase();
                  
                  String capitalizedSentence = "a mensagem chegou";
                  
                  msgEnviada = capitalizedSentence.getBytes();                      // guarda os bytes da mensagem
                  
                  DatagramPacket msgEnviarPacket = new DatagramPacket(msgEnviada, msgEnviada.length, IPAddress, port);  // prepara o pacote para enviar a mensagem para o cliente indicanto o IP e a porta
                  
                  serverSocket.send(msgEnviarPacket);                               // envia de volta a mensagem que recebeu
               }
            } catch (SocketException ex) {
            Logger.getLogger(Servidor_UDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

}
