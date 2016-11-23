
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
        this.disponibilidade = 0;
    }
    
    public int DisponibilidadeCelula (){
        
        int resultado;
        
        resultado = this.disponibilidade;
        
        return resultado;
    }
    
    public void TornarDisponivel(){
        
        if(this.disponibilidade == 0){
            this.disponibilidade = 1;
        }
    }
    
    public void TornarIndisponivel(){
        
        if(this.disponibilidade == 1){
            this.disponibilidade = 0;
        }
    }
    
    //Gonçalo e Emanuel mandam variável que indica a disponibilidade 1 ou 0
    public void AtualizarCelula (){
        int disp = 0;                                   
        if(id_celula == 1){
            //disp = ModBus.readPLC(ref,count);
        }
        else if(id_celula == 2){
            //disp = ModBus.readPLC(ref,count);
        }
        else if(id_celula == 3){
            //disp = ModBus.readPLC(ref,count);
        }
        else if(id_celula == 4){
           //disp = ModBus.readPLC(ref,count);
        }
        else if(id_celula == 5){
           //disp = ModBus.readPLC(ref,count);
        } 
        else if(id_celula == 6){
           //disp = ModBus.readPLC(ref,count);
        } 
        else if(id_celula == 7){
           //disp = ModBus.readPLC(ref,count);
        } 
        
        return;
    }
    
    
}



