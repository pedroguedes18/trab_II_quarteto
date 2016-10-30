
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
    // O gestor de produção recebe o que lhe é enviado pelo Servidor UDP e o guarde no vetor de pedidos_pendentes
    //----------------------------------------------------------------------------------------------------------------------
    
    public void transformacao(String n_ordem, String peca_origem, String peca_final, String quantidade)
    {
        
    }
    
    public void montagem(String n_ordem, String peca_baixo, String peca_cima, String quantidade)
    {
        
    }
    
    public void descarga(String n_ordem, String peca, String pusher, String quantidade)
    {
        
    }
    
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
    
    // Tenho de analisar se o conteudo da mensagem é o correcto
    //---------------------------------------------------------
    
    public String verifica_conteudo(String conteudo)                            // retorna exatamente o que interessa do pedido, ou seja se tiver espaços a mais no inicio elimina-os
    {
        
        String[] mensagem = conteudo.split("(?<=:)");                           // garanto que o inicio começa com este sinal ":"
       
        String part2 = mensagem[1];                                             // Ficamos com o que está a direita de ":"
        
        part2 = part2.substring(0, 8);                                          // fico com o tamanho certo que quero
        
        return part2;
    }
    

    public void insere_vetor_pedidos_pedentes(String pedido)
    {
        // vou ter de analizar o que tem na string pedido mas para já só guarda no vetor, e na posicao que está vazia.
        
        String ordem = verifica_conteudo(pedido);
        
        int pos = this.ver_se_vetor_cheio(vetor_pedidos_pendentes);             // se tem espaço é aqui guardado a posicao disponivel;
        
        if( pos > -1)
        {
            this.vetor_pedidos_pendentes[pos] = ordem;
            
            System.out.println("O texto adicionado na posicao " + pos + " foi: " + this.vetor_pedidos_pendentes[pos]);
        }
        
        else
        {
            System.out.println("O vetor está cheio. Não é possivel adicionar mais pedidos");
        }
    }
    
    public void executa_pedido_pendente()
    {
        String n_ordem;
        String peca_origem;
        String peca_final;
        String quantidade;
        
        String ordem = this.vetor_pedidos_pendentes[0];
        
        switch (ordem.substring(0, 1)) 
        {
            case "T":                                                           // se for uma transformação
                
                n_ordem  = ordem.substring(1, 4);
                peca_origem = ordem.substring(4, 5);
                peca_final = ordem.substring(5, 6);
                quantidade = ordem.substring(6, 8);
                
                transformacao(n_ordem, peca_origem, peca_final, quantidade);    // chama a funcao que vai tratar de enviar informacao de transformação
                
                System.out.println("----------------------------------");
                System.out.println("Ordem de Transformação:");
                System.out.println("numero ordem: " + n_ordem);
                System.out.println("peça origem: P" + peca_origem);
                System.out.println("peça final:  P" + peca_final);
                System.out.println("quantidade:  " + quantidade);
                System.out.println("----------------------------------");
                break;
        
            case "M":                                                           // se for uma montagem
                
                n_ordem  = ordem.substring(1, 4);
                peca_origem = ordem.substring(4, 5);
                peca_final = ordem.substring(5, 6);
                quantidade = ordem.substring(6, 8);
                
                montagem(n_ordem, peca_origem, peca_final, quantidade);
                
                System.out.println("----------------------------------");
                System.out.println("Ordem de Montagem:");
                System.out.println("numero ordem: " + n_ordem);
                System.out.println("peça baixo: P" + peca_origem);
                System.out.println("peça cima:  P" + peca_final);
                System.out.println("quantidade:  " + quantidade);
                System.out.println("----------------------------------");
                break;
        
            case "U":                                                           // se for uma descarga
                
                n_ordem  = ordem.substring(1, 4);
                peca_origem = ordem.substring(4, 5);
                peca_final = ordem.substring(5, 6);
                quantidade = ordem.substring(6, 8);
                              
                descarga(n_ordem, peca_origem, peca_final, quantidade);
                
                System.out.println("----------------------------------");
                System.out.println("Ordem de Descarga:");
                System.out.println("numero ordem: " + n_ordem);
                System.out.println("peça : P" + peca_origem);
                System.out.println("Destino:  Pusher " + peca_final);
                System.out.println("quantidade:  " + quantidade);
                System.out.println("----------------------------------");
                break;
            
            default:
                System.out.println("A string está no formato errado");
                break;
        }
    }
}
