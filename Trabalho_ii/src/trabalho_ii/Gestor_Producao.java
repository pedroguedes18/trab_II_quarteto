
package trabalho_ii;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Gestor_Producao implements Runnable {
    
    private final String [] vetor_pedidos_pendentes = new String [15];          // cria um vetor de pedidos pendentes
    private final int [] vetor_aux_ped_pendentes = new int [15];                // vetor auxiliar de pedidos pendes que indicam se o processo já entrou em execução alguma vez
    private final String [] vetor_pedidos_execucao = new String [7];            // cria um vetor de pedidos execucao. Apenas tem sete pois sao o numero de celulas disponiveis.
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    private final ArrayList<String> numero_ordem = new ArrayList<> ();
    private final ArrayList<String> horaData_init_pedidos_pendentes = new ArrayList<> ();     // assumi que tem ligacao directa ao vetor_pedidos_execucao
    private final ArrayList<String> horaData_final_pedidos_pendentes = new ArrayList<>();    // " " " "
    
    private int celula;
    private int peca_trans_1, peca_trans_2, peca_trans_3, peca_trans_4, peca_trans_5;
    private int numero_serie = 0;
    private int sensorAT2;
    private int num_serie_AT2 = 0;
    private int estado = 0;
    private int aux_estado = 0;
    
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
    

    public void insere_vetor_pedidos_pedentes(String pedido)
    {
        // vou ter de analizar o que tem na string pedido mas para já só guarda no vetor, e na posicao que está vazia.
        
        String ordem = verifica_conteudo(pedido);
        
        int pos = this.ver_se_vetor_cheio(this.vetor_pedidos_pendentes);             // se tem espaço é aqui guardado a posicao disponivel;
        
        if( pos > -1)
        {
            this.vetor_pedidos_pendentes[pos] = ordem;
            this.vetor_aux_ped_pendentes[pos] = 0;                              // 0 -> significa que ainda não foi executado nenhuma vez... 
            
            System.out.println("----------------------------------------------------------------------------------\n"
                    + "-------------------------------------------------------------------------------------");
            
            System.out.println("O texto adicionado na posicao " + pos + " foi: " + this.vetor_pedidos_pendentes[pos]);
        
            
            System.out.println("----------------------------------------------------------------------------------\n"
                    + "-------------------------------------------------------------------------------------");
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
    
    public void escreve_PLC(int peca_origem)
    {
        aux_estado = 1;
        System.out.println("aux_estado: " +aux_estado);
        System.out.println("estado: " +estado);
        
        numero_serie = numero_serie +1;
        
        ModBus.writePLC(8, numero_serie);
        ModBus.writePLC(1, peca_origem);                                        // passa a peca inicial para o PLC

        try {                                                                   // tenho de esperar um tempo pois se nao so le a ultima instrucao
            Thread.sleep(100);
        } catch(InterruptedException ex) 
            {
            Thread.currentThread().interrupt();
            }
        
        ModBus.writePLC(1, 0);   
    }
    
    public void remove_pedido_pendente(int pos)
    {
        int tamanho = this.vetor_pedidos_pendentes.length -1;
        
        this.vetor_pedidos_pendentes[pos] = null;
        
        for(int i=pos; i < tamanho; i++)
        {
            this.vetor_pedidos_pendentes[i] = this.vetor_pedidos_pendentes[i+1]; 
        }
        
        this.vetor_pedidos_pendentes[tamanho] = null;                           // asseguro que a ultima posicao fica com valor nulo
        
    }
    
    public void thread_espera_peca(String ordem_numero, int num_serie)                                             //para testar se funciona deste modo
    {
        new Thread()
                {
                    @Override
                    public void run()
                    {
                        //ler como estão os valores
                        System.out.println("numero serie suposto" +num_serie);
                        
                       sensorAT2 = ModBus.readPLC(0, 0);                // readPLC(numRegisto,0)
                       num_serie_AT2 = ModBus.readPLC(1, 0);            // readPLC(numRegisto,0) 
                        
                       while (sensorAT2 != 1 || num_serie_AT2 != num_serie)
                       {
                            //fica aqui à espera e vai atualizando as variaveis
                            System.out.flush();                            
                            sensorAT2 = ModBus.readPLC(0, 0);           // readPLC(numRegisto,0)
                            num_serie_AT2 = ModBus.readPLC(1, 0);       // readPLC(numRegisto,0)
                        }
                       
                        System.out.println("sensor_lido: " + sensorAT2);
                        System.out.println("num_serie_lido: "+num_serie_AT2);
                       
                        // vai atulizar hora de fim
                                                    
                        Date date_fim = new Date();
        
                        String hourDate_fim = dateFormat.format(date_fim);                                  // devolve a hora e a data que o pedido finalizou a sua execucao
                        
                        int posicao = numero_ordem.indexOf(ordem_numero);                                   //vai ver em que posicao está o numero de ordem para lhe associar a sua hora de fim
        
                        horaData_final_pedidos_pendentes.add(posicao, hourDate_fim);

                        //horaData_final_pedidos_pendentes[i] = hourDate_fim;                               // associa na mesma posicao a hora de fim de o pedido pendente
                                                
                        System.out.println("DATA DE FIM: " + horaData_final_pedidos_pendentes.get(posicao));        // só para ver se funciona a data

                    }
                }.start();
    }

    
    public void maquina_estados()                                             //para testar se funciona deste modo
    {
        new Thread()
                {
                    @Override
                    public void run()
                    {
                        System.out.println("estado: " +estado);
                        while(true)
                        {
                            System.out.flush();
                            if(estado == 0 && aux_estado == 1)                        // passa para o estado 1 quando recebe ordem do escolha caminho
                            {
                                estado = 1;
                                System.out.println("estado: " +estado);
                                System.out.flush();
                            }
                          
                            else if(estado == 1 && ModBus.readPLC(7,0)==1 )     // sensor AT1
                            {
                                estado = 2;
                                System.out.println("estado: " +estado);
                                System.out.flush();
                            }
                          
                            else if (estado == 2 && ModBus.readPLC(2,0)== 1)    // tapete do armazem livre
                            {
                                estado = 0;
                                aux_estado = 0;
                                System.out.println("estado: " +estado);
                                System.out.flush();
                            }
                        }
                    }
                }.start();
    }
    
    @Override
    public void run()                                                           // função que vai andar sempre a percorrer o vetor de pedidos pendentes e a mandar executar
    {
        String n_ordem;
        String peca_1;
        String peca_2;
        String quantidade;
        int quant;
        

        while(true)
        {
            System.out.flush();
            
            if(this.vetor_pedidos_pendentes[0] != null)                         // quer dizer que já tem pelo menos um pedido pendente; o vetor está não esta vazio logo precisa de executar
            {
                
                for(int i=0; this.vetor_pedidos_pendentes[i] != null ; i++)     // percorre o vetor de pedidos pendentes do inicio até à ultima posicao ocupada
                {
                         
                    Escolha_Caminho escolha_caminho = Escolha_Caminho.getInstance();        // vai buscar a instancia da Classe Escolha_caminho      
                    
                    switch (vetor_pedidos_pendentes[i].substring(0, 1))                     // primeiro vê que tipo de instrução e separa os parametros
                    {
                            //-----------------------------------------------------TRANSFORMACAO-------------------------------------------------------------------------------------------------
                            //------------------------------------------------------------------------------------------------------------------------------------------------------
                             case "T":                                                           // se for uma transformação
                                        System.out.println("entrou no swith");
                                        n_ordem  =this.vetor_pedidos_pendentes[i].substring(1, 4);
                                        peca_1 = this.vetor_pedidos_pendentes[i].substring(4, 5);
                                        peca_2 = this.vetor_pedidos_pendentes[i].substring(5, 6);
                                        quantidade = this.vetor_pedidos_pendentes[i].substring(6, 8);
                                        
                                        int peca_orig = Integer.parseInt(peca_1);
                                        int peca_final = Integer.parseInt(peca_2);


                                        quant = Integer.parseInt(quantidade);                                       // converte para inteiro a quantidade
                                                
                                        if( quant == 0)
                                        {
                                            System.out.println("quantidade = 0");
                                            remove_pedido_pendente(i);  // como é a primeira vez que vai ser executado, se a quantidade for zero não é um pedido válido logo removemos

                                        }
                                        
                                        else if(quant > 0)
                                        {
                                            System.out.println("quantidade maior que zero");
                                            while(estado != 0)
                                            {
                                                // espero que o tapete esteja livre para poder voltar a tirar uma peça
                                                System.out.flush();
                                                //celula = 0;
                                            }
                                            
                                            System.out.println("passou o ciclo while do estado");
                                        
                                            // já está livre o tapete do armazém
                                        
                                            celula = escolha_caminho.Associar_Celulas_Transformaçao(peca_orig, peca_final);
                                        
                                            System.out.println("celula: " +celula);
                                        
                                            /*celula = 4;
                                            peca_trans_1 = 4;
                                            peca_trans_2 = 5;
                                            peca_trans_3 = 7;
                                            peca_trans_4 = 0;
                                            peca_trans_5 = 0;*/
                                        
                                        
                                            if ( celula > 0)                    // quer dizer que existe uma célula disponivel
                                            {
                                                // AQUI DEVO PODER VER SE O NUMERO DE ORDEM JÁ ESTÁ NA LISTA DE NUMEROS DE ORDENS
                                                
                                                if (numero_ordem.indexOf(n_ordem) == -1)
                                                //if(this.vetor_aux_ped_pendentes[i] == 0)                                                // o pedido é a primeira vez que vai ser executado logo actualizamos o vetor de horas iniciais
                                                {
                                                    numero_ordem.add(n_ordem);                                                          // adiciona o numero de ordem a ser executado

                                                    int pos_ordem = numero_ordem.indexOf(n_ordem);                                      // vai ver em que posicao adicionou para depois lhe poder atribuir as horas
                                                    
                                                    Date data_inicio = new Date();
        
                                                    String hourDate_inicio = dateFormat.format(data_inicio);                            // devolve a hora e a data que o pedido comecou a sua execucao
        
                                                    //this.horaData_init_pedidos_pendentes[i] = hourDate_inicio;                        // associa na mesma posicao a hora de inicio de o pedido pendente
                                                
                                                    horaData_init_pedidos_pendentes.add(pos_ordem, hourDate_inicio);                    //adiciona a hora de inicio na posicao correspondente
                                                    
                                                    System.out.println("DATA DE INICIO: " +horaData_init_pedidos_pendentes.get(pos_ordem));       // só para ver se funciona a data
                                                
                                                    
                                                    quant = quant - 1;

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    /*ModBus.writePLC(2, celula);                                                    // passa a celula para o PLC
                                                    ModBus.writePLC(3, peca_trans_1);
                                                    ModBus.writePLC(4, peca_trans_2);
                                                    ModBus.writePLC(5, peca_trans_3);
                                                    ModBus.writePLC(6, peca_trans_4);
                                                    ModBus.writePLC(7, peca_trans_5);*/
                                                    
                                                    escreve_PLC(peca_orig);     
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca(n_ordem, numero_serie);
                                                    }
                                                
                                                
                                                    //---------------------------------------------------------------------------------------------------------------------
                                                
                                                    // verificar se a quantidade è zero, porque se for, tenho de remover do vetor pedidos pendentes
                                                    // e fazer shift a todos os elementos do vetor
                                                
                                                    //---------------------------------------------------------------------------------------------------------------------
                                                

                                                
                                                    //--------------------------------------------------------------------------------------------------------------------
          

                                                }
                                            
                                                else if (numero_ordem.indexOf(n_ordem) > -1)
                                                //else if(this.vetor_aux_ped_pendentes[i] == 1)               // quer dizer que já tem a hora de inicio guardada e entao só precisa de executar a função
                                                {
                                                    // tenho de retirar 1 à quantidade -----------------------------------------------------------------------------------
                                                
                                                    quant = Integer.parseInt(quantidade);                   // converte para inteiro a quantidade

                                                    quant = quant - 1;

                                                    quantidade = Integer.toString(quant);                                       // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    /*ModBus.writePLC(2, celula);                                                    // passa a celula para o PLC
                                                    ModBus.writePLC(3, peca_trans_1);
                                                    ModBus.writePLC(4, peca_trans_2);
                                                    ModBus.writePLC(5, peca_trans_3);
                                                    ModBus.writePLC(6, peca_trans_4);
                                                    ModBus.writePLC(7, peca_trans_5);*/
                                                    
                                                    escreve_PLC(peca_orig);
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca(n_ordem, numero_serie);
                                                    }
                                                //---------------------------------------------------------------------------------------------------------------------
                                                
                                                // verificar se a quantidade è zero, porque se for, tenho de remover do vetor pedidos pendentes
                                                // e fazer shift a todos os elementos do vetor
                                                
                                                //---------------------------------------------------------------------------------------------------------------------
    
                                                }
                                            }
                                        }
                                            
                                        break;

                            //--------------------------------------------------------MONTAGEM----------------------------------------------------------------------------------------------
                            //------------------------------------------------------------------------------------------------------------------------------------------------------
                            case "M":                                                           // se for uma montagem
                
                                        n_ordem  = vetor_pedidos_pendentes[i].substring(1, 4);
                                        peca_1 = vetor_pedidos_pendentes[i].substring(4, 5);
                                        peca_2 = vetor_pedidos_pendentes[i].substring(5, 6);
                                        quantidade = vetor_pedidos_pendentes[i].substring(6, 8);
                                    
                                        //celula = escolha_caminho.Caminho_Associado_Montagem();
                                        
                                        if(celula == -1)
                                        {
                                            break;                              // ambas as células estão indisponiveis
                                        }
                                        
                                        else
                                        {
                                            // executar a funcao de Montagem
                                            
                                            break;
                                        }
                             
                            //--------------------------------------------------------DESCARGA----------------------------------------------------------------------------------------------
                            //------------------------------------------------------------------------------------------------------------------------------------------------------
                            case "U":                                                           // se for uma descarga
                
                                        n_ordem  = vetor_pedidos_pendentes[i].substring(1, 4);
                                        peca_1 = vetor_pedidos_pendentes[i].substring(4, 5);
                                        peca_2 = vetor_pedidos_pendentes[i].substring(5, 6);
                                        quantidade = vetor_pedidos_pendentes[i].substring(6, 8);
                    
                                        //celula = escolha_caminho.Caminho_Associado_Descarga(celula);
                
                                        if(celula == -1)
                                        {
                                            break;                              // ambas as células estão indisponiveis (nap existe caminhos disponiveis)
                                        }
                                        
                                        else
                                        {
                                            // executar a funcao de Descarga
                                            
                                            break;
                                        }
            
                        default:
                                System.out.println("A string está no formato errado");
                                break;
        }
                        
                        // if (caminho == -1)
                        //{
                        //  break;
                        //}
                        
                        
                        // else
                        //{
                        // vai ter de inserir pedido_execução
                        // vai ter de actualizar a quantidade associada ao pedido
                        //
                        //Executa pedido (vetor_pedidos_pendentes[i], caminho); 
                        //
                        //}
                        
                        
                    //}
                
                }
        }
        }
    }


}
