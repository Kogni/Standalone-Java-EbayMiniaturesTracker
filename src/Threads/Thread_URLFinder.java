package Threads;

import java.util.StringTokenizer;
import Control.Controller;

public class Thread_URLFinder extends Thread {
	
	Controller Class_Controller;
	Thread_LinkScanner Owner;
	long ThreadstartTime = System.currentTimeMillis();
	String Data;
	String URLsource;
	
	boolean Busy = false;
	
	public Thread_URLFinder( Controller Class_Controller, String Data, Thread_LinkScanner Owner, String URLsource ) {
		//System.out.println ( System.currentTimeMillis()+": Thread_URLFinder init" );
		//System.out.println ( System.currentTimeMillis()+" +Thread_URLFinder created: "+this.getName() );
		
		this.Class_Controller = Class_Controller;
		this.Owner = Owner;
		this.Data = Data;
		this.URLsource = URLsource;
	}

	public void run() {
		try {
			Busy = true;
			SplitLine1( Data );
			Busy = false;
			//long ThreadendTime = System.currentTimeMillis();
			//System.out.println("Time (ms) : " + (ThreadendTime - ThreadstartTime));
		} catch ( Exception T ) {
			CastErrors( T );
		}
		//this.Class_Controller.TimeTick( "Thread_URLFinder completed" );
		//System.out.println(System.currentTimeMillis()+"- Thread_DataCleaner finished: "+this.getName());
		/*if ( URLsource.equals( "http://www.google.no/search?q=miniatyr")) {
			System.out.println(System.currentTimeMillis()+"- Thread_URLFinder finished: "+this.getName());
		}*/
	}
	
	private void SplitLine1( String Pagecontent ) {
		//System.out.println( "SplitLine1 started" );
		StringTokenizer token = new StringTokenizer(Pagecontent);
		int count = 0;
		String Frase;
		while ( token.hasMoreTokens() ){
			Frase = token.nextToken(); //<- Henter 1 og 1 string fra pagecontent
			//System.out.println("Tokens left: "+token.countTokens());
			//Line = CleanHTML( Line );
			//System.out.println( "0 "+Frase );
			Frase = OnlyURLS( Frase ); //<-sletter strings som ikke inneholder noen url, og returnerer f.o.m http dersom stringen inneholder en url
			//System.out.println( "A "+Frase );
			//String Temp1 = Frase;
			Frase = CleanRightofURL( Frase ); // <-Sletter alt på høyresiden av Frase, som ikke er en del av url.
			//System.out.println( "B "+Frase );
			//String Temp2 = Frase;
			Frase = OnlyURLS( Frase );
			Frase.replaceAll(" ", "");
			//System.out.println( "C "+Frase );
			if ( Frase.indexOf( "http://" ) > -1 ) { //<- betyr at Frase inneholder en url
				count++;
				//System.out.println( "Thread fant URL" );
				this.Class_Controller.SaveURL( Frase, "Thread searching", Owner.RelationValue );

			}
		}

		//System.out.println( "SplitLine1 finished " );
	}
	
	private String OnlyURLS( String source ) {
		int idx = source.indexOf( "http://" );
		if ( idx == -1 ) {
			return "";
		} else {
			String Sub = source.substring(idx, source.length());
			//Sub = CleanRightofURL( Sub );
			Sub = IgnoreURLS( Sub );
			return Sub+" ";
		}
	}
	
