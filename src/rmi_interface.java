//package calculator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface rmi_interface extends Remote {
	
	//----------------------SERVIDOR TCP------------------------------------
	public String print_listas() throws RemoteException;
	public boolean verifica_pass(String cc, String pass) throws RemoteException;
	public void vota(String lista)throws RemoteException, IOException;
	public void local_de_voto(String lista) throws RemoteException, IOException;
	public boolean verifica_se_votou(String lista) throws RemoteException, IOException;
	public void cria_live_stats() throws IOException, RemoteException;
	//--------------------------------------------------------------
	
	
	
	//----------------------ADMIN CONSOLE------------------------------
	public void add_pessoa(String lista)throws RemoteException, IOException;
	public String proxima_lista() throws RemoteException;
	public boolean verifica_cc_existe(String cc) throws RemoteException, IOException; //true se existir || false se nao existir
	public String verifica_cc_existe_retorna_tipo(String cc) throws RemoteException, IOException;
	
	//-----------------------------------------------------
	public String get_local_de_voto(String lista) throws RemoteException, IOException;
	
	
	public String resultados(String id) throws IOException, RemoteException;
	
	//adicionar/remover departamentos e faculdades a uma eleicao
	public void add_dep_fac(String lista) throws IOException, RemoteException;
	public boolean verifica_fac_existe(String faculdade) throws IOException, RemoteException;//TRUE se existir
	public boolean verifica_dep_existe(String faculdade) throws IOException, RemoteException;//TRUE se existir
	public void remove_departamento(String departamento) throws IOException, RemoteException;
	
	public void add_votacao(ArrayList <String> lista) throws IOException, RemoteException;
	public String get_votacao(String id) throws IOException, RemoteException;
	public void set_votação(String lista) throws IOException, RemoteException;
	 	
	
	public boolean verifica_tipo_eleicao_e_cc(String tipo, String cc) throws IOException, RemoteException;
	public void add_candidatos(ArrayList <String> lista) throws IOException, RemoteException;
	public boolean verifica_eleicao_existe(String id) throws IOException, RemoteException; //True se existe
	
	public void add_mesas_voto_a_eleicao(ArrayList <String> lista) throws IOException, RemoteException;

	public String get_info_eleicao(String id)throws IOException, RemoteException;
	public ArrayList<String> get_id_tipo_eleicoes_De_hoje() throws IOException, RemoteException;
	public ArrayList<String> retorna_mesas_voto(int id_int) throws IOException,RemoteException;
	public int retorna_id_mesa_voto_escolhida(int id_int,String departamento) throws IOException, RemoteException;
	
}
