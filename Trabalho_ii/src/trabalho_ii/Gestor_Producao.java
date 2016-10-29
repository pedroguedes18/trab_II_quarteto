
package trabalho_ii;

public class Gestor_Producao {
    
    private final String [] vetor_pedidos_pendentes = new String [15];           // cria um vetor de pedidos pendentes
    private final String [] vetor_pedidos_execucao = new String [7];             // cria um vetor de pedidos execucao. Apenas tem sete pois sao o numero de celulas disponiveis.
   
    
    private static Gestor_Producao instance;                                    // instância que é da class Gestor_produção
    
    //private Gestor_Producao(){}                                               // ainda nao percebi para que é este método
    
	
    public static Gestor_Producao getInstance()                                 // método para criar se ainda não foi criado uma instancia do Gestor de Produção
    {
	if(instance==null)
        {
            instance=new Gestor_Producao();	
	}
        
	return instance;
    }
        //Singleton
    
    
    //----------------------------------------------------------------------------------------------------------------------
    // tentar que o gestor de produção receba o que lhe é enviado pelo Servidor UDP e o guarde no vetor de pedidos_pendentes
    //----------------------------------------------------------------------------------------------------------------------
    
    private int ver_se_vetor_cheio (String [] vetor)
    {
        int pos = -1;
        
        for (int i=0; i<vetor.length-1; i++)                                    // percorre o vetor até encontrar uma posição vazia 
        {
            if(this.vetor_pedidos_pendentes[i] == null)
            {
                pos = i;
                break;
            }
        }
        
        return pos;                                                             // se não encontrar nenhuma posicao vazia restorna -1;
    }
    
    
    public void insere_vetor_pedidos_pedentes(String pedido)
    {
        // vou ter de analizar o que tem na string pedido mas para já só guarda no vetor, e na posicao que está vazia.
        
        int pos = this.ver_se_vetor_cheio(vetor_pedidos_pendentes);             // se tem espaço é aqui guardado a posicao disponivel;
        
        if( pos > -1)
        {
            this.vetor_pedidos_pendentes[pos] = pedido;
        }
        
        else
        {
            System.out.println("O vetor está cheio. Não é possivel adicionar mais pedidos");
        }

    }
 
}
