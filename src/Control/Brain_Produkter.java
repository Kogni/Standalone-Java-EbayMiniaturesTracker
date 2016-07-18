package Control;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import Objects.Object_Offer;
import Objects.Object_Ord;
import Objects.Object_Produkt;
import Objects.Object_Webpage;

public class Brain_Produkter {

	Controller Class_Controller;
	public Object_Produkt[] Produktliste;
	
	int NumberOfDetailsToSearch = 0;
	
	boolean Soeker = false;
	String[] DetailsToSearch;
	boolean SamlerDetaljer = false;
	private int SoekeID = 0;
	
	String ProductsFilename = "Products.txt";
	File ProductsFile = new File ( ProductsFilename );
	
	Brain_Produkter( Controller Class_Controller ){
		
		this.Class_Controller = Class_Controller;
		
		Produktliste = new Object_Produkt[999];
		
		LoadProducts();
		
	}
	
	public void LoadProducts() {
		try {
			
			//this.Class_Controller.AddProgressMessage( "Loading search list" );
			if ( !ProductsFile.exists() ) {
				ProductsFile.createNewFile();
			}
			
			FileInputStream fstream2 = new FileInputStream ( ProductsFile );
			DataInputStream in2 = new DataInputStream ( fstream2 );
			while ( in2.available() > 0 ) {
				String Linje = in2.readLine();
				String Product = "";
				String Producer = "";
				String Details = "";
				double Price = 9999.0;
				int IndexProducer = Linje.indexOf("#");
				try { //blir error her hvis det er en tom linje i filen
					//producer = 0 -> #
					Producer = Linje.substring(0, IndexProducer);
					
					//product = producer# -> #details
					Linje = Linje.substring((IndexProducer+1));
					int IndexDetails = Linje.indexOf("#");
					Product = Linje.substring(0, IndexDetails);
					
					//details = product# -> #price
					Linje = Linje.substring((IndexDetails+1));
					int IndexPrice = Linje.indexOf("#");
					Details = Linje.substring(0, IndexPrice);
					
					//price = # -> 
					Linje = Linje.substring((IndexPrice+1));
					try {
						Price = Double.parseDouble( Linje );
					} catch ( NumberFormatException E) {
						CastErrors( E );
					}
					
					InsertNewProduct( Product, Producer, Details, Price, "LoadProducts" );
				} catch ( Exception T) {
					//CastErrors( T );
				}
			}
			
			//this.Class_Controller.AddProgressMessage( "Ferdig å loade produkter fra fil" );
			
			in2.close ( );
			in2 = null;
	        System.gc();
	        //this.Class_Controller.AddProgressMessage( "Loaded search list." );
	        SaveToFile( );
		} catch ( Exception T) {
			CastErrors( T );
		}
	}

	public void InsertNewProduct( String Name, String Producer, String Details, double Price, String Kilde ) {
		Name = Name.toLowerCase();
		Producer = Producer.toLowerCase();
		Details = Details.toLowerCase();
		if ( Producer.indexOf("_") > -1 ) {
			return;
		}
		//omdanner string med detaljer til array
		String[] DetailsArray = new String[40];;
		String Detalj = null;
		while ( Details.length() > ", ".length() ){
			if ( Details.indexOf(", ") > -1 ) {
				Detalj = Details.substring(0, Details.indexOf(", ") );
				for ( int A = 0 ; A < DetailsArray.length; A++ ) {
					if ( DetailsArray[A] == null ) {
						DetailsArray[A] = Detalj;
						//System.out.println( A+" "+DetailsArray[A] );
						A = DetailsArray.length;
					}
				}
				Details = Details.substring( (Details.indexOf(", ")+", ".length()) );
			} else {
				Detalj = Details;
				for ( int A = 0 ; A < DetailsArray.length; A++ ) {
					if ( DetailsArray[A] == null ) {
						DetailsArray[A] = Detalj;
						//System.out.println( A+" "+DetailsArray[A] );
						A = DetailsArray.length;
					}
				}
				Details = "";
			}
		}

		//legger inn item
		for ( int x = 0; x < Produktliste.length ; x++ ) {
			if ( Produktliste[x] == null ) {
				Produktliste[x] = new Object_Produkt( Name, Producer, DetailsArray, Price );
				//if ( this.Class_Controller.ReadyToSearch == true ) {
					//System.out.println( "Addet "+Name+" fra "+Producer+" Details="+Produktliste[x].DetailsToString()+" kilde="+Kilde );
				//}
				SaveToFile(  );
				return;
			} else if ( Produktliste[x].OfficialName.equals( Name )) {
				if ( Produktliste[x].Producer.equals( Producer )) {
					Produktliste[x].Details = DetailsArray;
					Produktliste[x].MaxPrice = Price;
					return;
				}
			}
		}
		
		System.out.println( "Brain_Produkter ERROR: Ikke plass til flere produkter i array!" );
		return;
	}
	
