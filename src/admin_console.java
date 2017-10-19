//package calculator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;

public class admin_console {

	/**
	 * @param args
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws DivByZeroException 
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, DivByZeroException {
		rmi_interface admin_console = (rmi_interface) Naming.lookup("rmi");
		Scanner keyboardScanner = new Scanner(System.in);
		
		int i=0;
		try {
			do {
				i=print_menu();

				switch(i){
				case 1:
					String x=mini_menu_1(admin_console);
					//System.out.println(x);
					if (x!=null) 
						admin_console.add_pessoa(x);

					break;
				case 2:
					String l=mini_menu_2(admin_console);
					if (l!=null) 
						admin_console.add_dep_fac(l);
					
					break;
				case 3:
					ArrayList<String> k=mini_menu_3();
					//admin_console.add_votacao(k);
					break;
				case 4:
					ArrayList<String> n=mini_menu_4(admin_console.proxima_lista());
					//admin_console.add_lista(n);
					break;
				case 5:
					ArrayList<String> n2=mini_menu_5();
					//adicionar na bd
					//n2 é uma lista com [numero de eleicao, dep1, dep2, dep3,...]
					break;
				case 6:
					String b=mini_menu_6(admin_console);
					if(b!=null) {
						admin_console.set_votação(b);
					}
					break;

				case 7:
					mini_menu_7(admin_console);
					break;

				case 8:
					break;

				case 9:
					String z=mini_menu_9();
					break;

				case 10:

					String h=mini_menu_10();//cc#id
					System.out.println(admin_console.resultados(h));
					break;
				case 11:
					System.out.println("Terminando");
					break;

				default:
					System.out.println("Opção impossivel");
					break;
				}

			}while(i!=11);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static int print_menu() {
		Scanner keyboardScanner = new Scanner(System.in);
		int opcao=0;
		do {
			System.out.println("Selecione uma opção!");
			System.out.println("1->Registar pessoas");
			System.out.println("2->Gestão Faculdades e Departamentos");
			System.out.println("3->Criação de uma eleição");
			System.out.println("4->Registo de Candidaturas");
			System.out.println("5->Gestão de mesas de voto");
			System.out.println("6->Alteraçao a uma eleicao");
			System.out.println("7->Local de voto do eleitor");
			System.out.println("8->Informacao do estado das mesas de voto");
			System.out.println("9->Estatistica de abstencao");
			System.out.println("10->Histórico de eleicoes e resultados");
			System.out.println("11->Sair");
			System.out.println("Escolha a sua opção: ");
			opcao= keyboardScanner.nextInt();
				
		}while(opcao>11 || opcao<1);
		return opcao;
		
	}
	public static String mini_menu_1(rmi_interface admin_console) {
		Scanner keyboardScanner = new Scanner(System.in);
		int opcao=0;
		String info=null,temp;
		try {
			//nome
			System.out.println("Nome: ");
			info= keyboardScanner.nextLine();
			if(info.length()==0) {
				System.out.println("Numero de Identificação existente");
				System.out.println("Press enter to continue!");
				System.in.read();
				return null;
			}
			info=info+"#";
			//cc
			System.out.println("Numero de Indentificacao: ");
			temp= keyboardScanner.nextLine();

			if(admin_console.verifica_cc_existe(temp)) {
				System.out.println("Numero de Identificação existente");
				System.out.println("Press enter to continue!");
				System.in.read();
				return null;
			}

			info=info+ temp;
			info=info+"#";

			//validade
			System.out.println("Validade do documento(dd/mm/aaaa): ");
			temp=keyboardScanner.nextLine();
			
			DateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			Date date2 = formatter2.parse(temp);
			Date date = Calendar.getInstance().getTime();
			
			if(date.after(date2)) { 
				System.out.println("Identificação fora de Validade");
				System.out.println("Press enter to continue!");
				System.in.read();
				return null;
	        }
			
			info=info+ temp;
			info=info+"#";
			//password(poder repetir)
			System.out.println("DIgite uma password: ");
			temp=keyboardScanner.nextLine();
			if (temp.length()==0) {
				System.out.println("Numero de Identificação existente");
				System.out.println("Press enter to continue!");
				System.in.read();
				return null;
			}
			info=info+ temp;
			info=info+"#";
			//departamento
			System.out.println("Departamento: ");
			info=info+ keyboardScanner.nextLine();
			info=info+"#";
			//Faculdade
			System.out.println("Faculdade: ");
			info=info+ keyboardScanner.nextLine();
			info=info+"#";
			//contacto
			System.out.println("Contacto: ");
			temp=keyboardScanner.nextLine();
			info=info+ temp;
			info=info+"#";

			do {
				System.out.println("Funcao do utilizador!");
				System.out.println("1->Estudante");
				System.out.println("2->Docente");
				System.out.println("3->Funcionario");
				System.out.println("Escolha a sua opção: ");
				opcao= keyboardScanner.nextInt();
			}while(opcao>3 || opcao<0);

			switch(opcao) {
				case 1:
					info=info+"estudante";
					break;
				case 2:
					info=info+ "docente";
					break;
				case 3:
					info=info+ "funcionario";
					break;
				default:
					break;
			};
			return info;



		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParseException e) {
			System.out.println("Data mal introduzida");

			// TODO Auto-generated catch block
			
		}
		return info;
		

	}
	public static String mini_menu_2(rmi_interface admin_console) {
		Scanner keyboardScanner = new Scanner(System.in);
		int x;
		System.out.println("Selecione uma opção!");
		System.out.println("1->Inserir faculdade");
		System.out.println("2->Inserir Departamento");
		System.out.println("3->Alteração departamento");
		System.out.println("4->Remoçao de faculdade");
		System.out.println("5->Remoçao de departamento");
		System.out.println("6->Voltar ao menu principal");
		System.out.println("Escolha a sua opção: ");
		x= keyboardScanner.nextInt();
		String info= keyboardScanner.nextLine();
		String temp;
		try {
			switch(x) {
			case 1:
				System.out.println("Nome da nova faculdade:");
				temp=keyboardScanner.nextLine();

				if(admin_console.verifica_fac_existe(temp)) {
					System.out.println("Faculdade existente");
					System.out.println("Press enter to continue!");
					System.in.read();
					return null;
				}

				info= temp+"#";
				info= info + "NOT_FOUND";
				break;
			case 2:
				System.out.println("Nome da faculdade:");
				temp=keyboardScanner.nextLine();
				if(!admin_console.verifica_fac_existe(temp)) {
					System.out.println("Faculdade inexistente");
					System.out.println("Press enter to continue!");
					System.in.read();
					return null;
				}
				info= temp+"#";
				System.out.println("Nome do novo departamento:");
				temp=keyboardScanner.nextLine();
				if(admin_console.verifica_dep_existe(temp)) {
					System.out.println("Departamento existente");
					System.out.println("Press enter to continue!");
					System.in.read();
					return null;
				}
				
				info= info+temp;
				break;
			case 3:
				System.out.println("Disponivel em breve!");
				break;
			case 4:
				System.out.println("Disponivel em breve!");
				break;
			case 6:
				System.out.println("Disponivel em breve!");
				break;
			default:
				System.out.println("Erro na escolha!");
				break;
			}

			return info;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		
	}
	public static ArrayList<String> mini_menu_3() {
		Scanner keyboardScanner = new Scanner(System.in);
		int x;
		String eleicao = "";
		ArrayList<String> dep=new ArrayList<String>();
		do {
			System.out.println("Selecione uma opção!");
			System.out.println("1->Eleicao do nucleo de estudantes");
			System.out.println("2->Eleicao do concelho geral(estudantes)");
			System.out.println("3->Eleicao do concelho geral(docentes)");
			System.out.println("4->Eleicao do concelho geral(funcionarios)");
			System.out.println("Escolha a sua opção: ");
			x= keyboardScanner.nextInt();

		}while(x<0 || x>4);
		
		
		System.out.println("Data de inicio (dd/mm/aaaa):");
		eleicao=eleicao+keyboardScanner.nextLine()+"#";
		
		System.out.println("hora do inicio (hh:mm:ss):");
		eleicao=eleicao+keyboardScanner.nextLine()+"#";
		
		System.out.println("hora do fim (hh:mm:ss):");
		eleicao=eleicao+keyboardScanner.nextLine()+"#";
		
		eleicao=Integer.toString(x)+"#";
		
		System.out.println("Nome da eleicao:");
		eleicao=eleicao+keyboardScanner.nextLine()+"#";
		
		System.out.println("Descricao da eleicao:");
		eleicao=eleicao+keyboardScanner.nextLine()+"#";
		
		dep.add(eleicao);
		
		if(x==1) {
			System.out.println("Nome do departamento:");
			dep.add(keyboardScanner.nextLine());
		}
		else {
			System.out.println("Numero de departamentos:");
			x= keyboardScanner.nextInt();
			for (int i = 0; i < x; i++) {
				System.out.println("Nome do departamento:");
				dep.add(keyboardScanner.nextLine());
			}
		}
		
		return dep;
	}
	
	public static ArrayList<String> mini_menu_4(String n_lista) {
		Scanner keyboardScanner = new Scanner(System.in);
		int x;
		String info = "";
		String id_eleicao="";
		String nome_lista=n_lista;
		Boolean first=true;
		ArrayList<String> lista=new ArrayList<String>();
		
		//numero de membros da lista
		System.out.println("Numero de elementos: ");
		x=keyboardScanner.nextInt();
		info=info+ keyboardScanner.nextLine();
		
		for (int i = 0; i < x; i++) {
			
			//cc
			System.out.println("Numero de Indentificacao: ");
			info=info+ keyboardScanner.nextLine();
			info=info+"#";
			/*identificaçao da eleicao*/
			if(first) {
				System.out.println("Id da eleicao: ");
				id_eleicao=keyboardScanner.nextLine();
				info=info+ id_eleicao;
				info=info+"#";
				
			}
			else {
				info=info+ id_eleicao+"#";
			}

