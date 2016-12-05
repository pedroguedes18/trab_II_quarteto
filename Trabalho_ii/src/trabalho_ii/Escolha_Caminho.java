package trabalho_ii;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Escolha_Caminho implements Runnable {
    
    //Na classe Gestor_Produção, dentro da função Executa_Pedido_Pendente,
    //para cada caso (T,M ou D), invoca-se a função Caminho Associado com 
    //os parâmetros necessários e dentro da função Caminho Associado tem de 
    //se invocar uma função Disponibilidade da Célula
    
    private static Escolha_Caminho instance;                                    // instância da classe Escolha_Caminho
    private int auxiliar;
    private int auxiliarM;
    private int auxiliarD;
    
    public Escolha_Caminho (){             //método construct
        this.auxiliar = 1;
        this.auxiliarM = 1;
        this.auxiliarD = 1;
    }
                                                
    public static Escolha_Caminho getInstance()                                 // método para criar se ainda não foi criado uma instancia de Escolha_Caminho
    {       
	if(instance==null)
        {
            instance=new Escolha_Caminho();
	}
        
	return instance;
    }
    
    
    ModBus modbus = ModBus.getInstance();   //instância do modbus para usar as suas funções
    
    Celula celula_1 = new Celula(1); //Célula Paralelo 1
    Celula celula_2 = new Celula(2); //Célula Série 1
    Celula celula_3 = new Celula(3); //Célula Paralelo 2
    Celula celula_4 = new Celula(4); //Célula Série 2
    Celula celula_5 = new Celula(5); //Célula Montagem
    Celula celula_6 = new Celula(6); //Local de Descarga 1
    Celula celula_7 = new Celula(7); //Local de Descarga 2
    
    //TESTAR
    
    //VARIÁVEL AUXILIAR FAZ COM QUE A DISPONIBILIDADE DUMA CÉLULA NÃO SEJA
    //DECREMENTADA ATÉ PRONTO_ENVIAR_O PASSAR A TER VALOR 0
    //(EVITA INCREMENTO->DECREMENTO->INCREMENTO) DURANTE O PERÍODO EM QUE 
    //PRONTO_ENVIAR_O = 1 (VARIÁVEL DO PLC)
    
    //VARIÁVEL ULTIMA_PEÇA FAZ COM QUE A ULTIMA PEÇA TRANSFORMADA DUM PEDIDO
    //SEJA ENCAMINHADA PARA A CELULA MAIS À DIREITA DE MODO A QUE NÃO EXISTA
    //CONFLITO COM A HORA DE FIM DO PEDIDO
    
    //Associar células à transformação
    
    public int Associar_Celulas_Transformaçao (int peça_origem, int peça_final, int ultima_peça){
        
        int i=0;
        int d1=0, d2=0, d3=0, d4=0;
        int pt1, pt2, pt3, pt4, pt5;
        
        if (peça_origem == 1){
            
            switch (peça_final){
                //P1-P2
                case 2: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        
                        if((d3 == 1) && (this.auxiliar == 1)){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,2);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;
                        
                
                //(P1-P3) OU (P1-P2-P3)
                //POR UMA QUESTÃO DE OTIMIZAÇÃO DÁ-SE PRIORIDADE ÀS CÉLULAS
                //COM MENOS TRANSFORMAÇÕES        
                case 3: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,2);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5
                        }
                        
                        break;
                        
                //(P1-P3-P4) OU (P1-P2-P3-P4)      
                //POR UMA QUESTÃO DE OTIMIZAÇÃO DÁ-SE PRIORIDADE ÀS CÉLULAS
                //COM MENOS TRANSFORMAÇÕES         
                case 4: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,2);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5
                        }
                        
                        break;  
                
                //P1-P3-P4-P5
                case 5: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,5);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break; 
                
                //P1-P2-P3-P4-P6
                case 6: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,2);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,6);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;        
                        
                //(P1-P3-P4-P5-P7) OU (P1-P2-P3-P4-P6-P7)
                //POR UMA QUESTÃO DE OTIMIZAÇÃO DÁ-SE PRIORIDADE ÀS CÉLULAS
                //COM MENOS TRANSFORMAÇÕES         
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,5);     //Envia para o PLC pt3
                            modbus.writePLC(6,7);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,2);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,6);     //Envia para o PLC pt4
                            modbus.writePLC(7,7);     //Envia para o PLC pt5
                        }
                        
                        break;        
            }            
        }
        
        else if (peça_origem == 2){
            
            switch (peça_final){
                //P2-P1
                case 1: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,1);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;
                        
                //(P2-P1-P3) OU (P2-P3)        
                case 3: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else if(d4 == 1 && this.auxiliar == 1 && ultima_peça > 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,1);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5 
                        }
                        
                        break;
                        
                //(P2-P1-P3-P4) OU (P2-P3-P4)
                case 4: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else if(d4 == 1 && this.auxiliar == 1 && ultima_peça > 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,1);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5 
                        }
                        
                        break; 
                
                //P2-P1-P3-P4-P5
                case 5: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,1);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,5);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break; 
                
                //P2-P3-P4-P6
                case 6: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,6);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;        
                        
                //(P2-P1-P3-P4-P5-P7) OU (P2-P3-P4-P6-P7)
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else if(d4 == 1 && this.auxiliar == 1 && ultima_peça > 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,3);     //Envia para o PLC pt1
                            modbus.writePLC(4,4);     //Envia para o PLC pt2
                            modbus.writePLC(5,6);     //Envia para o PLC pt3
                            modbus.writePLC(6,7);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,1);     //Envia para o PLC pt1
                            modbus.writePLC(4,3);     //Envia para o PLC pt2
                            modbus.writePLC(5,4);     //Envia para o PLC pt3
                            modbus.writePLC(6,5);     //Envia para o PLC pt4
                            modbus.writePLC(7,7);     //Envia para o PLC pt5 
                        }
                        
                        break;        
            }
        }
        else if (peça_origem == 3){
            
            switch (peça_final){
                //P3-P4
                case 4: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,4);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                       
                        break;
                        
                //P3-P4-P5
                case 5: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,4);     //Envia para o PLC pt1
                            modbus.writePLC(4,5);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;  
                
                //P3-P4-P6
                case 6: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,4);     //Envia para o PLC pt1
                            modbus.writePLC(4,6);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;        
                        
                //(P3-P4-P5-P7) OU (P3-P4-P6-P7)
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,4);     //Envia para o PLC pt1
                            modbus.writePLC(4,5);     //Envia para o PLC pt2
                            modbus.writePLC(5,7);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,4);     //Envia para o PLC pt1
                            modbus.writePLC(4,6);     //Envia para o PLC pt2
                            modbus.writePLC(5,7);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5 
                        }
                        
                        break;         
            }        
            
        }
        
        else if (peça_origem == 4){
            
            switch (peça_final){
                //P4-P5
                case 5: d2=celula_2.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,5);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;
                
                //P4-P6
                case 6: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,6);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;        
                        
                //(P4-P5-P7) OU (P4-P6-P7)
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i == 2 || i == 4){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,5);     //Envia para o PLC pt1
                            modbus.writePLC(4,7);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        else if (i == 1 || i == 3){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,6);     //Envia para o PLC pt1
                            modbus.writePLC(4,7);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5 
                        }
                        
                        break;
            }        
        }
        
        else if (peça_origem == 5){
            
            switch(peça_final){
                //P5-P7
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d2=celula_2.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        d4=celula_4.DisponibilidadeCelula();
                        
                        if(d4 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=4;
                            celula_4.DecrementarDisponibilidade();
                        }
                        else if(d3 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d2 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=2;
                            celula_2.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,7);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break;
            }
        }
        
        else if (peça_origem == 6){
           
            switch(peça_final){
                //P6-P7
                case 7: d1=celula_1.DisponibilidadeCelula();
                        d3=celula_3.DisponibilidadeCelula();
                        
                        if(d3 == 1 && this.auxiliar == 1){                                    //Assume-se numero máximo de 2 peças numa célula 
                            i=3;
                            celula_3.DecrementarDisponibilidade();
                        }
                        else if(d1 == 1 && this.auxiliar == 1 && ultima_peça > 1){
                            i=1;
                            celula_1.DecrementarDisponibilidade();
                        }
                        else i=0;
                        
                        if (i > 0){
                            modbus.writePLC(2,i);     //Envia para o PLC celula
                            modbus.writePLC(9,peça_origem);     //Envia peca original
                            modbus.writePLC(3,7);     //Envia para o PLC pt1
                            modbus.writePLC(4,0);     //Envia para o PLC pt2
                            modbus.writePLC(5,0);     //Envia para o PLC pt3
                            modbus.writePLC(6,0);     //Envia para o PLC pt4
                            modbus.writePLC(7,0);     //Envia para o PLC pt5          
                        }
                        
                        break; 
            }
        }
        
        
        //RETORNA AGORA NÚMERO DA CÉLULA OU NULO SE NÃO TIVER NENHUMA LIVRE
        
        return i;
    }
    
    
    public int Associar_Celulas_Montagem (){
        int i = 0;
        int d5 = 0;
        
        d5 = celula_5.DisponibilidadeCelula();
        System.out.flush();
        
        if(d5 == 1 && this.auxiliarM == 1){
            i = 5;
            celula_5.DecrementarDisponibilidade();
        }
        else
            i=0;
        
        if (i == 5){
            modbus.writePLC(2,i);     //Envia para o PLC celula
        }
        
        return i;
    }
    
    
    public int Associar_Celulas_Descarga (int local_descarga){
        int i = 0;
        int d6 = 0;
        int d7 = 0;
        
        if(local_descarga == 1){
            d6 = celula_6.DisponibilidadeCelula();
            
            if(d6 == 1 && this.auxiliarD == 1){
                i = 6;
                celula_6.DecrementarDisponibilidade();
            }
            else 
                i = 0;
        }
        else if(local_descarga == 2){
            d7 = celula_7.DisponibilidadeCelula();
            
            if(d7 == 1 && this.auxiliarD == 1){
                i = 7;
                celula_7.DecrementarDisponibilidade();
            }
            else
                i = 0;
        }
        
        if (i == 6){
            modbus.writePLC(2,i);
        }
        else if (i == 7){
            modbus.writePLC(2,i);
        }
        
        return i;
    }

    @Override
    public void run() {
        while(true)
        {
            try {       
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escolha_Caminho.class.getName()).log(Level.SEVERE, null, ex);
            }
                    System.out.flush();
                   
                    //synchronized (this)
                    //{
                        if(ModBus.readPLC(5,0) == 1){                   //Célula Série 1
                        this.auxiliar = 0;
                        System.out.println("Célula 2: Vou incrementar agora");
                        while(modbus.readPLC(5,0) == 1){
                            celula_2.IncrementarDisponibilidade();
                        }
                        this.auxiliar = 1;
                        System.out.println("Célula 2: Já incrementei, vou sair do IF \n");
                        }
                    //}
            
                    //synchronized (this)
                    //{
                        if(modbus.readPLC(6,0) == 1){                   //Célula Série 2
                        this.auxiliar = 0;
                        System.out.println("Célula 4: Vou incrementar agora");
                        while(modbus.readPLC(6,0) == 1){
                            celula_4.IncrementarDisponibilidade();
                        }
                        this.auxiliar = 1;
                        System.out.println("Célula 4: Já incrementei, vou sair do IF \n");
                        }
                    //}
                    
                    //synchronized (this)
                    //{
                    if(modbus.readPLC(3,0) == 1){                   //Célula Paralelo 1
                        this.auxiliar = 0;
                        System.out.println("Célula 1: Vou incrementar agora");
                        while(modbus.readPLC(3,0) == 1){
                            celula_1.IncrementarDisponibilidade();
                        }
                        this.auxiliar = 1;
                        System.out.println("Célula 1: Já incrementei, vou sair do IF \n");
                    }
                    //}
                    
                    //synchronizes (this)
                    //{
                    if(modbus.readPLC(4,0) == 1){                   //Célula Paralelo 2
                        this.auxiliar = 0;
                        System.out.println("Célula 3: Vou incrementar agora");
                        while(modbus.readPLC(4,0) == 1){
                            celula_3.IncrementarDisponibilidade();
                        }
                        this.auxiliar = 1;
                        System.out.println("Célula 3: Já incrementei, vou sair do IF \n");
                    }
                    //}
                    
                    //synchronized (this)
                    //{
                        if(ModBus.readPLC(10,0) == 1){                   //Célula Montagem
                        this.auxiliarM = 0;
                        System.out.println("Célula 5: Vou incrementar agora");
                        while(modbus.readPLC(10,0) == 1){
                            celula_5.IncrementarDisponibilidade();
                        }
                        this.auxiliarM = 1;
                        System.out.println("Célula 5: Já incrementei, vou sair do IF \n");
                        }
                    //}
                    
                    //synchronized (this)
                    //{
                        if(ModBus.readPLC(12,0) == 1){                   //Célula Descarga 1
                        this.auxiliarD = 0;
                        System.out.println("Célula 6: Vou incrementar agora");
                        while(modbus.readPLC(12,0) == 1){
                            celula_6.IncrementarDisponibilidade();
                        }
                        this.auxiliarD = 1;
                        System.out.println("Célula 6: Já incrementei, vou sair do IF \n");
                        }
                    //}
                    
                    //synchronized (this)
                    //{
                        if(ModBus.readPLC(14,0) == 1){                   //Célula Descarga 2
                        this.auxiliarD = 0;
                        System.out.println("Célula 7: Vou incrementar agora");
                        while(modbus.readPLC(14,0) == 1){
                            celula_7.IncrementarDisponibilidade();
                        }
                        this.auxiliarD = 1;
                        System.out.println("Célula 7: Já incrementei, vou sair do IF \n");
                        }
                    //}
        }
    }
}