	private void SaveToFile( ) {

		try {
			boolean success = ProductsFile.delete();
			if ( success == false ) {
				return;
			}
			System.out.println( "delete file success? "+success );
			/*String ProductsFilename2 = "Products2.txt";
			File ProductsFile2 = new File ( ProductsFilename2 );*/
			ProductsFile.createNewFile();
			
			PrintStream utfil;
			FileOutputStream appendFilen = new FileOutputStream ( ProductsFile, true );
			utfil = new PrintStream ( appendFilen );

			for ( int A = 0 ; A < Produktliste.length ; A++ ) {
				if ( Produktliste[A] != null ) {
					//utfil.println( Produktliste[A].OfficialName+"#"+Produktliste[A].Producer+"#"+Produktliste[A].DetailsToString() );
					utfil.println( Produktliste[A].Producer+"#"+Produktliste[A].OfficialName+"#"+Produktliste[A].DetailsToString()+"#"+Produktliste[A].MaxPrice );
				}
			}

			utfil.close ( );
			utfil = null;
	        System.gc();

			//this.Class_Controller.AddProgressMessage( "New product saved to searchlist file." );
		} catch ( IOException T ) {
			CastErrors( T );
			if ( T.getMessage ( ).equals("Access is denied")) {
				SaveToFile(  );
			} else {
				CastErrors( T );
			}
		} catch ( Exception T ) {
			CastErrors( T );
		}
	}
	
	private String[] InsertDetail( String[] Details, String Detail ) {
		Detail = Detail.toLowerCase();
		if ( Detail.length() == 0 ) {
			return Details;
		}
		//System.out.println( "InsertDetail "+Detail );
		
		for ( int x = 0; x < Details.length ; x++ ) {
			if ( Details[x] == null ) {
				Details[x] = Detail;
				return Details;
			} else if ( Details[x].equals( Detail )) {
				return Details;
			}
		}
		System.out.println( "Brain_Produkter ERROR: Ikke plass til flere detaljer i array!" );
		return Details;
	}
	
	public void NextSearch() {
		DetailsToSearch = GetDetailsToSearch();
		//System.out.println( "NextSearch CanSearch()="+Class_Controller.CanSearch()+" SearchOrdliste_Complete="+Class_Controller.SearchOrdliste_Complete+" SoekeID="+SoekeID+" DetailsToSearch.length="+NumberOfDetailsToSearch );
		if ( Class_Controller.CanSearch() == true ) {
			if ( Class_Controller.SearchOrdliste_Complete == true ) {
				Class_Controller.SearchOrdliste_Complete = false;
			}
			
			if ( SoekeID >= NumberOfDetailsToSearch ) {
				Class_Controller.SearchOrdliste_Complete = true;
				//System.out.println( "RESETTER SOEKEID A" );
				SoekeID = 0;
			} else {
				Class_Controller.SearchOrdliste_Complete = false;
				SearchOrdliste();
			}
		} else {
			Class_Controller.SearchOrdliste_Complete = true;
			//System.out.println( "RESETTER SOEKEID B" );
			SoekeID = 0;
		}
	}
	
