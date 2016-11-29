
package trabalho_ii;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Trabalho_ii {

    public static void main(String[] args) throws Exception {
        Servidor_UDP UDPserver = new Servidor_UDP();
        Cliente_UDP cliente = new Cliente_UDP();
        
        Gestor_Producao  gestor_producao = Gestor_Producao.getInstance();                                          // vou ter de utilizar para inicializar o objecto do Gestor de Producao
        
        gestor_producao.maquina_estados();
        
        Escolha_Caminho escolha_caminho = Escolha_Caminho.getInstance();
        
        //escolha_caminho.AtualizarCelula();
        
        ExecutorService executorService = Executors.newFixedThreadPool(3);       // criar duas threads para o servidor e cliente.
        executorService.submit(UDPserver);
        executorService.submit(cliente);
        executorService.submit(gestor_producao);
        
      
        
        //--------------------------------------------
        /*
        Escolha_Caminho caminho = Escolha_Caminho.getInstance();
        
        Celula paralelo1 = Celula.getInstance();
        Celula paralelo2 = Celula.getInstance();
        Celula serie1 = Celula.getInstance();
        Celula serie2 = Celula.getInstance();
        Celula montagem = Celula.getInstance();
        Celula descargaPM1 = Celula.getInstance();
        Celula descargaPM2 = Celula.getInstance();*/
        
        //A função DisponibilidadeCelula tem de ser usada mediante a Celula 
        //correspondente ao caminho, por exemplo, serie1.DisponibilidadeCelula()
        //
    
    
        
    }
    
}