	private String IgnoreURLS( String source ) {
		
		if ( source.equals("http:") ) {
			source = "";
			return source;
		}
		if ( source.indexOf( "ebay" ) == -1 ) {
			source = "";
			return source;
		}
		/*if ( source.indexOf( "viewitem" ) == -1 ) { //brukes kun på norsk ebay
			source = "";
			return source;
		}*/
		if ( source.indexOf( "/itm/" ) == -1 ) { //brukes kun på amerikansk ebay
			if ( source.indexOf( "/sch/" ) == -1 ) { //brukes kun på amerikansk ebay
				if ( source.indexOf( "store" ) == -1 ) { //brukes kun på amerikansk ebay
					source = "";
					return source;
				}
			}
		}
		
		if ( source.indexOf( "http://pics.ebaystatic.com" ) == -1 ) {
		} else {
			source = "";
			return source;
		}
		if ( source.indexOf( "http://pages.ebay." ) == -1 ) {
		} else {
			source = "";
			return source;
		}
		if ( source.indexOf( "classified" ) == -1 ) {
		} else {
			source = "";
			return source;
		}
		
		if ( source.indexOf( ":80" ) == -1 ) {
		} else {
			source = "";
			return source;
		}
		
		if ( source.indexOf( ".png" ) == -1 ) {
		} else {
			if ( source.indexOf( ".png" ) == (source.length()-".png".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".gif" ) == -1 ) {
		} else {
			if ( source.indexOf( ".gif" ) == (source.length()-".gif".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".jpg" ) == -1 ) {
		} else {
			if ( source.indexOf( ".jpg" ) == (source.length()-".jpg".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".css" ) == -1 ) {
		} else {
			if ( source.indexOf( ".css" ) == (source.length()-".css".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".js" ) == -1 ) {
		} else {
			if ( source.indexOf( ".js" ) == (source.length()-".js".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".rdf" ) == -1 ) {
		} else {
			if ( source.indexOf( ".rdf" ) == (source.length()-".rdf".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".pdf" ) == -1 ) {
		} else {
			if ( source.indexOf( ".pdf" ) == (source.length()-".pdf".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".ico" ) == -1 ) {
		} else {
			if ( source.indexOf( ".ico" ) == (source.length()-".ico".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".php" ) == -1 ) {
		} else {
			if ( source.indexOf( ".php" ) == (source.length()-".php".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".php4" ) == -1 ) {
		} else {
			if ( source.indexOf( ".php4" ) == (source.length()-".php4".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".py" ) == -1 ) {
		} else {
			if ( source.indexOf( ".py" ) == (source.length()-".py".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}

		if ( source.indexOf( ".dtd" ) == -1 ) {
		} else {
			if ( source.indexOf( ".dtd" ) == (source.length()-".dtd".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".epl" ) == -1 ) {
		} else {
			if ( source.indexOf( ".epl" ) == (source.length()-".epl".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".jspa" ) == -1 ) {
		} else {
			if ( source.indexOf( ".jspa" ) == (source.length()-".jspa".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}

		if ( source.indexOf( ".txt" ) == -1 ) {
		} else {
			if ( source.indexOf( ".txt" ) == (source.length()-".txt".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".swf" ) == -1 ) {
		} else {
			if ( source.indexOf( ".swf" ) == (source.length()-".swf".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".asp" ) == -1 ) {
		} else {
			if ( source.indexOf( ".asp" ) == (source.length()-".asp".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".aspx" ) == -1 ) {
		} else {
			if ( source.indexOf( ".aspx" ) == (source.length()-".aspx".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".mil" ) == -1 ) {
		} else {
			if ( source.indexOf( ".mil" ) == (source.length()-".mil".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".mvc" ) == -1 ) {
		} else {
			if ( source.indexOf( ".mvc" ) == (source.length()-".mvc".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".g" ) == -1 ) {
		} else {
			if ( source.indexOf( ".g" ) == (source.length()-".g".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".shtml" ) == -1 ) {
		} else {
			if ( source.indexOf( ".shtml" ) == (source.length()-".shtml".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		if ( source.indexOf( ".mv" ) == -1 ) {
		} else {
			if ( source.indexOf( ".mv" ) == (source.length()-".mv".length()) ) {
				source = "";
				return source;
			} else {
				
			}
		}
		
		return source;
	}
	
	
	private String CleanRightofURL( String source ) {
		
		if ( source.indexOf( "index.en-us.html" ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( "index.en-us.html" ));
		}
		
		if ( source.indexOf( ":" ) <= 5 ) {
		} else {
			source = source.substring(0, source.indexOf( ":" ));
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
		
		if ( source.indexOf( " " ) == -1 ) {
		} else {
			source = source.substring(0, source.indexOf( " " ));
		}
		/*
		idx = source.lastIndexOf("/");
		String Temp = "http://";
		if ( idx == -1 ) {
		} else {
			if ( idx > Temp.length() ) {
				source = source.substring(0, idx);
			} else {
				
			}
		}*/
		
		return source;
	}
	
	private void CastErrors( Exception T ) {
		System.err.println("Thread_URLFinder");
		System.err.println ( "Throwable message: " + T.getMessage ( ) );
		System.err.println ( "Throwable cause: " + T.getCause ( ) );
		System.err.println ( "Throwable class: " + T.getClass ( ) );
		
		System.err.println ( "Origin stack "+1+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[0].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[0].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[0].getLineNumber ( ) );
		
		System.err.println ( "Origin stack "+1+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[1].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[1].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[1].getLineNumber ( ) );
		/*
		for ( int y = 2 ; y < T.getStackTrace().length ; y++ ) {
			System.err.println (" ");
			System.err.println ( "Origin stack "+y+": ");
			System.err.println ( "Class: " + T.getStackTrace ( )[y].getClassName ( ) );
			System.err.println ( "Method: " + T.getStackTrace ( )[y].getMethodName ( ) );
			System.err.println ( "Line: " + T.getStackTrace ( )[y].getLineNumber ( ) );
		}*/
	}
}