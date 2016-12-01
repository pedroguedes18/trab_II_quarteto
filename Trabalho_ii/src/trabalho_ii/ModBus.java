
package trabalho_ii;

import java.net.InetAddress;
//import java.net.UnknownHostException;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.SimpleRegister;


public class ModBus {
    private static ModBus instance;
	private ModBus(){}
	
	public static ModBus getInstance(){
		if(instance==null){
			instance=new ModBus();	
		}
		return instance;
	}
	//Singleton
	
	
	//função para Ler PLC. 
	//ref-numero do primeiro registo a ler
	//count-numero de registos a ler
	
	
	public synchronized static int readPLC(int ref,int count) {
            //System.out.println("readPLC(): entrou no modbus\n");
		try{
		TCPMasterConnection con = null; //the connection
		ModbusTCPTransaction trans = null; //the transaction
		ReadInputRegistersRequest req = null; //the request
		ReadInputRegistersResponse res = null; //the response
		
		/* Variables for storing the parameters */
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		int port = 505;
	
		//int repeat = 1; //a loop for repeating the transaction
		
		con = new TCPMasterConnection(addr);
		//con.setTimeout(10000);//set Timeout de 10 segundos
		con.setPort(port);
		con.connect();
		
		//LER DO PLC
		//3. Prepare the request
		req = new ReadInputRegistersRequest(ref,count+1);

		//4. Prepare the transaction
		trans = new ModbusTCPTransaction(con);
		trans.setRequest(req);
		
		int a=0;
		trans.execute();
		res = (ReadInputRegistersResponse) trans.getResponse();
		a = res.getRegisterValue(count);
		//System.out.println("Digital Inputs Status=" +Integer.toBinaryString((a & 0xFF) + 0x100).substring(1));
		con.close();
                //System.out.println("writePLC(): saiu no modbus\n");
		return a;//retorna inteiro
		}
		catch(Exception e) {
			System.out.println("Erro de comunicação");
			return 0;
		}
		
	}
	
	//função para Escrever no PLC. 
	//ref-numero do registo a escrever
	//w-Word a escrever no PLC
	public synchronized static void writePLC(int ref,int w){
            //System.out.println("writePLC(): entrou no modbus\n");
		try{
                    //System.out.println("writePLC(): entrou no try\n");
		TCPMasterConnection con = null; //the connection
			
		/* Variables for storing the parameters */
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		int port = 505;
                //System.out.println("writePLC(): antes de estabelecer conexao\n");
		con = new TCPMasterConnection(addr);
		//con.setTimeout(10000);//set Timeout de 10 segundos
		con.setPort(port);
		con.connect();
		
                //System.out.println("writePLC(): estabeleceu conexao\n");
                
		//ESCREVER NO PLC
		SimpleRegister regis = new SimpleRegister(w);//prepara registo a enviar		
		WriteSingleRegisterRequest req2 = new WriteSingleRegisterRequest(ref,regis);//cria pedido
		ModbusTCPTransaction trans2 = new ModbusTCPTransaction(con);//cria transação
		trans2.setRequest(req2);//faz pedido
                //System.out.println("writePLC(): vai executar\n");
		trans2.execute();//executa transação
                //System.out.println("writePLC(): já executou\n");
		con.close();
		//WriteSingleRegisterResponse res2=(WriteSingleRegisterResponse) trans2.getResponse();//obtem resposta
		//int c=res2.getRegisterValue();
		//int d=res2.getReference();
		//System.out.println("escrito no registo" +d +c);
		}
		catch(Exception e) {
			System.out.println("Erro de comunicação\n");
		}
	}
}
