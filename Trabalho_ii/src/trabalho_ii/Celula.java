
package trabalho_ii;

public class Celula {
    private boolean disponibilidade;
    private int id_celula;
    
    private static Celula instance;                                    // instância da classe Escolha_Caminho
                                                
    public static Celula getInstance()                                 // método para criar se ainda não foi criado uma instancia de Escolha_Caminho
    {       
	if(instance==null)
        {
            instance=new Celula();	
	}
        
	return instance;
    }
    
    //NO PROGRAMA PRINCIPAL
    //Celula paralelo1 = new Celula()
    //Celula serie1 = new Celula()
    //Celula paralelo2 = new Celula()
    //Celula serie2 = new Celula()
    //Celula montagem = new Celula ()
    //Celula descarga_PM1 = new Celula()
    //Celula descarga_PM2 = new Celula()
    
    //paralelo1.Disponibilidade_Celula() 
    
    public boolean DisponibilidadeCelula (){
        
        boolean resultado;
        
        resultado = this.disponibilidade;
        
        return resultado;
    }
    
    public void TornarDisponivel(){
        
        if(this.disponibilidade == false){
            this.disponibilidade = true;
        }
    }
    
    public void TornarIndisponivel(){
        
        if(this.disponibilidade == true){
            this.disponibilidade = false;
        }
    }
    
    
}

