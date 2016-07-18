package Threads;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import Control.Controller;
import Objects.Object_Ord;
import Objects.Object_Webpage;

public class Thread_LinkScanner extends Thread {
	
	Controller Class_Controller;
	
	public Object_Webpage LinkToCheck;
	
	URL url;
	HttpURLConnection connection;
	//Thread_TimeKeeper Timer;
	StringBuffer DataImported = new StringBuffer();
	
	int RelationValue = 0;
	
	public boolean Busy = false;
	Thread_URLFinder URLFinner;
	Thread_PriceSaver PrisFinner;
	
	public Thread_LinkScanner( Controller Class_Controller, Object_Webpage LinkToCheck ) {
		//System.out.println ( "Scanner url="+LinkToCheck.Get_URL()  );
		//System.out.println ( System.currentTimeMillis()+" +Thread_LinkScanner created: "+this.getName()+" "+LinkToCheck.Get_URL() );
		
		this.Class_Controller = Class_Controller;
		this.LinkToCheck = LinkToCheck;

	}

	public void run() {
		//System.out.println ( "Thread_LinkFinder run" );
		
		Scan();
		
	}
	
	private void Scan() {
		//System.out.println ( this+" Scanner url="+LinkToCheck.Get_URL()  );
		Busy = true;
		URL url = LinkToCheck.Get_URL();
		String temp = url.toString().replaceAll(" ", "+");
		try {
			url = new URL( temp );
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*String temp = url.toString().replace( "http://www.ebay.com/itm", "http://viewitem.eim.ebay.no" );
		if ( url.toString().indexOf("http://www.ebay.com/itm") > -1) {
			try {
				url = new URL( temp );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		try {
			
			
			connection = (HttpURLConnection) url.openConnection();
			//Thread_TimeKeeper Timer = new Thread_TimeKeeper( this );
			//Timer.start();

            //URL url = new URL("http://www.google.com/search?q=miniature");
            URLConnection conn =  url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
            //conn.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "+ "Windows NT 5.1; en-US; rv:1.8.0.11) ");
            //conn.setRequestProperty("Content-Type", "text/xml");
            //conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            //conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
            //conn.setRequestProperty("CONTENT-TYPE","text/xml; charset=utf-8");
            
            BufferedReader in = new BufferedReader( new InputStreamReader(conn.getInputStream()) );
            String str;

            while ((str = in.readLine()) != null) {
                    //System.out.println(str);
                    DataImported.append( str );
            }
            //System.out.println(System.currentTimeMillis()+" URL scanned. Data size="+DataImported.length() );
            ScanStrings( DataImported );

		} catch ( FileNotFoundException T ) {
			LinkToCheck.Set_SelfRelationValue( 0 );
			System.err.println(System.currentTimeMillis()+" FileNotFoundException "+T.getMessage ( )+" "+LinkToCheck.Get_URL() );
		} catch ( UnknownHostException T ) {
			LinkToCheck.Set_SelfRelationValue( 0 );
			System.err.println(System.currentTimeMillis()+" UnknownHostException "+T.getMessage ( )+" "+LinkToCheck.Get_URL() );
		} catch ( ConnectException T ) {
			LinkToCheck.Set_SelfRelationValue( 0 );
			System.err.println(System.currentTimeMillis()+" ConnectException "+T.getMessage ( )+" "+LinkToCheck.Get_URL() );
			this.Class_Controller.SaveURL( LinkToCheck.Get_URL().toString(), "Thread_LinkScanner", LinkToCheck.LinkedRelationValue );
		} catch ( IOException T ) {
			System.err.println(System.currentTimeMillis()+" IOException "+T.getMessage ( )+" "+LinkToCheck.Get_URL() );

			try {
				System.err.println( "getResponseMessage="+connection.getResponseMessage() );
				System.err.println( "getResponseCode="+connection.getResponseCode() );
			} catch (IOException e) {
				CastErrors( T );
			}
			
			LinkToCheck.Set_SearchFailed();
			LinkToCheck.Set_LinkedRelationValue( LinkToCheck.Get_LinkedRelationValue()-10 );
			if ( LinkToCheck.Get_LinkedRelationValue() < this.Class_Controller.InterestBorder ) {
				LinkToCheck.Set_LinkedRelationValue( Class_Controller.InterestBorder );
			}
			
		} catch ( IllegalArgumentException T ) {
			LinkToCheck.Set_SelfRelationValue( 0 );
			System.err.println(System.currentTimeMillis()+" IllegalArgumentException "+T.getMessage ( )+" "+LinkToCheck.Get_URL() );
		} catch ( Exception T ) {
			CastErrors( T );
			LinkToCheck.Set_SelfRelationValue( 0 );
			System.err.println(System.currentTimeMillis()+" Exception "+LinkToCheck.Get_URL() );
		}
		
		//System.out.println ( "Thread_LinkScanner ferdig" );
		Busy = false;
		this.Class_Controller.TimeTick( "Thread_LinkScanner completed", this.LinkToCheck.Get_URL() );
	}
	
	private void ScanStrings( StringBuffer buffer ) {

		String Buffer = buffer.toString().toLowerCase() ;

		try {
			
			Object_Ord[] TempOrdbok = this.Class_Controller.GetOrdliste();
			RelationValue = 0;
			for ( int X = 0 ; X < TempOrdbok.length ; X++ ) {
				if ( TempOrdbok[X] != null ) {
					//System.out.println( "linkfinder "+X+" "+TempOrdbok[X].Ordet);
					if ( TempOrdbok[X].Value != 0 ) {
						int Bonus = 0;
						int Occurence1 = countOccurrences( Buffer, TempOrdbok[X].Ordet.toLowerCase() );
						int Occurence2 = countOccurrences( Buffer, " "+TempOrdbok[X].Ordet.toLowerCase()+" " );
						int Occurence3 = countOccurrences( Buffer, TempOrdbok[X].Ordet.toLowerCase().replaceAll(" ", "") );
						int Occurence4 = countOccurrences( Buffer, TempOrdbok[X].Ordet.toLowerCase().replaceAll(" ", "-") );

						Bonus = Bonus + ( Math.min( Occurence1, 3 )* TempOrdbok[X].Value);
						Bonus = Bonus + ( Math.min( Occurence2, 3 )* TempOrdbok[X].Value*2);
						Bonus = Bonus + ( Math.min( Occurence3, 3 )* TempOrdbok[X].Value);
						Bonus = Bonus + ( Math.min( Occurence4, 3 )* TempOrdbok[X].Value);

						RelationValue = RelationValue + Bonus;
					}
				} else {
					X = TempOrdbok.length;
				}
			}

			LinkToCheck.Set_SelfRelationValue( RelationValue );
			
			if ( URLFinner == null ) {
				URLFinner = new Thread_URLFinder( Class_Controller, Buffer, this, LinkToCheck.Get_URL().toString() );
				URLFinner.start();
			} else {
				while( URLFinner.Busy == true ) {
					this.sleep( 100 );
					//System.out.println( "Thread_LinkScanner venter på at Thread_URLFinder skal bli ferdig" );
				}
				URLFinner.Owner = this;
				URLFinner.Data = Buffer;
				URLFinner.URLsource = LinkToCheck.Get_URL().toString();
				URLFinner.run();
				
			}
			
			//if ( LinkToCheck.Get_URL().toString().indexOf( "search") == -1 ) {//kun på norsk ebay
			if ( LinkToCheck.Get_URL().toString().indexOf( "/sch/") == -1 ) { //kun på amerikansk ebay
				if ( PrisFinner == null ) {
					PrisFinner = new Thread_PriceSaver( Class_Controller, Buffer, this, LinkToCheck.Get_URL().toString() );
					PrisFinner.start();
					PrisFinner.SplitLine1( Buffer, this, LinkToCheck.Get_URL().toString() );
				} else {
					while( PrisFinner.Busy == true ) {
						this.sleep( 100 );
						//System.out.println( "Thread_LinkScanner venter på at Thread_PriceSaver skal bli ferdig" );
					}

					PrisFinner.SplitLine1( Buffer, this, LinkToCheck.Get_URL().toString() );
				}
			}
			
		} catch ( Exception T ) {
			CastErrors( T );
		}

	}

	
	public static int countOccurrences( String haystack, String needle ) {
		
		//System.out.println( "countOccurrences "+needle );
	    int count = 0;
	    int lastIndex = haystack.indexOf( needle, 0 );
	    //System.out.println( needle +" "+haystack.indexOf( needle, lastIndex ) );

	    while ( lastIndex != -1 ){
	    	//System.out.println( "lastIndex "+lastIndex );
	    	haystack = haystack.substring( (lastIndex+needle.length()), haystack.length() );
	    	lastIndex = haystack.indexOf( needle, 0 );

	    	if( lastIndex != -1){
	    		count ++;
	    	}
	    }
	    return count;
	}


	public void TimePast( ) {
		if ( connection != null ) {
			connection.disconnect();
		}
	}
	
	private void CastErrors( Exception T ) {
		System.err.println("Thread_LinkScanner");
		System.err.println ( "Throwable message: " + T.getMessage ( ) );
		System.err.println ( "Throwable cause: " + T.getCause ( ) );
		System.err.println ( "Throwable class: " + T.getClass ( ) );
		
		System.err.println ( "Origin stack "+0+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[0].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[0].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[0].getLineNumber ( ) );
		
		System.err.println ( "Origin stack "+1+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[1].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[1].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[1].getLineNumber ( ) );
		
		/*for ( int y = 2 ; y < T.getStackTrace().length ; y++ ) {
			System.err.println (" ");
			System.err.println ( "Origin stack "+y+": ");
			System.err.println ( "Class: " + T.getStackTrace ( )[y].getClassName ( ) );
			System.err.println ( "Method: " + T.getStackTrace ( )[y].getMethodName ( ) );
			System.err.println ( "Line: " + T.getStackTrace ( )[y].getLineNumber ( ) );
		}*/
	}

}