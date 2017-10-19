//package calculator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

class Disponivel{
	ArrayList<Integer> disp=new ArrayList<Integer>();
	int threads_respondidas=0;
	public Disponivel(int i) {
		for (int j = 0; j < i; j++) {
			disp.add(1);
		}
	}
	int get_disp() {
		for (int i = 0; i < disp.size(); i++) {
			if (disp.get(i)==1) {
				System.out.println("Thread disponivel: "+i);
				return i;
			}

		}
		return -1;
	}
	void verifica(int n_th,String x) {
		if(x.equals("Type|empty;disp|1;")) {
			disp.set(n_th, 1);
		}
		else {
			disp.set(n_th, 0);
		}
		threads_respondidas=threads_respondidas+1;
	}
	int get_threads_respondidas() {
		return threads_respondidas;
	}
	void restart_threads_respondidas() {
		threads_respondidas=0;
	}
}

public class tcp_server {

	/**
	 * @param args
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws DivByZeroException 
	 */

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, DivByZeroException {
		//---------------------
		//Conecção ao servidor rmi
		rmi_interface rmi = (rmi_interface) Naming.lookup("rmi");

		//buscar as listas
		//ArrayList<String> listas =rmi.print_listas();
		//boolean check=rmi.verifica_pass(111111111, "111");
		//System.out.println(check);

		int n_term=2;
		int n_term_origem=2;
		Disponivel disp=new Disponivel(n_term);
		ArrayList<Connection> todos=new ArrayList<Connection>();
		int tipo=0;
		int numero=0;
		int id=0;
		ArrayList<String> votacoes_hoje = new ArrayList<String>();

		try{
			int serverPort = 6000;
			System.out.println("A Escuta no Porto 6000");
			ServerSocket listenSocket = new ServerSocket(serverPort);
			System.out.println("LISTEN SOCKET="+listenSocket);
			Scanner keyboardScanner = new Scanner(System.in);
			
			//DEFENIR TIPO E ID DA VOTACAO
			votacoes_hoje=rmi.get_id_tipo_eleicoes_De_hoje();
			//Quando existem mais de que uma votacao em um dia.
			if(votacoes_hoje.size()==4) {
				System.out.println("Mesa de voto para:\n1-Nucleo de estudantes\n2-Concelho Geral");
				tipo=keyboardScanner.nextInt();
				
				if(tipo==2) {
					if(Integer.parseInt(votacoes_hoje.get(1))==1) {
						tipo=Integer.parseInt(votacoes_hoje.get(3));
						id=Integer.parseInt(votacoes_hoje.get(2));
					}else {
						tipo=Integer.parseInt(votacoes_hoje.get(1));
						id=Integer.parseInt(votacoes_hoje.get(0));
					}
				}
				else if(tipo==1) {
					if(Integer.parseInt(votacoes_hoje.get(1))==1) {
						id=Integer.parseInt(votacoes_hoje.get(0));
					}else {
						id=Integer.parseInt(votacoes_hoje.get(2));
					}
				}
			}else{//Quando so há uma votação nesse dia
				tipo=Integer.parseInt(votacoes_hoje.get(1));
				id=Integer.parseInt(votacoes_hoje.get(0));
			}
			System.out.println("TIPO=="+tipo+"|---|ID=="+id);
			
			//ESCOLHER OS LOCAIS
			ArrayList <String> mesas_voto=rmi.retorna_mesas_voto(id);
			for(int i=0;i<mesas_voto.size();i++) {
				System.out.println(mesas_voto.get(i));
			}
			System.out.println("Escolha o departamento: ");
			String dep=keyboardScanner.nextLine();
			dep=keyboardScanner.nextLine();
			int id_loc=rmi.retorna_id_mesa_voto_escolhida(id,dep);
			System.out.println("ID_LOCAL_MESA_DE_VOTO=="+id_loc);

			
			while(true) {
				Socket clientSocket = listenSocket.accept(); // BLOQUEANTE               
				System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
				numero ++;
				n_term--;
				
				Connection nova=new Connection(clientSocket, numero,n_term,n_term_origem,todos,disp,tipo,id);
				//cada vez que faz accept adiciona á lista o socket criado ou da connection
				synchronized(todos) {
					todos.add(nova);
				}
			}
		}catch(IOException e)
		{System.out.println("Listen:" + e.getMessage());}


	}
}

