package Threads;

import Control.Controller;

public class Thread_PriceSaver extends Thread {
	
	Controller Class_Controller;
	Thread_LinkScanner Owner;
	long ThreadstartTime = System.currentTimeMillis();
	String Data_Name, Data_Price, Data_Shipping;
	String URLsource;
	
	boolean Busy = false;
	
	public Thread_PriceSaver( Controller Class_Controller, String Data, Thread_LinkScanner Owner, String URLsource ) {
		//System.out.println ( System.currentTimeMillis()+" +Thread_PriceSaver created: "+this.getName()+" "+URLsource );
		
		this.Class_Controller = Class_Controller;
		this.Owner = Owner;
		this.Data_Name = Data;
		this.Data_Price = Data;
		this.Data_Shipping = Data;
		this.URLsource = URLsource;
		try {
			//run();
		} catch ( Exception T ) {
			CastErrors( T );
		}
	}

/*	public void run( String Data, Thread_LinkScanner Owner, String URLsource ) {
		try {
			Busy = true;
			SplitLine1( Data, Owner, URLsource );
			Busy = false;
		} catch ( Exception T ) {
			CastErrors( T );
		}
		this.Class_Controller.TimeTick( "Thread_PriceSaver completed", null );

	}*/
	
	void SplitLine1( String Data, Thread_LinkScanner Owner, String URLsource ) {
		Busy = true;
		this.Data_Name = Data;
		this.Data_Price = Data;
		this.Data_Shipping = Data;
		this.URLsource = URLsource;
		
		if ( URLsource.substring( 0, "http://search.".length() ).equals( "http://search." ) ) {
			System.out.println ( System.currentTimeMillis()+" +Thread_PriceSaver rejection A: "+this.getName()+" "+URLsource );
			return;
		}	
		String Price_Temp = Data_Price;
		String Shipping_Temp = Data_Shipping;
		/*if ( Data_Price.indexOf( "pris inkl. forsendelse" ) == -1 ) {
			if ( Data_Price.indexOf( "artikkelpris" ) == -1 ) {
				//System.out.println ( System.currentTimeMillis()+" +Thread_PriceSaver rejection B: "+this.getName()+" "+URLsource );
				return;
			} else {
				Price_Temp = Data_Price.substring( Data_Price.indexOf("artikkelpris") );
			}
		} else {
			Price_Temp = Data_Price.substring( Data_Price.indexOf("pris inkl. forsendelse") );
		}*/
		try {
			String SearchString = ">us $";
			if ( Price_Temp.indexOf( SearchString.toLowerCase() ) == -1 ) {
				if ( Price_Temp.indexOf( SearchString.toLowerCase() ) == -1 ) {
					//System.out.println ( System.currentTimeMillis()+" +Thread_PriceSaver rejection B: "+this.getName()+" "+URLsource );
					return;
				} else {
					Price_Temp = Price_Temp.substring( Price_Temp.indexOf( SearchString.toLowerCase() )+SearchString.toLowerCase().length() );
				}
			} else {
				Price_Temp = Price_Temp.substring( Price_Temp.indexOf( SearchString.toLowerCase() )+SearchString.toLowerCase().length() );
			}
			SearchString = "class=\"sh-tblcnt\"><div role=\"alert\">us $";//for US auksjoner
			if ( Shipping_Temp.indexOf( SearchString.toLowerCase() ) == -1 ) {
				SearchString = "srvCost\">(approx. <span id=\"v4-158\">US $"; //for GB auksjoner
				if ( Shipping_Temp.indexOf( SearchString.toLowerCase() ) == -1 ) {
					Shipping_Temp = "";
				} else {
					Shipping_Temp = Shipping_Temp.substring( Shipping_Temp.indexOf( SearchString.toLowerCase() )+SearchString.toLowerCase().length() );
				}
				Shipping_Temp = "";
			} else {
				Shipping_Temp = Shipping_Temp.substring( Shipping_Temp.indexOf( SearchString.toLowerCase() )+SearchString.toLowerCase().length() );
			}
		} catch ( Exception T ) {
			CastErrors( T );
		}

		
		//System.out.println( "Thread_PriceSaver SplitLine1 "+Data_Price.indexOf("artikkelpris") );
		
		Price_Temp = OnlyPrice( Price_Temp ); //<-sletter strings som ikke inneholder noen url, og returnerer f.o.m http dersom stringen inneholder en url
		Shipping_Temp = OnlyPrice( Shipping_Temp );
		//System.out.println( "Thread_PriceSaver SplitLine1 A "+Price_Temp );
		Price_Temp = CleanRightofPrice( Price_Temp ); // <-Sletter alt på høyresiden av Frase, som ikke er en del av url.
		Shipping_Temp = CleanRightofPrice( Shipping_Temp );
		//System.out.println( "Thread_PriceSaver SplitLine1 B "+Price_Temp );
		Double Price = 0.0;
		if ( Price == 0 ) {
			try {
				Price = Double.parseDouble(Price_Temp);
			} catch ( NumberFormatException E) {
				//System.out.println( "Fant ingen pris: "+Price_Temp );
			}
		}
		Double Shipping = 0.0;
		if ( Shipping == 0 ) {
			try {
				Shipping = Double.parseDouble(Shipping_Temp);
				try {
					Price = Price + Shipping;
				} catch ( NumberFormatException E) {
					//System.out.println( "Fant ingen pris: "+Price_Temp );
				}
			} catch ( NumberFormatException E) {
				//System.out.println( "Fant ingen shippingpris: "+Shipping_Temp );
			}
		}

		String Name = "";
		String Name_Temp = Data_Name;
		Name_Temp = OnlyName( Name_Temp ); //<-sletter strings som ikke inneholder noen url, og returnerer f.o.m http dersom stringen inneholder en url
		//System.out.println( "Thread_PriceSaver A "+Name_Temp );
		Name_Temp = CleanRightofName( Name_Temp ); // <-Sletter alt på høyresiden av Frase, som ikke er en del av url.
		//System.out.println( "Thread_PriceSaver B "+Name_Temp );
		if ( Name.equals("") ) {
			Name = Name_Temp;
		}
		
		String Beskrivelse = "";
		String Descr_Temp = Data_Name;
		String Letestreng = "<div id=\"desc_div\">";
		if ( Descr_Temp.indexOf( Letestreng ) > -1 ) {
			Descr_Temp = Descr_Temp.substring( Descr_Temp.indexOf( Letestreng )+Letestreng.length() );
		}
		Descr_Temp = Descr_Temp.substring( Descr_Temp.indexOf( "<div>" ) );
		Descr_Temp = Descr_Temp.substring( 0, Descr_Temp.indexOf( "</div>" ) );
		Beskrivelse = Descr_Temp;
		
		if ( Price > 0 ) { //<- betyr at Frase inneholder en url
			if ( Name.equals("") ) {
				System.out.println( this.getClass().toString()+" Ugyldig navn: "+Name_Temp+" fra "+URLsource );
			} else {
				//System.out.println( this.getClass().toString()+"Ber om å lagre pris: "+Name+" "+Beskrivelse+" "+Price+" "+URLsource );
				//System.out.println( "Thread_PriceSaver opprinnelig data: "+Name+"->"+Data );
				this.Class_Controller.SavePrice( Name, Price, URLsource, Beskrivelse, this ); //String Name, double Price, String Source, String URL, String Beskrivelse
			}
			
		} else {
			//System.out.println( "Ugyldig pris: "+Price_Temp+" fra "+URLsource );
		}
		Data_Name = "";
		Data_Price = "";
		
		Busy = false;
	}
	
