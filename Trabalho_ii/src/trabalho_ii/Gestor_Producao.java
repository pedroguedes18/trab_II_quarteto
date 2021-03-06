
package trabalho_ii;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Gestor_Producao implements Runnable {
    
    private final String [] vetor_pedidos_pendentes = new String [15];          // cria um vetor de pedidos pendentes
    private final int [] vetor_aux_ped_pendentes = new int [15];                // vetor auxiliar de pedidos pendes que indicam se o processo já entrou em execução alguma vez
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    private final ArrayList<String> todos_pedidos = new ArrayList<> ();
    private final ArrayList<String> numero_ordem = new ArrayList<> ();                          // numero de ordem em execução
    private final ArrayList<String> numero_ordem_pendentes = new ArrayList<> ();                // todos os numeros de ordem
    private final ArrayList<String> horaData_init_pedidos_pendentes = new ArrayList<> ();       // assumi que tem ligacao directa ao vetor_pedidos_execucao
    private final ArrayList<String> horaData_final_pedidos_pendentes = new ArrayList<>();       // " " " "
    private final ArrayList<String> horaData_entrada_pedidos_pendentes = new ArrayList<>();     // hora de entrada dos pedidos pendentes
    private final ArrayList<Integer> pecas_pendentes = new ArrayList<>();                      // indica as pecas produzidas em cada ordem
    private final ArrayList<Integer> pecas_execucao = new ArrayList<>();           // cria um vetor de pedidos execucao. É uma replica dos pedidos na medida em que diz quantas pecas estao no sistema a ser processadas
    
    private int celula;
    private int peca_trans_1, peca_trans_2, peca_trans_3, peca_trans_4, peca_trans_5;
    private int numero_serie = 0;
    private int sensorAT2;
    private int sensorCT3;
    private int sensorPM1;
    private int sensorPM2;
    private int num_serie_AT2 = 0;
    private int num_serie_CT3 = 0;
    private int num_serie_PM1 = 0;
    private int num_serie_PM2 = 0;
    private int estado = 0;
    private int aux_estado = 0;
    private int aux;
    
    private int[] Pusher_1 = new int [8];
    private int[] Pusher_2 = new int [8];
    private int[] maquina_c1_b = new int [3];   // pecas do tipo 2 4 e 7
    private int[] maquina_c1_c = new int [3];   // pecas do tipo 3 6 7
    private int[] maquina_c2_a = new int [3];   // pecas do tipo 1 3 5 
    private int[] maquina_c2_b = new int [3];   //...
    private int[] maquina_c3_b = new int [3];
    private int[] maquina_c3_c = new int [3];
    private int[] maquina_c4_a = new int [3];
    private int[] maquina_c4_b = new int [3];
    
    private int tempo_maq_c1_b =0;
    private int tempo_maq_c1_c =0;
    private int tempo_maq_c2_a =0;
    private int tempo_maq_c2_b =0;
    private int tempo_maq_c3_b =0;
    private int tempo_maq_c3_c =0;
    private int tempo_maq_c4_a =0;
    private int tempo_maq_c4_b =0;
    private int maq_c1_b_total =0;
    private int maq_c1_c_total =0;
    private int maq_c2_a_total =0;
    private int maq_c2_b_total =0;
    private int maq_c3_b_total =0;
    private int maq_c3_c_total =0;
    private int maq_c4_a_total =0;
    private int maq_c4_b_total =0;
    
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
            this.todos_pedidos.add(ordem);                                      // fica com todos os pedidos iniciais
            
            System.out.println("----------------------------------------------------------------------------------\n"
                    + "-------------------------------------------------------------------------------------");
            
            System.out.println("O texto adicionado na posicao " + pos + " foi: " + this.vetor_pedidos_pendentes[pos]);
        
            
            System.out.println("----------------------------------------------------------------------------------\n"
                    + "-------------------------------------------------------------------------------------");
            
            // vou associar uma hora de entrada do pedido no sistema, ou seja, ao numero de ordem
            
            String n_ord  =this.vetor_pedidos_pendentes[pos].substring(1, 4);
            String quantidade = this.vetor_pedidos_pendentes[pos].substring(6, 8);
            int quant = Integer.parseInt(quantidade);
                                
            Date data_inicio = new Date();
        
            String hourDate_inicio = dateFormat.format(data_inicio);            // devolve a hora e a data que o pedido comecou a sua execucao
            
            numero_ordem_pendentes.add(n_ord);                                  //fica guardado o numero de ordem de um pedido; associado a ele estão as respectivas datas
                                                                                // deste modo quando o é removido do vetor de pedidos pendentes, tenho sempre acesso ao número de ordem
            
            pecas_pendentes.add(quant);                                         // insere quantas pecas foram pedidas associadas sempre ao numero_ordem_pendentes
            pecas_execucao.add(0);
            
            int n_ord_int = numero_ordem_pendentes.indexOf(n_ord);
            
            horaData_entrada_pedidos_pendentes.add(n_ord_int,hourDate_inicio);  // na posição igual ao seu numero de ordem associa-lhe a data do vetor de pedidos pendentes                            
            horaData_init_pedidos_pendentes.add(n_ord_int,"0");                 // tenho de garantir que são inicializados 
            horaData_final_pedidos_pendentes.add(n_ord_int,"0");
            
        }
        
        else
        {
            System.out.println("O vetor está cheio. Não é possivel adicionar mais pedidos");
        }
    }
    
    public void insere_vetor_pedidos_execucao(int pos, int celula)
    {
            String n_ordem  =this.vetor_pedidos_pendentes[pos].substring(1, 4);
            
            int posicao = numero_ordem_pendentes.indexOf(n_ordem);
        
            int quant = pecas_execucao.get(posicao);
            quant++;
            
            pecas_execucao.set(posicao, quant);
            
            new Thread()
                {
                    @Override
                    public void run()
                    {
                        Escolha_Caminho EC = Escolha_Caminho.getInstance();
                        int quant;
                        int posicao = numero_ordem_pendentes.indexOf(n_ordem);
                        
                        switch (celula)
                        {
                            case 1:
                                    while(EC.celula_1.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);

                                    break;
                                    
                            case 2:
                                    while(EC.celula_2.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);
                                    
                                    break;
                            
                            case 3:
                                    while(EC.celula_3.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);
                                          
                                    break;
                            case 4:
                                    while(EC.celula_4.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);

                                    break;
                                    
                            case 5:
                                    while(EC.celula_5.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);

                                    break;
                                    
                            case 6:
                                    while(EC.celula_6.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                                    System.out.println("devia retirar agora");
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);

                                    break;
                                    
                            case 7:
                                    while(EC.celula_7.DisponibilidadeCelula() == 0)
                                    {
                                        System.out.flush();
                                    }
                                    
                                    System.out.println("");
                        
                                    quant = pecas_execucao.get(posicao);
                                    quant--;

                                    pecas_execucao.set(posicao, quant);

                                    break;
                        }
                        
                    }
                }.start();

    }
    
    public void escreve_PLC(int peca_origem, int peca_final,int quantidade)
    {
        System.out.println("entrou no ciclo escrever");
        aux_estado = 1;
        //System.out.println("aux_estado: " +aux_estado);
        //System.out.println("estado: " +estado);
        
        numero_serie = numero_serie +1;
        
        ReentrantLock lock = new ReentrantLock();
       
        lock.lock();
        try { 
            ModBus.writePLC(8, numero_serie);

            ModBus.writePLC(1, peca_origem);
        }
        finally 
            {
                lock.unlock();
            }
    
        System.out.println("Escreveu o que tinha de escrever\n");
        /*try {       
              Thread.sleep(10);
            } catch (InterruptedException ex) 
                {
                    Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                }*/
        
        
        
        //-------------------------------------- PARA VOLTAR A ESCREVER CASO NAO ESCREVA-------------------------------
        //------------------------------------------------------------------------------------------------------------
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date tempo = new Date();
        String dataFormatada = sdf.format(tempo);
        
        String segundos = dataFormatada.substring(6, 8);
        
        int seg = Integer.parseInt(segundos);
        int seg2 = 0;
        int dif = 0;
        //System.out.println("segundos_1 = " +seg);
        
        while(estado == 1)  // significa que ainda esta a retirar a peca, pois quando retira vai para o estado 2
        {
            System.out.flush();
            /*System.out.flush();
            tempo = new Date();
            dataFormatada = sdf.format(tempo);
            String segundos2 = dataFormatada.substring(6, 8);
            seg2 = Integer.parseInt(segundos2);
            dif= seg2-seg;
            
            System.out.flush();
            
            if(dif > 10)    // demorou mais de 10s a tirar peça, volta a tirar
            {
                System.out.println("já passou mais de 10 segundos e não mandou nada");
                
                Escolha_Caminho E_C = Escolha_Caminho.getInstance();
                
                switch (celula)
                {
                    case 1:
                        E_C.celula_1.IncrementarDisponibilidade();
                        break;
                    case 2:
                        E_C.celula_2.IncrementarDisponibilidade();
                        break;
                    case 3:
                        E_C.celula_3.IncrementarDisponibilidade();
                        break;
                    case 4:
                        E_C.celula_4.IncrementarDisponibilidade();
                }
                
                celula = E_C.Associar_Celulas_Transformaçao(peca_origem, peca_final, quantidade);
                
                lock.lock();
                try { 
                        ModBus.writePLC(8, numero_serie);

                        ModBus.writePLC(1, peca_origem);
                    }
                finally 
                {
                    lock.unlock();
                }
    
                System.out.println("Voltou a escrever o que tinha de escrever\n");
                try {       
                        Thread.sleep(10);
                    } catch (InterruptedException ex) 
                        {
                        Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                        }
        
                ModBus.writePLC(1, 0);
                
                seg = seg2;
            }*/
            
        }
        ModBus.writePLC(1, 0);
        //-------------------------------------- PARA VOLTAR A ESCREVER CASO NAO ESCREVA-------------------------------
        //------------------------------------------------------------------------------------------------------------
        
    }
    
    public void escreve_PLC_montagem_descarga(int peca)
    {
        System.out.println("entrou no ciclo escrever");
        aux_estado = 1;
        
        numero_serie = numero_serie +1;
        
        ReentrantLock lock = new ReentrantLock();
       
        lock.lock();
        try { 
            ModBus.writePLC(8, numero_serie);

            ModBus.writePLC(1, peca);
        }
        finally 
            {
                lock.unlock();
            }
    
        System.out.println("Escreveu o que tinha de escrever\n");
        /*try {       
              Thread.sleep(10);
            } catch (InterruptedException ex) 
                {
                    Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                }*/
        
        
        
        //-------------------------------------- PARA VOLTAR A ESCREVER CASO NAO ESCREVA-------------------------------
        //------------------------------------------------------------------------------------------------------------
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date tempo = new Date();
        String dataFormatada = sdf.format(tempo);
        
        String segundos = dataFormatada.substring(6, 8);
        
        int seg = Integer.parseInt(segundos);
        int seg2 = 0;
        int dif = 0;
        //System.out.println("segundos_1 = " +seg);
        
        while(estado == 1)  // significa que ainda esta a retirar a peca, pois quando retira vai para o estado 2
        {
            System.out.flush();
            /*System.out.flush();
            tempo = new Date();
            dataFormatada = sdf.format(tempo);
            String segundos2 = dataFormatada.substring(6, 8);
            seg2 = Integer.parseInt(segundos2);
            dif= seg2-seg;
            
            System.out.flush();
            
            if(dif > 10)    // demorou mais de 10s a tirar peça, volta a tirar
            {
                System.out.println("já passou mais de 10 segundos e não mandou nada");
                
                //Escolha_Caminho E_C = Escolha_Caminho.getInstance();
                
                //E_C.celula_5.IncrementarDisponibilidade();
                
                
                //celula = E_C.Associar_Celulas_Montagem();
                
                lock.lock();
                try { 
                        ModBus.writePLC(8, numero_serie);

                        ModBus.writePLC(1, peca);
                    }
                finally 
                {
                    lock.unlock();
                }
    
                System.out.println("Voltou a escrever o que tinha de escrever\n");
                try {       
                        Thread.sleep(10);
                    } catch (InterruptedException ex) 
                        {
                        Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                        }
        
                ModBus.writePLC(1, 0);
                
                seg = seg2;
            }*/
            
        }
        
        ModBus.writePLC(1, 0);  // quer dizer que já pos a peca
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
    
    //------------------------------------------- FUNÇÕES PARA AS ESTATISTICAS--------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public int atualiza_total_pecas_descarga(int Pusher)
    {
        int total = 0;
        
        switch (Pusher)
        {
            case 1:
                    for(int z=0; z<8; z++)
                    {
                        total = total + Pusher_1[z];
                    }
                break;
                
            case 2:
                    for(int z=0; z<8; z++)
                    {
                        total = total + Pusher_2[z];
                    }
                break;
        }
         
        return total;
    }
    
    public void adiciona_peca_descarga(int peca, int pusher)
    {
        switch (pusher)
        {
           case 1:
                
                Pusher_1[peca-1]++;
                break;
            
           case 2:
                Pusher_2[peca-1]++;    
                break; 
        }     
    }
    
    public void atualiza_pecas_maquina()
    {
        for(int y=0; y<2; y++)
           {
               maquina_c1_b[y] = ModBus.readPLC(y+18, 0);
               maquina_c1_c[y] = ModBus.readPLC(y+21, 0);
               //------------------------------------
               maquina_c2_a[y] = ModBus.readPLC(y+24, 0);
               maquina_c2_b[y] = ModBus.readPLC(y+27, 0);
               //------------------------------------
               maquina_c3_b[y] = ModBus.readPLC(y+30, 0);
               maquina_c3_c[y] = ModBus.readPLC(y+33, 0);
               //------------------------------------
               maquina_c4_a[y] = ModBus.readPLC(y+36, 0);
               maquina_c4_b[y] = ModBus.readPLC(y+39, 0); 
           }
        
        maq_c1_b_total = maquina_c1_b[0] + maquina_c1_b[1] + maquina_c1_b[2];
        maq_c1_c_total = maquina_c1_c[0] + maquina_c1_c[1] + maquina_c1_c[2];
        //------------------------------------------------------------------
        maq_c2_a_total = maquina_c2_a[0] + maquina_c2_a[1] + maquina_c2_a[2];
        maq_c2_b_total = maquina_c2_b[0] + maquina_c2_b[1] + maquina_c2_b[2];
        //------------------------------------------------------------------
        maq_c3_b_total = maquina_c3_b[0] + maquina_c3_b[1] + maquina_c3_b[2];
        maq_c3_c_total = maquina_c3_c[0] + maquina_c3_c[1] + maquina_c3_c[2];
        //------------------------------------------------------------------
        maq_c4_a_total = maquina_c4_a[0] + maquina_c4_a[1] + maquina_c4_a[2];
        maq_c4_b_total = maquina_c4_b[0] + maquina_c4_b[1] + maquina_c4_b[2];
    }
    
    public void atualiza_tempo_operacao_maquina()    // Atualiza todos os tempos que cada maquina esteve a trabalhar em minutos
    {
        // Atualiza pecas nas maquinas
            
           atualiza_pecas_maquina();
        
        // calcular o tempo
           
           tempo_maq_c1_b =(25*maquina_c1_b[0] + 25*maquina_c1_b[1]+20*maquina_c1_b[2]);
           tempo_maq_c1_c =(25*maquina_c1_c[0] + 10*maquina_c1_c[1]+30*maquina_c1_c[2]);
           //-----------------------------------------------------------------------------
           tempo_maq_c2_a =(20*maquina_c2_a[0] + 25*maquina_c2_a[1]+25*maquina_c2_a[2]);
           tempo_maq_c2_b =(25*maquina_c2_b[0] + 25*maquina_c2_b[1]+20*maquina_c2_b[2]);
           //-----------------------------------------------------------------------------
           tempo_maq_c3_b =(25*maquina_c3_b[0] + 25*maquina_c3_b[1]+20*maquina_c3_b[2]);
           tempo_maq_c3_c =(25*maquina_c3_c[0] + 10*maquina_c3_c[1]+30*maquina_c3_c[2]);
           //-----------------------------------------------------------------------------
           tempo_maq_c4_a =(20*maquina_c4_a[0] + 25*maquina_c4_a[1]+25*maquina_c4_a[2]);
           tempo_maq_c4_b =(25*maquina_c4_b[0] + 25*maquina_c4_b[1]+20*maquina_c4_b[2]);
    }
    
    public int procura_vetor(String n_ordem)
    {
        //retorna a posição do vetor onde está o numero de ordem
        
        int pos=-1;
        
        for (int i=0; vetor_pedidos_pendentes != null ; i++)                    // percorre o vetor até encontrar uma posição que tenha o numero de serie
        {

            String numero_ordem = this.vetor_pedidos_pendentes[i].substring(1, 4);

            if(numero_ordem.equals(n_ordem))
            {
                pos = i;
                break;
            }
            
        }
        
        return pos;
    }
    
    public int get_pecas_execucao(String n_ordem)
    {
        // tenho de ir ver em que posicao está o n_ordem no vetor de pedidos pendentes
        // pois está associada ao numero de pecas em execucao
        int quantidade = 0;

        int pos = numero_ordem_pendentes.indexOf(n_ordem);
        
        quantidade = pecas_execucao.get(pos);
        
        return quantidade;
            
    }
    
    public int get_pecas_pendentes(String n_ordem)
    {
        int quantidade = 0;

        int pos = numero_ordem_pendentes.indexOf(n_ordem);
        
        quantidade = pecas_pendentes.get(pos);
        
        return quantidade;
        
    }
    
    
        public int get_quantidade_original(int pos)
    {
        //devolve a quantide que inicialmente tinha uma ordem
        
        int quantidade = 0;

        quantidade = Integer.parseInt(todos_pedidos.get(pos).substring(6, 8));
        
        return quantidade;
        
    }    
        
    
    public String ver_estado(int pos)                                           // recebo a posicao do que estou a anaalisar no numero_ordem_pendentes
    {
        // devolve o estado a ordem em questão
        
        String estado;
        
        if(!(horaData_final_pedidos_pendentes.get(pos).equals("0")))
        {
            estado = "Executado";
        }
        else if(!(horaData_init_pedidos_pendentes.get(pos).equals("0")))
        {
            estado = "Em execução";
        }
        else
            estado = "Pendente";
        
        return estado;
    }
    
    public int pecas_pendentes(int pos)                                     // recebo a posicao do que estou a anaalisar no numero_ordem_pendentes
    {
        // devolve o numero de pecas pendentes de uma ordem de execução
        
        int pecas_pendentes = 0;
        
        String estado = ver_estado(pos);
        
        if(estado.equals("Executado"))
        {
            pecas_pendentes = 0;
        }
        
        else if(estado.equals("Em execução"))
        {
            String n_ordem = numero_ordem_pendentes.get(pos);
            
            pecas_pendentes = get_pecas_pendentes(n_ordem);
        }
        
        else if(estado.equals("Pendente"))
        {
            pecas_pendentes = get_quantidade_original(pos);
        }
        
        return pecas_pendentes;
    }
    
    public int pecas_produzidas(int pos)                                        // recebo a posicao do que estou a anaalisar no numero_ordem_pendentes
    {
        int pecas_produzidas = 0;
        int pecas_pendentes = 0;
        int pecas_totais = 0;
        int pecas_execucao = 0;
        String estado = ver_estado(pos);
        
        if(estado.equals("Executado"))
        {
            pecas_produzidas = get_quantidade_original(pos);
        }
        
        else if(estado.equals("Em execução"))
        {
            String n_ordem = numero_ordem_pendentes.get(pos);
            
            pecas_totais = get_quantidade_original(pos);
            pecas_pendentes = get_pecas_pendentes(n_ordem);
            pecas_execucao = get_pecas_execucao(n_ordem);
            
            pecas_produzidas = pecas_totais - pecas_pendentes - pecas_execucao;
        }
        
        else if(estado.equals("Pendente"))
        {
            pecas_produzidas = 0;
        }
        
        return pecas_produzidas;
    }
    
        
    public void teste_de_estatisticas()
    {
        // imprime todas as ordens que já foram executadas
        
        System.out.println("\n");
        System.out.println("numero ordem   |   tipo de ordem   | estado  | pecas_produzidas  |  Pecas em producao  |  Pecas pendentes | hora de entrada | hora de inicio execucao | hora de fim");
        
        for(int i=0; i<numero_ordem_pendentes.size(); i++)
        {
            String n_ordem = numero_ordem_pendentes.get(i);
            
            System.out.println(numero_ordem_pendentes.get(i) +"  |  "+ todos_pedidos.get(i)+"  |  "+ ver_estado(i) + "  |  "+ pecas_produzidas(i) + "  |  "+ get_pecas_execucao(n_ordem) + "  |  "+ pecas_pendentes(i)
                    + "  |  "+ horaData_entrada_pedidos_pendentes.get(i) + "  |  "+ horaData_init_pedidos_pendentes.get(i) + "  |  "+ horaData_final_pedidos_pendentes.get(i) );
        }
        
        System.out.println("\n");
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void thread_espera_peca(String tipo_ordem,String ordem_numero, int num_serie)                                             //para testar se funciona deste modo
    {
        new Thread()
                {
                    @Override
                    public void run()
                    {
                        //ler como estão os valores
                        
                        if (tipo_ordem.equals("T"))
                        {
                            System.out.println("numero serie suposto" +num_serie);
                        
                            sensorAT2 = ModBus.readPLC(0, 0);                // readPLC(numRegisto,0)
                            num_serie_AT2 = ModBus.readPLC(1, 0);            // readPLC(numRegisto,0) 
                        
                            while (sensorAT2 != 1 || num_serie_AT2 != num_serie)
                            {
                                try {       
                                    Thread.sleep(25);
                                } catch (InterruptedException ex) {
                                         Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                                }
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
                        
                            int posicao =numero_ordem_pendentes.indexOf(ordem_numero);                          //vai ver em que posicao está o numero de ordem para lhe associar a sua hora de fim
        
                            horaData_final_pedidos_pendentes.set(posicao, hourDate_fim);
                            
                            System.out.println("NUMERO DE ORDEM           : " + ordem_numero);
                            System.out.println("DATA DE ENTRADA DO PEDIDO : " + horaData_entrada_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE INICIO DE EXECUÇÃO: " +  horaData_init_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE FIM DE EXECUÇÃO   : " + horaData_final_pedidos_pendentes.get(posicao));        // só para ver se funciona a data
                            
                            atualiza_pecas_maquina();
                            atualiza_tempo_operacao_maquina();
                            teste_de_estatisticas();
                            
                            
                            System.out.println("------------------------ESTATISTICAS-----------------------------------------");
                            System.out.println("--------------------------MAQUINAS-------------------------------------------");
                            System.out.println("            |  Maquina-a C2  |  Maquina-a C4");
                            System.out.println("Total_pecas |  "+maq_c2_a_total+"          |       "+maq_c4_a_total);
                            System.out.println("Peca-P1     |  "+maquina_c2_a[0]+"          |       "+maquina_c4_a[0]);
                            System.out.println("Peca-P3     |  "+maquina_c2_a[1]+"          |       "+maquina_c4_a[1]);
                            System.out.println("Peca-P5     |  "+maquina_c2_a[2]+"          |       "+maquina_c4_a[2]);
                            System.out.println("Tempo_Oper. |  "+tempo_maq_c2_a+"seg       |       "+tempo_maq_c4_a+"seg");
                            System.out.println("_____________________________________________________________________________");
                            System.out.println("            |Maquina-b C1  |  Maquina-b C2  |  Maquina-b C3  |  Maquina-b C4");
                            System.out.println("Total_pecas |  "+maq_c1_b_total+"          |       "+maq_c2_b_total+"          |       "+maq_c3_b_total+"          |       "+maq_c4_b_total);
                            System.out.println("Peca-P2     |  "+maquina_c1_b[0]+"          |       "+maquina_c2_b[0]+"          |       "+maquina_c3_b[0]+"          |       "+maquina_c4_b[0]);
                            System.out.println("Peca-P4     |  "+maquina_c1_b[1]+"          |       "+maquina_c2_b[1]+"          |       "+maquina_c3_b[1]+"          |       "+maquina_c4_b[1]);
                            System.out.println("Peca-P7     |  "+maquina_c1_b[2]+"          |       "+maquina_c2_b[2]+"          |       "+maquina_c3_b[2]+"          |       "+maquina_c4_b[2]);
                            System.out.println("Tempo_Oper. |  "+tempo_maq_c1_b+"seg       |       "+tempo_maq_c2_b+"seg       |       "+tempo_maq_c3_b+"seg       |       "+tempo_maq_c4_b+"seg");
                            System.out.println("_____________________________________________________________________________");
                            System.out.println("            |Maquina-c C1  |  Maquina-c C3");
                            System.out.println("Total_pecas |  "+maq_c1_c_total+"          |       "+maq_c3_c_total);
                            System.out.println("Peca-P3     |  "+maquina_c1_c[0]+"          |       "+maquina_c3_c[0]);
                            System.out.println("Peca-P6     |  "+maquina_c1_c[1]+"          |       "+maquina_c3_c[1]);
                            System.out.println("Peca-P7     |  "+maquina_c1_c[2]+"          |       "+maquina_c3_c[2]);
                            System.out.println("Tempo_Oper. |  "+tempo_maq_c1_c+"seg       |       "+tempo_maq_c3_c+"seg");
                            
                            System.out.println("-----------------------------------------------------------------------------");
                            System.out.println("-----------------------------------------------------------------------------");
                            
    
                        }
                        
                        else if (tipo_ordem.equals("M"))
                        {
                            System.out.println("numero serie suposto" +num_serie);
                        
                            sensorCT3 = ModBus.readPLC(8, 0);                // readPLC(numRegisto,0)
                            num_serie_CT3 = ModBus.readPLC(13, 0);            // readPLC(numRegisto,0) 
                        
                            while (sensorCT3 != 1 || num_serie_CT3 != num_serie)
                            {
                                //fica aqui à espera e vai atualizando as variaveis
                                System.out.flush();                            
                                sensorCT3 = ModBus.readPLC(8, 0);           // readPLC(numRegisto,0)
                                num_serie_CT3 = ModBus.readPLC(13, 0);       // readPLC(numRegisto,0)
                            }
                       
                            System.out.println("sensor_lido: " + sensorCT3);
                            System.out.println("num_serie_lido: "+num_serie_CT3);
                       
                            // vai atulizar hora de fim
                                                    
                            Date date_fim = new Date();
        
                            String hourDate_fim = dateFormat.format(date_fim);                                  // devolve a hora e a data que o pedido finalizou a sua execucao
                        
                            int posicao =numero_ordem_pendentes.indexOf(ordem_numero);                                  //vai ver em que posicao está o numero de ordem para lhe associar a sua hora de fim
        
                            horaData_final_pedidos_pendentes.set(posicao, hourDate_fim);
                            
                            System.out.println("NUMERO DE ORDEM           : " + ordem_numero);
                            System.out.println("DATA DE ENTRADA DO PEDIDO : " + horaData_entrada_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE INICIO DE EXECUÇÃO: " +  horaData_init_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE FIM DE EXECUÇÃO   : " + horaData_final_pedidos_pendentes.get(posicao));        // só para ver se funciona a data
                            
                            teste_de_estatisticas();
                        }
                        
                        else if (tipo_ordem.equals("U1"))
                        {
                            System.out.println("numero serie suposto" +num_serie);
                        
                            // tenho de saber a que pusher tenho de ler, se não vou estar a ler dos dois desnecessáriamente
                            
                            //sensorPM1 = ModBus.readPLC(9, 0);                // readPLC(numRegisto,0)
                            num_serie_PM1 = ModBus.readPLC(15, 0);            // readPLC(numRegisto,0) 
                        
                            while (/*sensorPM1 != 1 ||*/ num_serie_PM1 != num_serie)
                            {
                                //fica aqui à espera e vai atualizando as variaveis
                                System.out.flush();                            
                                //sensorPM1 = ModBus.readPLC(9, 0);           // readPLC(numRegisto,0)
                                num_serie_PM1 = ModBus.readPLC(15, 0);       // readPLC(numRegisto,0)
                            }
                       
                            //System.out.println("sensor_lido: " + sensorAT2);
                            System.out.println("num_serie_lido: "+num_serie_AT2);
                       
                            // vai atulizar hora de fim
                                                    
                            Date date_fim = new Date();
        
                            String hourDate_fim = dateFormat.format(date_fim);                                  // devolve a hora e a data que o pedido finalizou a sua execucao
                        
                            int posicao =numero_ordem_pendentes.indexOf(ordem_numero);                                  //vai ver em que posicao está o numero de ordem para lhe associar a sua hora de fim
        
                            horaData_final_pedidos_pendentes.set(posicao, hourDate_fim);
                            
                            System.out.println("NUMERO DE ORDEM           : " + ordem_numero);
                            System.out.println("DATA DE ENTRADA DO PEDIDO : " + horaData_entrada_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE INICIO DE EXECUÇÃO: " +  horaData_init_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE FIM DE EXECUÇÃO   : " + horaData_final_pedidos_pendentes.get(posicao));        // só para ver se funciona a data
                            
                            teste_de_estatisticas();
                            
                            System.out.println("------------------------ESTATISTICAS-----------------------------------------");
                            System.out.println("-----------------------------------------------------------------------------");
                            System.out.println("Total pecas descarregadas Pusher 1: " +atualiza_total_pecas_descarga(1));
                            for(int y=0; y<8; y++)
                            {
                                System.out.println("Total pecas Pusher 1: P"+(y+1)+" - "+ Pusher_1[y]);
                            }
                            System.out.println("-----------------------------------------------------------------------------");
                            System.out.println("-----------------------------------------------------------------------------");
                        }
                        
                        else if (tipo_ordem.equals("U2"))
                        {
                            System.out.println("numero serie suposto" +num_serie);
                        
                            // tenho de saber a que pusher tenho de ler, se não vou estar a ler dos dois desnecessáriamente
                            
                            //sensorPM2 = ModBus.readPLC(11, 0);                // readPLC(numRegisto,0)
                            num_serie_PM2 = ModBus.readPLC(16, 0);            // readPLC(numRegisto,0) 
                        
                            while (/*sensorPM2 != 1 ||*/ num_serie_PM2 != num_serie)
                            {
                                //fica aqui à espera e vai atualizando as variaveis
                                System.out.flush();                            
                                //sensorPM2 = ModBus.readPLC(11, 0);           // readPLC(numRegisto,0)
                                num_serie_PM2 = ModBus.readPLC(16, 0);       // readPLC(numRegisto,0)
                            }
                       
                            //System.out.println("sensor_lido: " + sensorAT2);
                            System.out.println("num_serie_lido: "+num_serie_AT2);
                       
                            // vai atulizar hora de fim
                                                    
                            Date date_fim = new Date();
        
                            String hourDate_fim = dateFormat.format(date_fim);                                  // devolve a hora e a data que o pedido finalizou a sua execucao
                        
                            int posicao =numero_ordem_pendentes.indexOf(ordem_numero);                                   //vai ver em que posicao está o numero de ordem para lhe associar a sua hora de fim
        
                            horaData_final_pedidos_pendentes.set(posicao, hourDate_fim);
                            
                            System.out.println("NUMERO DE ORDEM           : " + ordem_numero);
                            System.out.println("DATA DE ENTRADA DO PEDIDO : " + horaData_entrada_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE INICIO DE EXECUÇÃO: " +  horaData_init_pedidos_pendentes.get(posicao));
                            System.out.println("DATA DE FIM DE EXECUÇÃO   : " + horaData_final_pedidos_pendentes.get(posicao));        // só para ver se funciona a data
                            
                            teste_de_estatisticas();
                            
                            System.out.println("------------------------ESTATISTICAS-----------------------------------------");
                            System.out.println("-----------------------------------------------------------------------------");
                            System.out.println("Total pecas descarregadas Pusher 2: " +atualiza_total_pecas_descarga(2));
                            for(int y=0; y<8; y++)
                            {
                                System.out.println("Total pecas Pusher 1: P"+(y+1)+" - "+ Pusher_2[y]);
                            }
                            System.out.println("-----------------------------------------------------------------------------");
                            System.out.println("-----------------------------------------------------------------------------");
                        }
                        

                    }
                }.start();
    }

    
    public void maquina_estados()                                               
    {
        new Thread()
                {
                    @Override
                    public void run()
                    {
                        System.out.println("estado: " +estado);
                        while(true)
                        {
                            try {       
                                    Thread.sleep(25);
                                } catch (InterruptedException ex) {
                                         Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            System.out.flush();
                            if(estado == 0 && aux_estado == 1)                        // passa para o estado 1 quando recebe ordem do escolha caminho
                            {
                                estado = 1;
                                //System.out.println("estado: " +estado);
                                System.out.flush();
                            }
                          
                            else if(estado == 1 && ModBus.readPLC(7,0)==1 )     // sensor AT1
                            {
                                estado = 2;
                                //System.out.println("estado: " +estado);
                                System.out.flush();
                            }
                          
                            else if (estado == 2 && ModBus.readPLC(2,0)== 1)    // tapete do armazem livre
                            {
                                estado = 0;
                                aux_estado = 0;
                                //System.out.println("estado: " +estado);
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
        int pos_num_ordem;
        
        //colocar todos os contadores dos Pusher a zero
        for(int p=0; p<8; p++)
        {
            Pusher_1[p] =0;
            Pusher_2[p] =0;
        }
        

        while(true)
        {
            System.out.flush();
            
            //if(this.vetor_pedidos_pendentes[0] != null)                         // quer dizer que já tem pelo menos um pedido pendente; o vetor está não esta vazio logo precisa de executar
            //{
                
                for(int i=0; i < 15; i++)     // percorre o vetor de pedidos pendentes do inicio até à ultima posicao ocupada
                {
                    if(vetor_pedidos_pendentes[i] != null)
                    {
                    aux = 0;     
                    Escolha_Caminho escolha_caminho = Escolha_Caminho.getInstance();        // vai buscar a instancia da Classe Escolha_caminho      
                    
                    switch (vetor_pedidos_pendentes[i].substring(0, 1))                     // primeiro vê que tipo de instrução e separa os parametros
                    {
                            //-----------------------------------------------------TRANSFORMACAO-------------------------------------------------------------------------------------------------
                            //------------------------------------------------------------------------------------------------------------------------------------------------------
                             case "T":                                                           // se for uma transformação
                                        //System.out.println("entrou no swith");
                                        n_ordem  =this.vetor_pedidos_pendentes[i].substring(1, 4);
                                        peca_1 = this.vetor_pedidos_pendentes[i].substring(4, 5);
                                        peca_2 = this.vetor_pedidos_pendentes[i].substring(5, 6);
                                        quantidade = this.vetor_pedidos_pendentes[i].substring(6, 8);
                                        
                                        int peca_orig = Integer.parseInt(peca_1);
                                        int peca_final = Integer.parseInt(peca_2);


                                        quant = Integer.parseInt(quantidade);                                       // converte para inteiro a quantidade
                                                
                                        if( quant == 0)
                                        {
                                            
                                            remove_pedido_pendente(i);  // como é a primeira vez que vai ser executado, se a quantidade for zero não é um pedido válido logo removemos
                                           
                                        }
                                        
                                        else if(quant > 0)
                                        {
                                            while(estado != 0)
                                            {
                                                System.out.flush();
                                            }
                                            
                                            celula = escolha_caminho.Associar_Celulas_Transformaçao(peca_orig, peca_final, quant);
                                        
                                            
                                            if ( celula > 0)                    // quer dizer que existe uma célula disponivel para executar o pedido
                                            {
                                            
                                                aux = 1;    // significa que meteu/vai meter uma peça
                                               
                                                
                                                if (numero_ordem.indexOf(n_ordem) == -1)           // o pedido é a primeira vez que vai ser executado logo actualizamos o vetor de horas iniciais
                                                {
                                                    
                                                    numero_ordem.add(n_ordem);                     // adiciona o numero de ordem a ser executado
                                                     
                                                    int n_ord_int = numero_ordem_pendentes.indexOf(n_ordem);     // vai buscar a posicao que guardou o nº ordem quando foi
                                                    
                                                    Date data_inicio = new Date();
        
                                                    String hourDate_inicio = dateFormat.format(data_inicio);                            // devolve a hora e a data que o pedido comecou a sua execucao
        
                                                    horaData_init_pedidos_pendentes.add(n_ord_int, hourDate_inicio);                    //adiciona a hora de inicio na posicao correspondente
                                                    
                                                    System.out.println("DATA DE INICIO: " +horaData_init_pedidos_pendentes.get(n_ord_int));       // só para ver se funciona a data
                                                
                                                    
                                                    quant = quant - 1;
                                                    
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    System.out.println("ordem: "+n_ordem+ " - EM EXECUÇÃO: "+get_pecas_execucao(n_ordem));
                                                    
                                                    escreve_PLC(peca_orig, peca_final, quant);
                                                    //ModBus.writePLC(1, 0);      // para garantir que só tira uma peça
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca("T",n_ordem, numero_serie);
                                                    }
                                                    
                                                }
                                            
                                                else if (numero_ordem.indexOf(n_ordem) > -1)             // quer dizer que já tem a hora de inicio guardada e entao só precisa de executar a função
                                                {
                                                    
                                                    quant = quant - 1;
                                                    
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);                                       // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    System.out.println("ordem: "+n_ordem+ " - EM EXECUÇÃO: "+get_pecas_execucao(n_ordem));
                                                    
                                                    escreve_PLC(peca_orig, peca_final, quant);
                                                    //ModBus.writePLC(1, 0); 
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca("T",n_ordem, numero_serie);
                                                    }
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
                                    
                                        
                                        int peca_baixo = Integer.parseInt(peca_1);
                                        int peca_cima = Integer.parseInt(peca_2);
                                        
                                        quant = Integer.parseInt(quantidade);                                       // converte para inteiro a quantidade
                                                
                                        if( quant == 0)
                                        {
                                            System.out.println("quantidade = 0");
                                            remove_pedido_pendente(i);              // como é a primeira vez que vai ser executado, se a quantidade for zero não é um pedido válido logo removemos

                                        }
                                        
                                        else if(quant > 0)
                                        {
                                            
                                            while(estado != 0)
                                            {
                                                
                                                System.out.flush();
                                                
                                            }
                                            
                                            celula = escolha_caminho.Associar_Celulas_Montagem(); //para ir para a montagem.. tenho de ver se está livre
                                            
                                        
                                            if ( celula > 0)                    // quer dizer que existe uma célula disponivel
                                            {
                                                System.out.println("Montagem: entrou celula > 0");
                                                aux = 1;
                                                
                                                if (numero_ordem.indexOf(n_ordem) == -1)                                               // o pedido é a primeira vez que vai ser executado logo actualizamos o vetor de horas iniciais
                                                {
                                                    System.out.println("Montagem: numero de ordem ainda não foi nenhuma vez executado");
                                                    
                                                    numero_ordem.add(n_ordem);                                                          // adiciona o numero de ordem a ser executado

                                                    int n_ord_int = numero_ordem_pendentes.indexOf(n_ordem);                                      // vai buscar a posicao que guardou o nº ordem
                                                    
                                                    Date data_inicio = new Date();
        
                                                    String hourDate_inicio = dateFormat.format(data_inicio);                            // devolve a hora e a data que o pedido comecou a sua execucao
        
                                                    horaData_init_pedidos_pendentes.add(n_ord_int, hourDate_inicio);                    //adiciona a hora de inicio na posicao correspondente
                                                    
                                                    System.out.println("DATA DE INICIO: " +horaData_init_pedidos_pendentes.get(n_ord_int));       // só para ver se funciona a data
                                                
                                                    
                                                    quant = quant - 1;
                                                    
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    int peca_original = 0;
                                                    peca_trans_1 = 14;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //---------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_cima);    //manda a peça de cima
                                                    //ModBus.writePLC(1, 0);              //mete a zero a variavel tirar peça porque se nao no PLC não funciona, devido à forma como a Maq.Est. está feita
                                                    
                                                    
                                                    while(estado != 0)                  // só quando o tapete ficar novamente livre é que vai mandar a peça de cima
                                                    {
                                                        // espero que o tapete esteja livre para poder voltar a tirar uma peça
                                                        System.out.flush();
                                                        //celula = 0;
                                                    }
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    peca_original = 0;
                                                    peca_trans_1 = 0;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //-------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_baixo);            //manda a peça de cima
                                                    ModBus.writePLC(1, 0);
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca("M",n_ordem, numero_serie);
                                                    }
                                                
                                                }
                                            
                                                else if (numero_ordem.indexOf(n_ordem) > -1)               // quer dizer que já tem a hora de inicio guardada e entao só precisa de executar a função
                                                {
                                                    // tenho de retirar 1 à quantidade -----------------------------------------------------------------------------------
                                                    System.out.println("Montagem: numero de ordem já se encontra a ser executado.");
                                                    quant = quant - 1;
                                                    
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    int peca_original = 0;
                                                    peca_trans_1 = 14;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //---------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_cima);    //manda a peça de cima
                                                    //ModBus.writePLC(1, 0);              //mete a zero a variavel tirar peça porque se nao no PLC não funciona, devido à forma como a Maq.Est. está feita
                                                    
                                                    
                                                    while(estado != 0)                  // só quando o tapete ficar novamente livre é que vai mandar a peça de cima
                                                    {
                                                        // espero que o tapete esteja livre para poder voltar a tirar uma peça
                                                        System.out.flush();
                                                        //celula = 0;
                                                    }
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    peca_original = 0;
                                                    peca_trans_1 = 0;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //-------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_baixo);            //manda a peça de cima
                                                    ModBus.writePLC(1, 0);
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        thread_espera_peca("M",n_ordem, numero_serie);
                                                    }

                                                }
                                            }
                                        }
                                            
                                        break;
                             
                            //--------------------------------------------------------DESCARGA----------------------------------------------------------------------------------------------
                            //------------------------------------------------------------------------------------------------------------------------------------------------------
                            case "U":                                                           // se for uma descarga
                                        n_ordem  = vetor_pedidos_pendentes[i].substring(1, 4);
                                        peca_1 = vetor_pedidos_pendentes[i].substring(4, 5);
                                        peca_2 = vetor_pedidos_pendentes[i].substring(5, 6);
                                        quantidade = vetor_pedidos_pendentes[i].substring(6, 8);
                    
                                        
                                        int peca_descarga = Integer.parseInt(peca_1);
                                        int local_descarga = Integer.parseInt(peca_2);
                                        
                                        quant = Integer.parseInt(quantidade);                                       // converte para inteiro a quantidade
                                                
                                        if( quant == 0)
                                        {
                                            System.out.println("quantidade = 0");
                                            remove_pedido_pendente(i);              // como é a primeira vez que vai ser executado, se a quantidade for zero não é um pedido válido logo removemos

                                        }
                                        
                                        else if(quant > 0)
                                        {
                                            //System.out.println("quantidade maior que zero");
                                            while(estado != 0)
                                            {
                                                System.out.flush();
                                            }
                                            
                                            celula = escolha_caminho.Associar_Celulas_Descarga(local_descarga); //para ir para a montagem.. tenho de ver se está livre
                                            
                                        
                                            if ( celula > 0)                    // quer dizer que existe uma célula disponivel
                                            {
                                                aux = 1;
                                                
                                                if (numero_ordem.indexOf(n_ordem) == -1)                                               // o pedido é a primeira vez que vai ser executado logo actualizamos o vetor de horas iniciais
                                                {
                                                    numero_ordem.add(n_ordem);                                                          // adiciona o numero de ordem a ser executado

                                                    int n_ord_int = numero_ordem_pendentes.indexOf(n_ordem);                                      // vai buscar a posicao que guardou o nº ordem
                                                    
                                                    Date data_inicio = new Date();
        
                                                    String hourDate_inicio = dateFormat.format(data_inicio);                            // devolve a hora e a data que o pedido comecou a sua execucao
        
                                                    horaData_init_pedidos_pendentes.add(n_ord_int, hourDate_inicio);                    //adiciona a hora de inicio na posicao correspondente
                                                    
                                                    System.out.println("DATA DE INICIO: " +horaData_init_pedidos_pendentes.get(n_ord_int));       // só para ver se funciona a data
                                                
                                                    
                                                    quant = quant - 1;
                                                    
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    int peca_original = 0;
                                                    peca_trans_1 = 0;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //---------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_descarga);         //manda a peça de baixo
                                                    ModBus.writePLC(1, 0);              //mete a zero a variavel tirar peça porque se nao no PLC não funciona, devido à forma como a Maq.Est. está feita
                                                   
                                                    adiciona_peca_descarga(peca_descarga, local_descarga);  // aumenta no vetor do pusher correspondente, o tipo de peca que descarregou
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        if(local_descarga == 1)
                                                        {
                                                            thread_espera_peca("U1",n_ordem, numero_serie);
                                                        }
                                                        
                                                        else if(local_descarga == 2)
                                                        {
                                                            thread_espera_peca("U2",n_ordem, numero_serie);
                                                        } 
                                                    }
                                                
                                                }
                                            
                                                else if (numero_ordem.indexOf(n_ordem) > -1)               // quer dizer que já tem a hora de inicio guardada e entao só precisa de executar a função
                                                {
                                                    // tenho de retirar 1 à quantidade -----------------------------------------------------------------------------------
                                                
                                                    quant = quant - 1;
                                                   
                                                    pos_num_ordem = numero_ordem_pendentes.indexOf(n_ordem);    //vai buscar a posicao onde esta o numero de ordem
                                                    pecas_pendentes.add(pos_num_ordem, quant);

                                                    quantidade = Integer.toString(quant);     // converte para string a quantidade desejada

                                                    if (quant < 10) 
                                                    {
                                                        String zero = "0";

                                                        quantidade = zero.concat(quantidade);
                                                    }
                                                    
                                                    String aux = this.vetor_pedidos_pendentes[i].substring(0, 6);               // seleciona na ordem apenas o texto que nao vai ser alterado

                                                    this.vetor_pedidos_pendentes[i] = aux.concat(quantidade);                   // actualiza a quantidade no vetor de pedidos pendentes

                                                    System.out.println("atualizacao do vetor de pedidos pendentes: " + this.vetor_pedidos_pendentes[i]);

                                                    insere_vetor_pedidos_execucao(i, celula);   //adiciona que uma peca está a ser executada
                                                    
                                                    //-------------------------- PARTE FEITA NO ESCOLHA CAMINHO ---------------------------------------------
                                                    int peca_original = 0;
                                                    peca_trans_1 = 0;
                                                    peca_trans_2 = 0;
                                                    peca_trans_3 = 0;
                                                    peca_trans_4 = 0;
                                                    peca_trans_5 = 0;
                                                    
                                                    ModBus.writePLC(2,celula);           //Envia para o PLC celula
                                                    ModBus.writePLC(9,peca_original);    //Envia peca original
                                                    ModBus.writePLC(3,peca_trans_1);     //Envia para o PLC pt1
                                                    ModBus.writePLC(4,peca_trans_2);     //Envia para o PLC pt2
                                                    ModBus.writePLC(5,peca_trans_3);     //Envia para o PLC pt3
                                                    ModBus.writePLC(6,peca_trans_4);     //Envia para o PLC pt4
                                                    ModBus.writePLC(7,peca_trans_5);     //Envia para o PLC pt5 
                                                    //---------------------------------------------------------------------------------------------------------
                                                    
                                                    escreve_PLC_montagem_descarga(peca_descarga);         //manda a peça de baixo
                                                    ModBus.writePLC(1, 0);              //mete a zero a variavel tirar peça porque se nao no PLC não funciona, devido à forma como a Maq.Est. está feita
                                                   
                                                    adiciona_peca_descarga(peca_descarga, local_descarga);  // aumenta no vetor do pusher correspondente, o tipo de peca que descarregou
                                                    
                                                    if( quant == 0)             //quer dizer que é a ultima peca (nao esquecer que em cima já foi retirado 1 à quantidade)
                                                    {
                                                        if(local_descarga == 1)
                                                        {
                                                            thread_espera_peca("U1",n_ordem, numero_serie);
                                                        }
                                                        
                                                        else if(local_descarga == 2)
                                                        {
                                                            thread_espera_peca("U2",n_ordem, numero_serie);
                                                        } 
                                                    }
                                                    
                                                }
                                                    
                                            }
                                        }
                    }
                    if (aux == 1)           //significa que colocou uma ordem em execução logo volta a tras para ver se pode voltar a meter mais recentes.
                    {
                        break;  //sai fora do ciclo for para fazer reset
                    }
                }
            }
        }
    }
}
