
package trabalho_ii;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Trabalho_ii {

    public static void main(String[] args) throws Exception {
        Servidor_UDP UDPserver = new Servidor_UDP();
        Cliente_UDP cliente = new Cliente_UDP();
        
        Gestor_Producao.getInstance();                                          // vou ter de utilizar para inicializar o objecto do Gestor de Producao
        
        ExecutorService executorService = Executors.newFixedThreadPool(2);       // criar duas threads para o servidor e cliente.
        executorService.submit(UDPserver);
        executorService.submit(cliente);
      
    }
    
}