	private String CleanRightofName( String source ) {
		
		String Letestreng = "\"";
		if ( source.indexOf( Letestreng ) == -1 ) {
		} else {
			source = source.substring( 0, source.indexOf( Letestreng ));
		}
		
		return source;
	}

	private String OnlyName( String name_Temp ) {

		String Letestreng = "og:title\" content=\"";
		if ( name_Temp.indexOf( Letestreng ) > -1 ) {
			String Sub = name_Temp.substring( name_Temp.indexOf( Letestreng )+Letestreng.length() );
			return Sub+" ";
		}

		return "";
	}

	private String OnlyPrice( String source ) {

		String Letestreng = "</span>";
		
		int idx = source.indexOf( Letestreng );
		if ( idx > -1 ) {
			String Sub = source.substring( 0, idx );
			return Sub+" ";
		}

		return "";
	}

	private String CleanRightofPrice( String source ) {
		if ( source.indexOf( "</span><span id=\"sellerTotalPriceDivId\"" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "</span><span id=\"sellerTotalPriceDivId\"" ));
		}
		
		if ( source.indexOf( "," ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "," ));
		}

		if ( source.indexOf( ";" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( ";" ));
		}
		
		if ( source.indexOf( "?" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "?" ));
		}
		
		if ( source.indexOf( "\"" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "\"" ));
		}
		
		if ( source.indexOf( "#" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "#" ));
		}
		
		if ( source.indexOf( " " ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( " " ));
		}
		
		if ( source.indexOf( "///" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "///" ));
		}
		
		if ( source.indexOf( "=" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "=" ));
		}
		
		if ( source.indexOf( ")" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( ")" ));
		}
		
		if ( source.indexOf( "<" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "<" ));
		}
		
		if ( source.indexOf( ">" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( ">" ));
		}
		
		if ( source.indexOf( "'" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "'" ));
		}
		
		if ( source.indexOf( "&" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "&" ));
		}
		
		if ( source.indexOf( ".." ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( ".." ));
		}
		
		if ( source.indexOf( "(" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "(" ));
		}
		
		if ( source.indexOf( ")" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( ")" ));
		}
		
		if ( source.indexOf( "[" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "[" ));
		}
		
		if ( source.indexOf( "]" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "]" ));
		}
		
		return source;
	}
	
	private void CastErrors( Exception T ) {
		System.err.println("Thread_PriceSaver");
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
		
		System.err.println ( "Origin stack "+2+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[2].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[2].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[2].getLineNumber ( ) );
		
		/*for ( int y = 2 ; y < T.getStackTrace().length ; y++ ) {
			System.err.println (" ");
			System.err.println ( "Origin stack "+y+": ");
			System.err.println ( "Class: " + T.getStackTrace ( )[y].getClassName ( ) );
			System.err.println ( "Method: " + T.getStackTrace ( )[y].getMethodName ( ) );
			System.err.println ( "Line: " + T.getStackTrace ( )[y].getLineNumber ( ) );
		}*/
	}
}