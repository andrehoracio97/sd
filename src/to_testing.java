import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

class to_testing {
  public static void main(String args[]) throws IOException, ParseException{
	   int id_i=4;
	   
	   String id = String.valueOf(id_i);
	   String nome="locais_voto.txt";
	   String line;
	   String [] separado;
	   BufferedReader br = new BufferedReader(new FileReader(nome));
	   Scanner keyboardScanner = new Scanner(System.in);
	   String escolha;
	   
	   line = br.readLine();
	   for(int i=1; (line = br.readLine()) !=null;i++){
		   separado=line.split("#");
		   if(separado[0].equals(id) && separado[3].equals("0")) {
			   System.out.println(separado[2]);
		   }
	   }
	   br.close();
	   escolha=keyboardScanner.nextLine();
	   
	   int id_local_return=0;


		String temporario="voto_tmp.txt";
		BufferedReader br2 = new BufferedReader(new FileReader(nome));
		PrintWriter pw = new PrintWriter(new FileWriter(temporario));

		
		for(int i=1; (line = br2.readLine()) !=null;i++){
			separado = line.split("#"); 
			if (separado[0].equals(id) && separado[2].equals(escolha)) {
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
	   

	   System.out.println("returnnnnnn"+id_local_return);
	   
	   



	   
	   
	   
	   
	   
	   
	   
	   
  }
}