			//nome da lista
			info=info+ nome_lista;
			if(first)
				System.out.println("Nome da lista: "+nome_lista);
			info=info+"#";

			//cargo politico
			System.out.println("Cargo politico: ");
			info=info+ keyboardScanner.nextLine();
			first=false;

			lista.add(info);
			info="";
		}
		System.out.println(lista);
		return lista;
	}
	public static ArrayList<String> mini_menu_5() {
		Scanner keyboardScanner = new Scanner(System.in);
		ArrayList <String> dados=new ArrayList<String>();
		System.out.println("Selecione uma opção!");
		System.out.println("1->Adicionar uma mesa de voto");
		System.out.println("2->Remover uma mesa de voto");
		System.out.println("3->Voltar ao menu principal");
		int x=keyboardScanner.nextInt();
		String temp;
		switch(x) {
			case 1:
				System.out.println("ID da eleição: ");
				temp=keyboardScanner.nextLine();
				//verificação
				dados.add(temp);
				System.out.println("Numero de mesa de voto a acrescentar: ");
				x=keyboardScanner.nextInt();
				for (int i = 0; i < x; i++) {
					System.out.println("Nome do departamento: ");
					dados.add(keyboardScanner.nextLine());
				}
			case 2:
				System.out.println("Disponivel brevemente");
				break;
			case 3:
				break;
			default:
				System.out.println("Operação inexistente");
				break;
		}
		return dados;
		
		
		
	}
	public static String mini_menu_6(rmi_interface admin_console) {
		Scanner keyboardScanner = new Scanner(System.in);
		ArrayList <String> dados=new ArrayList<String>();
		System.out.println("Selecione uma opção!");
		System.out.println("1->Alterar data de eleição");
		System.out.println("2->Alterar hora de inicio/fim de uma eleição");
		System.out.println("3->Alterar titulo de eleição");
		System.out.println("4->Alterar descrição de eleição");
		System.out.println("5->Voltar ao menu principal");
		int x=keyboardScanner.nextInt();
		String temp,info;
		String []n;
		try {
			switch(x) {
			case 1:
				System.out.println("ID da eleição: ");
				temp=keyboardScanner.nextLine();
				//receber a eleição

				info =admin_console.get_info_eleicao(Integer.toString(x));
				System.out.println("Nova data(dd/mm/aaaa): ");
				temp=keyboardScanner.nextLine();
				n=info.split("#");
				info="";
				n[1]=temp;

				info=junta_eleicao(n);
				return info;

			case 2:
				//receber a eleição
				info =admin_console.get_info_eleicao(Integer.toString(x));
				n=info.split("#");
				info="";

				System.out.println("Nova hora inicial(hh:mm): ");
				temp=keyboardScanner.nextLine();

				n[2]=temp;

				System.out.println("Nova hora final(hh:mm): ");
				temp=keyboardScanner.nextLine();
				n[3]=temp;


				info=junta_eleicao(n);
				return info;


			case 3:

				info =admin_console.get_info_eleicao(Integer.toString(x));
				System.out.println("Novo Titulo: ");
				temp=keyboardScanner.nextLine();

				n=info.split("#");
				info="";
				n[5]=temp;

				info=junta_eleicao(n);
				return info;
			
			case 4:
				//receber a eleição
				info =admin_console.get_info_eleicao(Integer.toString(x));
				System.out.println("Nova Descrição: ");
				temp=keyboardScanner.nextLine();

				n=info.split("#");
				info="";
				n[6]=temp;

				info=junta_eleicao(n);
				return info;
				 
			default:
				System.out.println("Operação inexistente");
				break;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private static String junta_eleicao(String[] s) {
		String n="";
		for (int i = 0; i < s.length; i++) {
			n=n+s[i]+"#";
		}
		n = n.substring(0, n.length() - 1);
		return n;
	}
	
	public static void mini_menu_7(rmi_interface admin_console) {
		Scanner keyboardScanner = new Scanner(System.in);
		
		System.out.println("Numero de identificaçao do eleitor: ");
		String id_eleicao=keyboardScanner.nextLine();
		
		System.out.println("Id da eleicao: ");
		id_eleicao=id_eleicao+"#"+keyboardScanner.nextLine();
		
		try {
			System.out.println(admin_console.get_local_de_voto(id_eleicao));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static String mini_menu_9() {

		int opcao;
		Scanner keyboardScanner = new Scanner(System.in);
		
		System.out.println("Id da eleicao: ");
		String id_eleicao=keyboardScanner.nextLine();
		
		do {
			System.out.println("Parametro a alterar!");
			System.out.println("1->Titulo");
			System.out.println("2->Descricao");
			System.out.println("3->Inicio");
			System.out.println("4->Fim");
			System.out.println("Escolha a sua opção: ");
			opcao= keyboardScanner.nextInt();
		}while(opcao>5 || opcao<0);
		
		switch(opcao) {
			case 1:
				System.out.println("Novo Titulo");
				break;
			case 2:
				System.out.println("Nova descrição");
				break;
			case 3:
				System.out.println("Novo inicio");
				break;
			case 4:
				System.out.println("Novo fim");
				break;
		}
		
		
		return null;
	}
	
	public static String mini_menu_10() {
		Scanner keyboardScanner = new Scanner(System.in);
		
		System.out.println("Id da eleicao: ");
		String id=keyboardScanner.nextLine();
		
		return id;
	}
	
}
		
	
