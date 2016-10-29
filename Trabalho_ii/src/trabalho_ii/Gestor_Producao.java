
package trabalho_ii;

public class Gestor_Producao {
    
    private final String [] vetor_pedidos_pendentes = new String [15];           // cria um vetor de pedidos pendentes
    private final String [] vetor_pedidos_execucao = new String [7];             // cria um vetor de pedidos execucao. Apenas tem sete pois sao o numero de celulas disponiveis.
   
    
    private static Gestor_Producao instance;    // intancia que é da class Gestor_produção
    
    //private Gestor_Producao(){}               // ainda nao percebi para que é este método
    
	
    public static Gestor_Producao getInstance()
    {
	if(instance==null)
        {
            instance=new Gestor_Producao();	
	}
        
	return instance;
    }
	//Singleton
    
 
}
