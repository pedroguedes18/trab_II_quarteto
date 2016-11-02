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
    
    public int Caminho_Associado (int peça_origem, int peça_final){
        if (peça_origem == 2){
            switch (peça_final){
                case 1: //Caminho 
                    break;
                case 3: //Caminho
                    break;
                case 4: //Caminho
                    break;
                case 5: //Caminho
                    break;
                case 7: //Caminho
                    break;       
            }      
        }
    }
}
