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
