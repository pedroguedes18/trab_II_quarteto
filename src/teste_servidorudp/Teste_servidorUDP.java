
package teste_servidorudp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Teste_servidorUDP {

    public static void main(String[] args) {
       int port = 54321;
       
       UDP_Server server = new UDP_Server(port);
       Cliente_UDP client = new Cliente_UDP();
       
      
       ExecutorService executorService = Executors.newFixedThreadPool(2);       // criar duas threads para o servidor e cliente.
       executorService.submit(client);
       executorService.submit(server);
    }
    
}
