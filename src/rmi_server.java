//package calculator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class rmi_server extends UnicastRemoteObject implements rmi_interface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected rmi_server() throws RemoteException {
		super();
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {
		rmi_interface rmi = new rmi_server();
		LocateRegistry.createRegistry(1099).rebind("rmi", rmi);
		System.out.println("Rmi server ready...");
	}

	public String print_listas() { //colocar o id da eleicao
		//falta fechar os bufferssss
		String listas = "Type|listas;";
		try {  
			File file = new File("candidatos.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			line = br.readLine(); //line = candidatos
			for(int i=1; (line = br.readLine()) !=null;i++){
				String[] lista;
				lista = line.split("#");
				if (lista[3].equals("Presidente")) {
					File bd = new File("bd.txt");
					BufferedReader br2 = new BufferedReader(new FileReader(bd));
					String line2;
					line2 = br2.readLine();
					for(int j=1; (line2 = br2.readLine()) !=null;j++){
						String[] pessoa;
						pessoa = line2.split("#");
						if(pessoa[1].equals(lista[0])) {
							listas=listas+lista[2]+"|"+pessoa[0]+";";
							break;
						}

					} 
				}
			} 

			System.out.println(listas);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listas;
	}

	public boolean verifica_pass(String cc, String pass) {
		  try {  
			  File file = new File("bd.txt");
	          BufferedReader br = new BufferedReader(new FileReader(file));
	          String line;
	          line = br.readLine();
	          for(int i=1; (line = br.readLine()) !=null;i++){
	              String[] lista;
	              lista = line.split("#");
	               //lista[1] é o cc
	        	   //lista[3] é a pass
	              if(lista[1].equals(cc)) {    
	            	  if(lista[3].equals(pass)) {
	            		  return true;
	            	  }
	            	  else {
	            		  return false;
	            	  }
	              }
	          }
		  }catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return false;
	}
	
	public void vota(String lista) throws IOException {
		// exemplo do que recebe: String lista="type|voto;lista|F";
		String[] protocolo;
		protocolo=lista.split(";");
		String votada;
		
		try {
			votada=protocolo[1].split("\\|")[1];
		}catch(ArrayIndexOutOfBoundsException e) {
			votada="*";
		}
		
		String nome="live_stats.txt";
		String temporario="temp.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));
		PrintWriter pw = new PrintWriter(new FileWriter(temporario));
		
		String line;
		String[] separado;
		int flag=0;
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("-");
			if (separado[0].equals(votada)) {
				pw.printf("%s-%s\n",separado[0],Integer.valueOf(separado[1])+1);
				flag=1;
				
			}else {
				if(votada.equals("*") && separado[0].equals("Brancos")) {
					pw.printf("%s-%s\n",separado[0],Integer.valueOf(separado[1])+1);
					flag=1;
				}
				else if(separado[0].equals("Nulos") && flag==0) {
					pw.printf("%s-%s\n",separado[0],Integer.valueOf(separado[1])+1);
					flag=1;
					break;
				}
				else pw.printf("%s-%s\n",separado[0],separado[1]);
			}
		}
		pw.close();
		br.close();

		new File(nome).delete();
	    new File(temporario).renameTo(new File(nome));
	}
	
	public void cria_live_stats() throws IOException {
	    PrintWriter pw = new PrintWriter(new FileWriter("live_stats.txt"));
	    
		String listas=print_listas();
		String[] tudo=listas.split(";");
		String lista_guardar;
		for (int i=1; i<tudo.length;i++) {
			lista_guardar=tudo[i].split("\\|")[0];
			pw.printf("%s-0\n",lista_guardar);
		}
		pw.printf("Branco-0\n");
		pw.printf("Nulos-0");
		pw.close();
	}
	
	public void local_de_voto(String lista) throws IOException {
	    //exemplo lista="type|eleitor;cc|111111111;local|fctuc;id|1";
		Date test = new Date();
		String[] protocolo;
		
		protocolo=lista.split(";");
		String cc=protocolo[1].split("\\|")[1];
		String local=protocolo[2].split("\\|")[1];
		String id= protocolo[3].split("\\|")[1];
		String nome="eleitores.txt";
		
		
		String dayString = test.toString();
		String timeString = dayString.substring( 11 , 19 );
	
		Writer output;
		output = new BufferedWriter(new FileWriter(nome,true));  
		output.append("\n"+cc+"#"+id+"#"+local+"#"+timeString);
		output.close();
	}
	
	public void add_pessoa(String lista) throws IOException {
		//exemplo String lista="toto#123123123#2010#qwe#dei#fctuc#2233#Estudante";
		String nome="bd.txt";
		Writer output;
		output = new BufferedWriter(new FileWriter(nome,true));  //clears file every time
		output.append("\n"+lista);
		output.close();
	}
    
	public boolean verifica_se_votou(String lista) throws IOException {
		//exemplo 111111111#1
		String nome="eleitores.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));

		String[] lista_s;
		lista_s=lista.split("#");
		System.out.println(lista_s[1]);
		String[] protocolo;
		String line;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			protocolo=line.split("#");
			if(protocolo[1].equals(lista_s[1]) && protocolo[0].equals(lista_s[0])) {
				return true;
			}
		}
		return false;
	}
	
	public String nome_to_cc(String nome) throws IOException {
	    //exemplo nome="cc";
		File bd = new File("bd.txt");
		BufferedReader br = new BufferedReader(new FileReader(bd));
		String line;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			String[] pessoa;
			pessoa = line.split("#");
			if(pessoa[0].equals(nome)) {
				return pessoa[1];
			}
		}
		return "type|not_found";
	}
	
	public String proxima_lista() {
		String listas =print_listas();
		String ultima_lista[] = listas.split(";");
		String ultima=(ultima_lista[ultima_lista.length-1].split("\\|")[0]);
		char c=ultima.charAt(0);
		String ult = String.valueOf((char)(c+1));
		return ult;
	}
	
	public boolean verifica_cc_existe(String cc) throws IOException {
	    //String cc="222222223";
		  
		File bd = new File("bd.txt");
		BufferedReader br = new BufferedReader(new FileReader(bd));
		String line;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			String[] pessoa;
			pessoa = line.split("#");
			if(pessoa[1].equals(cc)) {
				return true;
			}
		} 
		return false;
	}
	public String verifica_cc_existe_retorna_tipo(String cc) throws IOException {
	    //String cc="222222223";
		  
		File bd = new File("bd.txt");
		BufferedReader br = new BufferedReader(new FileReader(bd));
		String line;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			String[] pessoa;
			pessoa = line.split("#");
			if(pessoa[1].equals(cc)) {
				return pessoa[8];
			}
		} 
		return null;
	}
	
	public String get_local_de_voto(String lista) throws IOException {

		  //entra cc#id_da_eleicao retornar local e hora
		  //exemplo String lista ="111111111#1";
		 
		  String[] lista_s= lista.split("#");
		  
		  String nome="eleitores.txt";
		  BufferedReader br = new BufferedReader(new FileReader(nome));
		  String line;
		  String[] protocolo;
		  line = br.readLine();
		  for(int i=1; (line = br.readLine()) !=null;i++){
			  System.out.println(line);
			  protocolo=line.split("#");
			  if (protocolo[0].equals(lista_s[0])) {
				  if (protocolo[1].equals(lista_s[1])) {
					  return protocolo[2]+"#"+protocolo[3];
				  }
			  }
		  }
		  return "type|not_found"; 
	 }
	
	public String resultados (String id) throws IOException {
		
		  //exemplo String id="1";
		  String nome="resultados.txt";
		  String ret="";
		  BufferedReader br = new BufferedReader(new FileReader(nome));
		  String line;
		  String[] protocolo;
		  line = br.readLine();
		  for(int i=1; (line = br.readLine()) !=null;i++){
			  protocolo=line.split("#");
			  if (protocolo[0].equals(id)) {
				  ret=ret+protocolo[1]+"#"+protocolo[2]+"#"+protocolo[3]+"|";
			  }
		  }
		  return ret;



	}

	public void add_dep_fac(String lista) throws IOException {
	    //recebe por exemplo fctuc_xxx
		
		String max="0";
		String nome="uc.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(nome));
		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (Integer.valueOf(separado[2])>Integer.valueOf(max)) {
				max=separado[2];
			}
		}
		br.close();

		String id= Integer.toString(Integer.valueOf(max)+1);
		System.out.println(id);
	
		Writer output;
		output = new BufferedWriter(new FileWriter(nome,true));  //clears file every time
		output.append("\n"+lista+"#"+id);
		output.close();
	}
	
	public boolean verifica_fac_existe(String faculdade) throws IOException {
	    //exemplo String faculdade="fctuc";
		String nome="uc.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(nome));
		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (separado[0].toLowerCase().equals(faculdade.toLowerCase())) {
				
				return true;
			}
		}
		br.close();
		return false;
	}
	
	public boolean verifica_dep_existe(String dep) throws IOException {
	    //exemplo String dep="dei";
		String nome="uc.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(nome));
		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (separado[1].toLowerCase().equals(dep.toLowerCase())) {
				return true;
			}
		}
		br.close();
		return false;
	}
	
	public void remove_departamento(String departamento) throws IOException {
		  //exemplo String departamento="deec";
		  String nome="uc.txt";
		  String temporario="temp.txt";
		  BufferedReader br = new BufferedReader(new FileReader(nome));
		  PrintWriter pw = new PrintWriter(new FileWriter(temporario));

		  
		  String line;
		  String[] separado;
		  for(int i=1; (line = br.readLine()) !=null;i++){
			  separado = line.split("#");
			  if (!separado[1].equals(departamento)) {
				  pw.printf("%s\n",line);
			  }
		  }
		  
		  pw.close();
		  br.close();

		  new File(nome).delete();
		  new File(temporario).renameTo(new File(nome));

		  
	}
	
	//alterar
	//recebe uma lista em que o primeiro elemento é 
	//12/10/2017#16:00:00#17:00:00#tipo#tip#teste
	//os restantes elementos da lista sao os departamentos em que decorrem a eleicao 
	
	public void add_votacao(ArrayList <String> lista) throws IOException {
		/*ArrayList <String> lista = new ArrayList<String>();
		lista.add("12/10/2017#16:00:00#17:00:00#tipo#tip#teste");
		lista.add("dei");
		lista.add("deec");
		lista.add("dic");*/

		String max="0";
		String nome="eleicoes.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));

		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (Integer.valueOf(separado[0])>Integer.valueOf(max)) {
				max=separado[0];
			}
		}


		String id= Integer.toString(Integer.valueOf(max)+1);


		Writer output;
		output = new BufferedWriter(new FileWriter(nome,true));  //clears file every time
		output.append("\n"+id+"#"+lista.get(0));
		output.close();


		String nome_dep;
		String nome2="locais_voto.txt";
		String id_dep="";
		nome="uc.txt";
		line=null;
		BufferedReader br2 = new BufferedReader(new FileReader(nome));


		output = new BufferedWriter(new FileWriter(nome2,true));  //clears file every time
		for(int i=1; i<lista.size();i++) {
			nome_dep=lista.get(i);



			line = br2.readLine();
			for(int j=1; (line = br2.readLine()) !=null;j++){
				separado = line.split("#");
				if(nome_dep.toLowerCase().equals(separado[1].toLowerCase())) {
					id_dep=separado[2];
					output.append("\n"+id+"#"+id_dep+"#"+nome_dep.toLowerCase()+"#0");
					break;
				}
			}
		}
		output.close();
		br.close();
	}

	public String get_votacao(String id) throws IOException {
		//recebe o id e develve os dados todos na forma:  2#12/10/2017#16:00:00#17:00:00#tipo#tip#teste
		String nome="eleicoes.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));

		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (separado[0].equals(id)) {
				return line;
			}
		}
		br.close();
		return "type|not_found";
	}

	public void set_votação(String lista) throws IOException {
	    //exemplo String lista="2#12/10/2017#6:00:00#17:00:00#xaxa#toctoc#teste";
		   
	    String lista_s[]=lista.split("#");
		String nome="eleicoes.txt";
		String temporario="elei_tmp.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));
		PrintWriter pw = new PrintWriter(new FileWriter(temporario));


		String line;
		String[] separado;
		
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (separado[0].equals(lista_s[0])) {
				pw.printf("%s\n",lista);
			}
			else {
				pw.printf("%s\n",line);
			}
		}

		pw.close();
		br.close();

		new File(nome).delete();
		new File(temporario).renameTo(new File(nome));


		
		
	}
	
	public boolean verifica_tipo_eleicao_e_cc(String tipo, String cc) throws IOException {
		  /*String tipo="4";
		  String cc="555555555";*/
		  String nome="bd.txt";
		  BufferedReader br = new BufferedReader(new FileReader(nome));
		  String line;
		  String[] separado;
		  line = br.readLine();
		  for(int i=1; (line = br.readLine()) !=null;i++){
			  separado = line.split("#");
			  if (separado[1].equals(cc)){
				  if(separado[7].equals("estudante") && (tipo.equals("1")||(tipo.equals("2")))){
					  return true;
				  }
				  else if(separado[7].equals("docente") && tipo.equals("3")) {
					  return true;
				  }
				  else if(separado[7].equals("funcionario") && tipo.equals("4")) {
					  return true;
				  }
				  else {
					  return false;
				  }
			  }
		  }
		  return false;
	}

	public void add_candidatos(ArrayList <String> lista) throws IOException {
		
		  /*ArrayList <String> lista = new ArrayList<String>();
		  lista.add("555555555#1#D#blabla");
		  lista.add("666666666#1#D#blavla");
		  lista.add("777777777#1#D#Presidente");*/
		  
		  String nome="candidatos.txt";
		  Writer output;
		  output = new BufferedWriter(new FileWriter(nome,true));
		  for(int i=0; i< lista.size();i++) {
			  output.append("\n"+lista.get(i));
		  }
		  output.close();
	}
	
	public boolean verifica_eleicao_existe(String id) throws IOException {
		//String id="6";
		String nome="eleicoes.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));
		String line;
		String[] separado;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			separado = line.split("#");
			if (separado[0].equals(id)) {
				br.close();
				return true;
			}
		}
		br.close();
		return false;
	}
	public void add_mesas_voto_a_eleicao(ArrayList <String> lista) throws IOException {
		//recebe exemplo lista com [numero de eleicao, dep1, dep2, dep3,...]
		String nome_dep;
		String id_dep="";
		String nome="uc.txt";
		String nome2="locais_voto.txt";
		String line=null;
		String [] separado;
		BufferedReader br2 = new BufferedReader(new FileReader(nome));
		Writer output;
		output = new BufferedWriter(new FileWriter(nome2,true));  //clears file every time
		for(int i=1; i<lista.size();i++) {
			nome_dep=lista.get(i);
			line = br2.readLine();
			for(int j=1; (line = br2.readLine()) !=null;j++){
				separado = line.split("#");
				if(nome_dep.toLowerCase().equals(separado[1].toLowerCase())) {
					id_dep=separado[2];
					output.append("\n"+lista.get(0)+"#"+id_dep+"#"+separado[1]+"#0");
					break;
				}
			}
		}
		output.close();
		br2.close();
	}
	
	public String get_info_eleicao(String id) throws IOException {
		  //String id="5";
		  String nome="eleicoes.txt";
		  BufferedReader br = new BufferedReader(new FileReader(nome));
		  String line;
		  String[] separado;
		  line = br.readLine();
		  for(int i=1; (line = br.readLine()) !=null;i++){
			  separado=line.split("#");
			  if (separado[0].equals(id)){
				  return line;
			  }
		  }
		  return "not_found";
		
	}
	
	public ArrayList<String> get_id_tipo_eleicoes_De_hoje() throws IOException {
		ArrayList <String> array = new ArrayList <String>();
		Date hoje = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		String hoje_s=formatter.format(hoje);


		String nome="eleicoes.txt";
		BufferedReader br = new BufferedReader(new FileReader(nome));

		String[] protocolo;
		String line;
		line = br.readLine();
		for(int i=1; (line = br.readLine()) !=null;i++){
			protocolo=line.split("#");
			if(protocolo[1].equals(hoje_s)) {
				System.out.println(protocolo[0]);
				array.add(protocolo[0]);
				array.add(protocolo[4]);
			}
		}
		br.close();
		return array;
	}
	
	public ArrayList<String> retorna_mesas_voto(int id_int) throws IOException {
		   //int id_i=4;
		   ArrayList<String> lista = new ArrayList<String>();
		   String id = String.valueOf(id_int);
		   String nome="locais_voto.txt";
		   String line;
		   String [] separado;
		   BufferedReader br = new BufferedReader(new FileReader(nome));
		  	   
		   line = br.readLine();
		   for(int i=1; (line = br.readLine()) !=null;i++){
			   separado=line.split("#");
			   if(separado[0].equals(id) && separado[3].equals("0")) {
				   lista.add(separado[2]);
			   }
		   }
		   br.close();
		   return lista;
	}
	public int retorna_id_mesa_voto_escolhida(int id_int,String departamento) throws IOException {
		
		String id = String.valueOf(id_int);
		int id_local_return=0;
		String nome="locais_voto.txt";
		String line;
		String [] separado;

		String temporario="voto_tmp.txt";
		BufferedReader br2 = new BufferedReader(new FileReader(nome));
		PrintWriter pw = new PrintWriter(new FileWriter(temporario));


		for(int i=1; (line = br2.readLine()) !=null;i++){
			separado = line.split("#"); 
			if (separado[0].equals(id) && separado[2].equals(departamento)) {
				pw.printf("%s#%s#%s#%s\n",separado[0],separado[1],separado[2],"1");
				System.out.println("blaaaaa"+separado[0]+separado[1]+separado[2]);
				id_local_return=Integer.parseInt(separado[1]);
			}
			else {
				pw.printf("%s\n",line);
			}
		}

		pw.close();
		br2.close();

		new File(nome).delete();
		new File(temporario).renameTo(new File(nome));
		return id_local_return;

	}
}
