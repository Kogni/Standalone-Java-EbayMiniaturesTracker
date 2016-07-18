package Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import Threads.Thread_TimeKeeper;

public class Brain_Currency {

	Controller Class_Controller;
	URL url;
	HttpURLConnection connection;
	StringBuffer DataImported = new StringBuffer();
	
	public Brain_Currency( Controller Class_Controller ) {
		this.Class_Controller = Class_Controller;
		
		GetCurrency();
	}
		
	public void GetCurrency() {

		try {
			url = new URL( "http://themoneyconverter.com/USD/NOK.aspx" );
			
			connection = (HttpURLConnection) url.openConnection();

	        URLConnection conn;
			
				conn = url.openConnection();
			
	        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
	        
	        BufferedReader in = new BufferedReader( new InputStreamReader(conn.getInputStream()) );
	        String str;
	
	        while ((str = in.readLine()) != null) {
	                DataImported.append( str );
	        }
	        HentInfo( DataImported );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void HentInfo( StringBuffer buffer ) {

		try {
			String Buffer = buffer.toString().toLowerCase() ;
			String Temp = Buffer;
			//System.out.println( "0 "+Temp.indexOf("<div id='tmc-header'>") );
			Temp = Temp.substring( Temp.indexOf("<div id='tmc-header'>")+"<div id='tmc-header'>".length() );
			//System.out.println( "A "+Temp );
			Temp = Temp.substring( Temp.indexOf("latest exchange rates:")+"latest exchange rates:".length() );
			//System.out.println( "B "+Temp );
			Temp = Temp.substring( Temp.indexOf("1 united states dollar = ")+"1 united states dollar = ".length() );
			//System.out.println( "C "+Temp );
			//Temp = Temp.substring( Temp.indexOf("1 United States Dollar = ")+"1 United States Dollar = ".length() );
			//System.out.println( "D "+Temp );
			Temp = Temp.substring( 0, Temp.indexOf(" norwegian krone") );
			Double Currency = 0.0;
			//System.out.println( "E "+Temp );
			try {
				Currency = Double.parseDouble( Temp );
				Class_Controller.SetCurrency( Currency );
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
