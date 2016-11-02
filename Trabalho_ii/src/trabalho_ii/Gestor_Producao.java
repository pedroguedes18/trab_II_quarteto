
package trabalho_ii;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Gestor_Producao implements Runnable {
    
    private final String [] vetor_pedidos_pendentes = new String [15];          // cria um vetor de pedidos pendentes
    private final String [] vetor_pedidos_execucao = new String [7];            // cria um vetor de pedidos execucao. Apenas tem sete pois sao o numero de celulas disponiveis.
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final String [] horaData_init_pedidos_execucao = new String[7];     // assumi que tem ligacao directa ao vetor_pedidos_execucao
    private final String [] horaData_final_pedidos_execucao = new String[7];    // " " " "
    private int caminho;
    
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
    

    
    private int ver_se_vetor_cheio (String [] vetor)                            // retorna a posicao para inserir se tiver espaço, se nao retorna -1
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
    


    
    
    
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public void transformacao(String n_ordem, String peca_origem, String peca_final, String quantidade, String pedido)
    {
        int pos;
        int peca_orig = Integer.parseInt(peca_origem);
        Date date = new Date();
        
        String hourDate = dateFormat.format(date);                              // devolve a hora e a data que o pedido comecou a sua execucao
        
        pos = insere_vetor_pedidos_execucao(pedido);                            // insere no vetor de pedidos de execucao;
        
        this.horaData_init_pedidos_execucao[pos] = hourDate;                    // associa na mesma posicao a hora de inicio
        
        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // temos de ir ver a disponibilidade da célula primeiro --------------------------------------------------------------------------------------------
        // assumindo que o caminho disponivel é caminho = 1;    --------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------------------------------------------
        
        caminho = 10;                                                           // caminho a passar para o PLC, que é devolvido pela classe escolhe caminho.
        
        ModBus.writePLC(0, caminho);                                            // passa o caminho para o PLC
        ModBus.writePLC(1, peca_orig);                                          // passa a peca inicial para o PLC
        try {                                                                   // tenho de esperar um tempo pois se nao so le a ultima instrucao
                Thread.sleep(100);
        } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
        }
        ModBus.writePLC(1, 0);                                                  // para só meter uma peca de cada vez
        
        
        
        
    }
    
    public void montagem(String n_ordem, String peca_baixo, String peca_cima, String quantidade)
    {
        
    }
    
    public void descarga(String n_ordem, String peca, String pusher, String quantidade)
    {
        
    }
    
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------
   
    public void executa_pedido_pendente()                                       // executa o pedido que está na primeira posicao do vetor de pedidos pendentes
    {
        String n_ordem;
        String peca_origem;
        String peca_final;
        String quantidade;
        
        String ordem = this.vetor_pedidos_pendentes[0];
        
        switch (ordem.substring(0, 1))                                          // priemiro vê que tipo de instrução e separa os parametros
        {
            case "T":                                                           // se for uma transformação
                
                n_ordem  = ordem.substring(1, 4);
                peca_origem = ordem.substring(4, 5);
                peca_final = ordem.substring(5, 6);
                quantidade = ordem.substring(6, 8);
                
                transformacao(n_ordem, peca_origem, peca_final, quantidade, ordem);    // chama a funcao que vai tratar de enviar informacao de transformação
                
                System.out.println("----------------------------------");       // estes prints foram para testar
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

    public void insere_vetor_pedidos_pedentes(String pedido)
    {
        // vou ter de analizar o que tem na string pedido mas para já só guarda no vetor, e na posicao que está vazia.
        
        String ordem = verifica_conteudo(pedido);
        
        int pos = this.ver_se_vetor_cheio(this.vetor_pedidos_pendentes);             // se tem espaço é aqui guardado a posicao disponivel;
        
        if( pos > -1)
        {
            this.vetor_pedidos_pendentes[pos] = ordem;
            
            System.out.println("O texto adicionado na posicao " + pos + " foi: " + this.vetor_pedidos_pendentes[pos]);
            
           executa_pedido_pendente();
        }
        
        else
        {
            System.out.println("O vetor está cheio. Não é possivel adicionar mais pedidos");
        }
    }
    
    public int insere_vetor_pedidos_execucao(String pedido)
    {
        int pos = this.ver_se_vetor_cheio(this.vetor_pedidos_execucao);             // se tem espaço é aqui guardado a posicao disponivel;
        
        if( pos > -1)
        {
            this.vetor_pedidos_execucao[pos] = pedido;
            
            System.out.println("Vetor de pedidos em execução: " + this.vetor_pedidos_execucao[pos]);
            
            return pos;
        }
        
        else
        {
            System.out.println("O vetor está cheio. Não é possivel executar mais pedidos");
            return pos;
        }
    }

    @Override
    public void run()                                                           // função que vai andar sempre a percorrer o vetor de pedidos pendentes e a mandar executar
    {
        while(true)
        {
            if(this.vetor_pedidos_pendentes[0] == (null))                       // o vetor está vazio logo nao precisa de executar nada
            {
                break;
            }
        
            else                                                                // quer dizer que já tem pelo menos um pedido pendente
            {
                for(int i=0; this.vetor_pedidos_pendentes[i] != null ; i++)      // percorre o vetor de pedidos pendentes do inicio até à ultima posicao ocupada
                {
                    if(this.ver_se_vetor_cheio(this.vetor_pedidos_execucao) == -1)   // verifica se pode adicionar pedidos de execução, ou seja, se já nao está tudo completo, vai ajudar para gerir as threads
                    {
                        break;                                                  // vetor está cheio ou seja nao posso adicionar mais pedidos em execução
                    }
                    
                    else
                    {
                        int x = this.ver_se_vetor_cheio(this.vetor_pedidos_execucao);   // retorna a ultima posicao livre, ou seja, vai ser a thread que vou iniciar
                        
                        
                    }
                
                }
        }
        }
    }


}