class Connection extends Thread {
	DataInputStream in;
	PrintWriter out;
	Socket clientSocket;
	int thread_number;
	ArrayList<Connection> todos_cli;
	int n_term;
	Disponivel disp;
	int tipo_elei=0;
	int id_eleicao;
	int num_term_origem;

	//, departamento, numero de eleicao
	public Connection (Socket aClientSocket, int numero,int n_terminais,int n_term_or ,ArrayList<Connection> todos,Disponivel dispo, int tipo,int id) {
		thread_number = numero;
		n_term=n_terminais;
		todos_cli=todos;
		disp=dispo;
		tipo_elei=tipo;
		id_eleicao=id;
		num_term_origem=n_term_or;

		try{
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			this.start();
		}catch(IOException e){System.out.println("Connection:" + e.getMessage());}
	}
	//=============================
	public void run(){
		int resposta;
		String temp;
		rmi_interface rmi;
		String tipo_cc;

		try {
			rmi = (rmi_interface) Naming.lookup("rmi");

			Scanner keyboardScanner = new Scanner(System.in);

			//thread para leitura do cliente
			new Thread() {
				public void run() {
					Scanner keyboardScanner = new Scanner(System.in);
					while(true) {
						String data;
						try {
							data = in.readLine();
							System.out.println("T["+thread_number + "] Recebeu: "+data);
							if(data.split(";")[0].equals("Type|login")) {
								boolean check=rmi.verifica_pass(data.split(";")[1].split("\\|")[1],data.split(";")[2].split("\\|")[1] );
								if(check) {
									out.println(rmi.print_listas());
									//enviar o local de voto do eleitor
									//thread number é o nome do local
									rmi.local_de_voto("Type|eleitor;"+data.split(";")[1]+";local|"+thread_number+";id|1");

								}

								else
									System.out.println("Erro no login");				
							}
							else if(data.split(";")[0].equals("Type|voto")) {
								rmi.vota(data);					
							}
							else if(data.split(";")[0].equals("Type|empty")){
								synchronized (disp) {
									//System.out.println("antes verifica"+threads_respondidas);
									disp.verifica(thread_number-1, data);

									//System.out.println("depois verifica"+threads_respondidas);
								}

							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}.start();
			String data;
			//escrever no cliente
			if (n_term==0) {
				while(true){
					//an echo server

					System.out.println("Numero de Indentificação do eleitor:	");
					resposta = keyboardScanner.nextInt();
					
					tipo_cc=rmi.verifica_cc_existe_retorna_tipo(Integer.toString(resposta));		
					
					while (tipo_cc==null) {
						System.out.println("CC não existe, digite um cc valido:");
						resposta = keyboardScanner.nextInt();
						tipo_cc=rmi.verifica_cc_existe_retorna_tipo(Integer.toString(resposta));	
					}
					System.out.println(tipo_elei+tipo_cc);
					
					while( tipo_elei==1 && !tipo_cc.equals("estudante")) {
						System.out.println(tipo_elei);
						System.out.println(tipo_cc);
						System.out.println("Pessoa nao autorizada, introduza um novo cc:");
						resposta = keyboardScanner.nextInt();
						tipo_cc=rmi.verifica_cc_existe_retorna_tipo(Integer.toString(resposta));	
					}
					
					
					

					//envia o numero de cc e o id da eleiçao("#1')
					try {
						if(!rmi.verifica_se_votou(resposta+"#1")) {
							synchronized (this) {
								System.out.println("size: "+todos_cli.size());
								for (int i = 0; i < todos_cli.size(); i++) {
									todos_cli.get(i).out.println("Type|empty;");
									System.out.println("ENVIA EMPTY PARA-->THREAD"+i);
								}
							}
					
							
							while(disp.get_threads_respondidas()!=num_term_origem) {
							}
							
							System.out.println("Antes resetar: " + disp.get_threads_respondidas());
							disp.restart_threads_respondidas();
							//System.out.println(disp.get_threads_respondidas());


							//sleep(2000);
							synchronized (this) {
								try {
									System.out.println("THREAD DISPONIVEL:" + disp.get_disp());
									todos_cli.get(disp.get_disp()).out.println("Type|request;cc|"+resposta+";");
								}catch(ArrayIndexOutOfBoundsException e) {
									System.out.println("Todos os terminais ocupados");
								}
								
							}
						}
						else {
							System.out.println("ja votou");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					//Thread.sleep(1000);
				}

			}
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
