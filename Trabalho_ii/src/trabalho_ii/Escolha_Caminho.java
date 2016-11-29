package trabalho_ii;

public class Escolha_Caminho {
    
    //Na classe Gestor_Produção, dentro da função Executa_Pedido_Pendente,
    //para cada caso (T,M ou D), invoca-se a função Caminho Associado com 
    //os parâmetros necessários e dentro da função Caminho Associado tem de 
    //se invocar uma função Disponibilidade da Célula
    
    private static Escolha_Caminho instance;                                    // instância da classe Escolha_Caminho
    
                                                
    public static Escolha_Caminho getInstance()                                 // método para criar se ainda não foi criado uma instancia de Escolha_Caminho
    {       
	if(instance==null)
        {
            instance=new Escolha_Caminho();	
	}
        
	return instance;
    }
    
    
    ModBus modbus = ModBus.getInstance();   //instância do modbus para usar as suas funções
    
    Celula celula_2 = new Celula(2);
    Celula celula_4 = new Celula(4);
    
    
    //Corre em ciclo infinito e atualiza disponibilidade das células
    //AVISO
    //SE O CICLO CORRER MAIS RÁPIDO OU MAIS LENTO DO QUE A PEÇA É ENVIADA PARA 
    //O TAPETE DE BAIXO, PODE INCREMENTAR A DISPONIBILIDADE MAIS DO QUE UMA VEZ
    //OU NÃO DETETAR SAÍDA DE PEÇA DA CÉLULA, RESPETIVAMENTE
    /*
    public void AtualizarCelula (){
        
        while(true){
            
            if(modbus.readPLC(1,0) == 1){                   //Célula Série 1
                celula_2.IncrementarDisponibilidade();
            }
            
            if(modbus.readPLC(3,0) == 1){                   //Célula Série 2
                celula_4.IncrementarDisponibilidade();
            }
            
        }
    
    } 
    */
    
    public void AtualizarCelula(){
        
        new Thread(){
            
            @Override
            public void run(){
               
               while(true){
                   
                    System.out.flush();
                   
                    if(modbus.readPLC(5,0) == 1){                   //Célula Série 1
                        celula_2.IncrementarDisponibilidade();
                        //System.out.println("Incrementei a disponibilidade da célula 2");
                    }
            
                    if(modbus.readPLC(6,0) == 1){                   //Célula Série 2
                        celula_4.IncrementarDisponibilidade();
                        //System.out.println("Incrementei a disponibilidade da célula 4");
                    }
            
                } 
            }
        }.start();
    }
    
    
    //Associar células à transformação
    
    public int Associar_Celulas_Transformaçao (int peça_origem, int peça_final){
        
        int i=0;
        int d2=0, d4=0;
        int pt1, pt2, pt3, pt4, pt5;
        
        if (peça_origem == 4){
            switch (peça_final){
                //P4-P5-P7
                case 7: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 || d4 == 2){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            System.out.println("Entrei no IF4");
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 || d2 == 2){
                            i=2;
                            System.out.println("Entrei no IF2");
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,5);     //Envia para o PLC pt1
                            modbus.writePLC(4,7);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;
            }
        }
        
        //VERIFICAR DISPONIBILIDADE DO TAPETE INICIAL
        //COLOCAR TAPETE INDISPONIVEL ANTES DE RETORNAR A CELULA

        System.out.println("Numero de peças na celula: " + celula_4.DisponibilidadeCelula());
        
        return i;
    }
}
    //ENVIA-SE CELULA JÁ AQUI OU NO GESTOR DE PRODUÇÃO? 
    
 /*   
    
    //Para o caso de uma Transformação
    
    public int Caminho_Associado_Transformaçao (int peça_origem, int peça_final){
        int caminho=0;
        
        if (peça_origem == 2){
            switch (peça_final){
                //MAQUINA A-B
                case 1: caminho = 1;
                        //Verifica disponibilidade da célula da direita e, em
                        //caso negativo verifica célula da esquerda
                        //Só atualiza valor do caminho se houver disponibilidade
                        //Caminho 
                    break;
                case 3: 
                        //Caminho  - (Tem caminho alternativo em B-C)
                    break;
                case 4: //Caminho  - (Tem caminho alternativo em B-C)
                    break;
                case 5: //Caminho  - (Tem caminho alternativo em B-C)
                    break;
                case 7: //Caminho
                    break;    
                //MAQUINA B-C    
                case 6: //Caminho
            }      
        }
         
        else if (peça_origem == 1){
            switch (peça_final){
                //MAQUINA A-B
                case 3: caminho = 2;
                        //Tem caminho alternativo em B-C
                    break;
                case 4: //Caminho  - (Tem caminho alternativo em B-C)
                    break;
                case 5: //Caminho
                    break;
                case 7: //Caminho  - (Tem caminho alternativo em B-C)
                    break; 
                //MAQUINA B-C  
                case 2: caminho = 4;
                        //Caminho
                    break;
                case 6: //Caminho    
                    break;
            }      
        }
        
        else if (peça_origem == 4){
            switch (peça_final){
                //MAQUINA A-B
                case 5: caminho = 3;
                    break;
                case 7: //Caminho  - (Tem caminho alternativo em B-C)
                    break;  
                //MAQUINA C
                case 6: //Caminho    
            }      
        }
        
        else if ((peça_origem == 3) & (peça_final == 4)){
           //MAQUINA B -PARALELO 1
           caminho = 5;
           //MAQUINA B -SERIE 1
           caminho = 11;
           //MAQUINA B -PARALELO 2
           
           //MAQUINA B -SERIE 2
           
        }
        
        else if ((peça_origem == 5) & (peça_final == 7)){
           //MAQUINA B
           caminho = 6;
        }
        
        else if ((peça_origem == 6) & (peça_final == 6)){
            //MAQUINA C
            //Caminho
        }
    
        
        return caminho;
        //Se o caminho retornado for 0 significa que não há disponibilidade 
        //para o pedido atual
    }
    
    //Para o caso de uma Montagem
    
    public int Caminho_Associado_Montagem (){
        int caminho = 0;
        //Ver disponibilidade e em caso afirmativo retorna Caminho
        return caminho;
    }
    
    //Para o caso de uma Descarga
    
    public int Caminho_Associado_Descarga (int destino){
        int caminho = 0;
        
        if(destino == 1){
            //Ver disponibilidade da célula
            //caminho
        }
        else if(destino == 1){
            //Ver disponibilidade da célula
            //caminho
        }
        
        return caminho;
    }
}
*/