	private void SearchOrdliste() {
		if ( DetailsToSearch != null ) {
			if ( Soeker == false ) {
				Soeker = true;
				SoekeID ++;
				URL New = null;
				//String Adresse = "http://search.eim.ebay.no/?kw="+DetailsToSearch[x]+"&ect=&elc=-2&eb=S%C3%B8k";
				//String Adresse = "http://www.ebay.com/sch/Toys-Hobbies-/220/i.html?LH_PrefLoc=2&LH_Price=2..35%40c&_nkw=%22"+DetailsToSearch[SoekeID]+"%22&_trkparms=65%253A12%257C66%253A2%257C39%253A1%257C72%253A5829&rt=nc&_catref=1&_ipg=200&_mPrRngCbx=1&_trksid=p3286.c0.m14.l1514";
				String Adresse = "http://www.ebay.com/sch/i.html?LH_PayPal=1&LH_PrefLoc=2&_trkparms=65%253A15%257C66%253A4%257C39%253A1&rt=nc&_nkw="+DetailsToSearch[SoekeID]+"&_stpos=&_trksid=p3286.c0.m14.l1513&gbr=1&_pgn=2";
				Adresse = Adresse.replaceAll(" ", "+");
				try {
					New = new URL( Adresse );
					URLConnection myURLConnection = New.openConnection();
					myURLConnection.connect();
				} catch ( MalformedURLException e ) {     // new URL() failed
				} catch ( Exception T ) {
					CastErrors( T );
				}
			
				if ( New != null ) {
					//Class_Controller.SaveURL( New.toString(), "Ordbok", 999 );
					Object_Webpage Temp = new Object_Webpage( New, 0, 0 );
					//this.Class_Controller.AddProgressMessage( "Searching for phrase on Ebay: #"+SoekeID+" - "+DetailsToSearch[SoekeID] );
					Class_Controller.SearchURL( Temp, this );
					//Class_Controller.SearchURL( new Object_Webpage( New, 999, 999 ), this );
				} else {
					System.err.println( this.getClass().getName() );
				}
			Soeker = false;
			}
		}
	}
	
	private String[] GetDetailsToSearch() {
		if ( SamlerDetaljer == false ) {
			SamlerDetaljer = true;
			String[] DetailsToSearchTemp = new String[9999];
			int NumberOfDetailsToSearch_Temp = 0;
			
			for ( int A = 0; A < Produktliste.length ; A++ ) {
				if ( Produktliste[A] != null ) {
					//System.out.println( "GetDetailsToSearch "+Produktliste[A].OfficialName );
					DetailsToSearchTemp = InsertDetail( DetailsToSearchTemp, Produktliste[A].OfficialName );
					DetailsToSearchTemp = InsertDetail( DetailsToSearchTemp, Produktliste[A].Producer );
					for ( int B = 0 ; B < Produktliste[A].Details.length ; B++ ) {
						if ( Produktliste[A].Details[B] != null ) {
							if ( Produktliste[A].Details[B].indexOf( "-" ) == 0 ) {//detaljen skal IKKE være med
	
							} else {
								//System.out.println( "GetDetailsToSearch "+Produktliste[A].Details[B] );
								DetailsToSearchTemp = InsertDetail( DetailsToSearchTemp, Produktliste[A].Details[B] );
								NumberOfDetailsToSearch_Temp ++;
							}
						}
					}
				}
			}
			SamlerDetaljer = false;
			NumberOfDetailsToSearch = NumberOfDetailsToSearch_Temp;
			return DetailsToSearchTemp;
		}
		return DetailsToSearch;
		
	}
	
	public Object_Ord[] Get_Ordliste() {
		Object_Ord[] Ordliste = new Object_Ord[9999];
		String[] DetailsToSearch = GetDetailsToSearch();
		for ( int x = 0; x < DetailsToSearch.length ; x++ ) {
			if ( DetailsToSearch[x] != null ) {
				Ordliste[x] = new Object_Ord( DetailsToSearch[x], 10 );
			}
		}
		
		return Ordliste;
	}

