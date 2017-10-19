import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

class fil_testing {
  public static void main(String args[]) throws IOException, InterruptedException{
	  

	  String x="5#12/10/2017#16:00:00#17:00:00#tipo#tip#teste";
	  String[] n= x.split("#");
	  x="";
	  n[0]="4";
	  for (int i = 0; i < n.length; i++) {
		x=x+n[i]+"#";
	  }
	  x = x.substring(0, x.length() - 1);

	  System.out.println(x);
	  
	  
		
  }
}
