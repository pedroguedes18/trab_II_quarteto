
package trabalho_ii;

public class Celula {
    private int disponibilidade;
    private final int id_celula;
    
    private static Celula instance;                                    // instância da classe Escolha_Caminho
                                                
    public static Celula getInstance()                                 // método para criar se ainda não foi criado uma instancia de Escolha_Caminho
    {       
	return instance;
    }
    
    
    ModBus modbus = ModBus.getInstance();   //instância do modbus para usar as suas funções
    
    
    public Celula (int num_celula){             //método construct
        this.id_celula = num_celula;
        this.disponibilidade = 2;
    }
    
    public int DisponibilidadeCelula (){
        
        int resultado;
        
        resultado = this.disponibilidade;
        
        return resultado;
    }
    
    public void TornarDisponivel(){
        
        if(this.disponibilidade < 2){
            this.disponibilidade = 2;
        }
    }
    
    public void TornarIndisponivel(){
        
        if(this.disponibilidade > 0){
            this.disponibilidade = 0;
        }
    }
    
    public void DecrementarDisponibilidade(){
        if((this.disponibilidade == 2) || (this.disponibilidade == 1)){
            this.disponibilidade -- ;
        }
        else{
            System.out.println("Erro a decrementar disponibilidade");
        }
    }
    
     public void IncrementarDisponibilidade(){
        if((this.disponibilidade == 0) || (this.disponibilidade == 1)){
            this.disponibilidade ++ ;
            System.out.println("Incrementei a disponibilidade");
        }
        else{
            System.out.println(this.disponibilidade);
            System.out.println("Erro a incrementar disponibilidade");
        }
    }
 
}