	void SavePrice( String name, String beskrivelse, double price, String uRL, Object Kilde ) {//Name, Beskrivelse, Price, URL
		//System.out.println( this.getClass().toString()+" SavePrice A "+name+" "+uRL+" "+Kilde );
		//1. finn det mest sannsynlige produktet
		//Object_Produkt Produkt = null;
		Object_Offer NewOffer;
		try {
			NewOffer = new Object_Offer( new URL(uRL), price, name, beskrivelse );
		} catch (MalformedURLException e1) {
			System.out.println( this.getClass().toString()+" SavePrice A2 "+name+" "+uRL+" "+Kilde );
			return;
			//e1.printStackTrace();
		}
		//int Probability_Highest = 0;
		boolean Saved = false;
		for ( int A = 0 ; A < Produktliste.length ; A++ ) {
			if ( Produktliste[A] != null ) {
				//System.out.println( "SavePrice B "+name+" "+uRL );
				int ProbabilityA = 0;
				int ProbabilityB = 0;
				try {
					ProbabilityB = Produktliste[A].VurderRelevans( new URL( uRL ) );
				} catch (MalformedURLException e) {
					//e.printStackTrace();
				}
				//System.out.println( "SavePrice B "+name+" "+uRL+" ProbabilityB="+ProbabilityB );
				if ( ProbabilityB > 0 ) { //sjekker at URL ikke inneholder noen ord som utelukker objektet
					//System.out.println( "SavePrice B2 "+name+" "+uRL+" ProbabilityB="+ProbabilityB );
					ProbabilityA =  Produktliste[A].ConsiderDescription( NewOffer );
					NewOffer.HitPrcnt = ProbabilityA;
					//System.out.println( this.getClass().toString()+" SavePrice B3 "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
					if ( ProbabilityA > 1 ) {
						//System.out.println( this.getClass().toString()+" SavePrice B4 "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
						if ( ProbabilityA > 50 ) {
							//System.out.println( this.getClass().toString()+" SavePrice C "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
							if ( ProbabilityA >= 60 ) {
								//System.out.println( this.getClass().toString()+" SavePrice D "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
								if ( Produktliste[A].SaveOffer( NewOffer ) == true ) {
									Saved = true;
									//System.out.println( this.getClass().toString()+" SavePrice E "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
									this.Class_Controller.AddProgressMessage( "Offer saved: "+name+" (for "+Produktliste[A].GiveInfo() ) ;
								} else {
									//System.out.println( this.getClass().toString()+" SavePrice F "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
								}
							} else {
								//System.out.println( this.getClass().toString()+" SavePrice G "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
								SaveFailedAuctions( Produktliste[A].OfficialName, Produktliste[A].Producer, NewOffer, ProbabilityA );
							}
						} else {
							//System.out.println( this.getClass().toString()+" SavePrice H "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
							//SaveFailedAuctions( Produktliste[A].OfficialName, Produktliste[A].Producer, NewOffer, ProbabilityA );
						}
					} else {
						//System.out.println( this.getClass().toString()+" SavePrice I "+name+" "+uRL+" ProbabilityA="+ProbabilityA+" "+Produktliste[A].OfficialName );
					}
				}
			}
		}
		if ( Saved == false ) {
			System.out.println( this.getClass().toString()+" SavePrice J Rejected "+name+" "+uRL );
		}

	}
	
	private void SaveFailedAuctions( String Produktname, String producer, Object_Offer NewOffer, int Probability ) {

		try {
			Date Idag = new Date();
			DateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd");
			String Dato = formatter.format(Idag);

			File Folder = new File("Possible hits");
			if ( !Folder.exists() ) {
				Folder.mkdir();
			}
			
			String Filnavn = Dato+" Failed auction "+producer+" - "+Produktname+".txt";
			File FailedFile = new File ( Folder.getAbsolutePath()+"\\"+Filnavn );

			if (!FailedFile.getParentFile().exists())
				FailedFile.getParentFile().mkdirs();

			if ( !FailedFile.exists() ) {
				FailedFile.createNewFile();
			}
			
			PrintStream utfil;
			FileOutputStream appendFilen = new FileOutputStream ( FailedFile, true );
			utfil = new PrintStream ( appendFilen );

			utfil.println( Probability+"% hit: "+NewOffer.Name+" # "+NewOffer.Beskrivelse+" -> "+NewOffer.Url );

			utfil.close ( );
			//System.out.println( "Failed auction saved to file "+Filnavn );
		} catch ( IOException T ) {
			if ( T.getMessage ( ).equals("Access is denied")) {
				SaveFailedAuctions( Produktname, producer, NewOffer, Probability );
			} else {
				CastErrors( T );
			}
		} catch ( Exception T ) {
			CastErrors( T );
		}
	}
	
	public int VurderRelevans( Object_Webpage auksjon ) {
		
		int Relevans = 0;
		for ( int A = 0 ; A < Produktliste.length ; A++ ) {
			if ( Produktliste[A] != null ) {
				Relevans = Relevans + Produktliste[A].VurderRelevans( auksjon.Get_URL() );
			}
		}
		
		return Relevans;
	}
	
	public int Get_SoekeID() {
		return this.SoekeID;
	}

	public void Set_SoekeID( int i ) {
		SoekeID = i;
	}
	
	
	
	private void CastErrors( Exception T ) {
		System.err.println("Thread_LinkFinder");
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
