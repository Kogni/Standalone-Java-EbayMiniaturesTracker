package Control;

import java.net.URL;

import javax.swing.JFrame;

import GUI.Hovedvindu;
import Objects.*;
import Threads.Thread_LinkScanner;


public class Controller {

	Brain_ScanController Class_Brain_ScanController;

	public Brain_Produkter Class_Brain_Produkter;
	Brain_Currency Class_Brain_Currency;
	Hovedvindu Class_Hovedvindu;
	
	TimeKeeper Class_TimeKeeper;
	int TickInterval;
	Thread_LinkScanner[] SearchScanThreads;
	Thread_LinkScanner[] AuctionScanThreads;
	int ThreadsRunning = 0;
	int ThreadsStarted = 0;
	int ThreadsCompleted = 0;
	
	Object_Ordbok_Old OrdBok;
	
	boolean AllowedToSearch = false;
	boolean ReadyToSearch = false; //overstyrer alle søkemekanismer, ingenting får søke hvis denne er false
	boolean SearchComplete = true; //hvorvidt ordlistesøk og fullsøk har fullført sine siste søk
	boolean SearchOrdliste_Complete = true; //hvorvidt ordlistesøk har fullført sitt siste søk
	int CompletedSearches = 0;
	
	public int InterestBorder = 100;
	
	public Double Currency = 0.0;

	public Controller() {
		
		Class_Brain_Currency = new Brain_Currency(this);
		OrdBok = new Object_Ordbok_Miniatyrer();
		Class_Brain_ScanController = new Brain_ScanController( this );
		Class_Brain_Produkter = new Brain_Produkter( this );
		TickInterval = 100;
		Class_TimeKeeper = new TimeKeeper( this, TickInterval );
		AuctionScanThreads = new Thread_LinkScanner[2];
		SearchScanThreads = new Thread_LinkScanner[1];
		Class_Hovedvindu = new Hovedvindu( this );
		Class_Hovedvindu.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );

		Class_Hovedvindu.Startup ( );
		ReadyToSearch = true; //søkemekanismer får kjøre
		/*try {
			SearchURL( new Object_Webpage( new URL("http://viewitem.eim.ebay.no/gw-warhammer-chaos-beastman-dragon-ogre-shaggoth-body-metal-/250965550207/item"), 0, 0 ) , this);
		} catch (MalformedURLException e) {
		}*/

	}
	
	private boolean FigureSearchComplete() {
		SearchComplete = false;
		if ( ( Class_Brain_ScanController.FirstPageToSearch == null ) && ( Class_Brain_ScanController.Auctionspage_Last == 0 )) {//sjekker at forrige søk faktisk har startet
			
		} else {
			return false;
		}
		boolean FullSearchComplete = ( Class_Brain_ScanController.Auctionspage == Class_Brain_ScanController.Auctionspage_Last ) ;
		if ( SearchOrdliste_Complete && FullSearchComplete ) {
			SearchComplete = true;
			CompletedSearches ++;
		}
		//System.out.println( "FirstPageToSearch="+Class_Brain_ScanController.FirstPageToSearch );
		//System.out.println( "Auctionspage_Last="+Class_Brain_ScanController.Auctionspage_Last );
		//System.out.println( "Auctionspage="+Class_Brain_ScanController.Auctionspage );
		return SearchComplete;
	}
	
	public boolean CanSearch() {
		if ( AllowedToSearch && ReadyToSearch ) {
			return true;
		}
		return false;
	}
	
	public void UserCommand_StartSearching( String Kilde ) {
		StartSearching( Kilde );
	}
	
	public void StartSearching( String Kilde ) {
		AllowedToSearch = false;
		SetCurrentAction( "Started new search cycle" );
		//System.out.println( "* SearchOrdliste_Complete="+SearchOrdliste_Complete+" FigureSearchComplete="+FigureSearchComplete()+" AllowedToSearch="+AllowedToSearch+" SearchComplete="+SearchComplete+" ReadyToSearch="+ReadyToSearch );
		//System.out.println( "-> Started new search cycle "+Kilde );
		//AddProgressMessage( "Starting new search" );
		SearchOrdliste_Complete = false;
		Class_TimeKeeper = new TimeKeeper( this, TickInterval );
		Class_Brain_Produkter.Set_SoekeID( 0 );
		Class_Brain_ScanController.Auctionspage = 0;
		Class_Brain_Currency.GetCurrency();
		Class_Brain_Produkter.LoadProducts();
		Class_Brain_Produkter.NextSearch();
		Class_Brain_Produkter.NumberOfDetailsToSearch = 1000;
		Class_Brain_ScanController.Auctionspage = 0;
		//Class_Brain_ScanController.Auctionspage_Last = 9999;
		TimeTick( "", null );
		AllowedToSearch = true;
	}

	public void UserCommand_StopSearching() {
		AllowedToSearch = false;
		//ReadyToSearch = false; //ingen søkemekanismer får kjøre
		Class_TimeKeeper = null;
		SetCurrentAction( "Stopped searching" );
		AddProgressMessage( "Stopped search" );
	}
	
	public void SetCurrency( Double currency ) {
		this.Currency = currency;
		//AddProgressMessage( "US$->NOK currency is currently "+Currency );
	}

	public void TimeTick( Object Sender, URL urlscanned ) {
		
		if ( Class_TimeKeeper != Sender ) {
			return;
		}
		if ( urlscanned != null ) {
			//System.out.println( "TimeTick - ber om nytt søk "+Sender+" urlscanned="+urlscanned );
		}
		if ( CanSearch() == true ) {
			//System.out.println( "CanSearch()="+CanSearch()+" SearchOrdliste_Complete="+SearchOrdliste_Complete+" FigureSearchComplete="+FigureSearchComplete()+" AllowedToSearch="+AllowedToSearch+" SearchComplete="+SearchComplete+" ReadyToSearch="+ReadyToSearch );
			SetCurrentAction( "Figuring what to do next" );
			if ( FigureSearchComplete() ) {
				if ( ( Class_Brain_ScanController.FirstPageToSearch == null ) && ( Class_Brain_ScanController.Auctionspage_Last == 0 )) {//sjekker at forrige søk faktisk har startet
					Class_TimeKeeper = new TimeKeeper( this, TickInterval );
					StartSearching( "TimeTick<-"+Sender ); //restarter
				}
			}
			SetCurrentAction( "Issuing  phrase search" );
			Class_Brain_Produkter.NextSearch(); //lagrer URL'r til nye søk
			//System.out.println( "x SearchOrdliste_Complete="+SearchOrdliste_Complete+" FigureSearchComplete="+FigureSearchComplete()+" AllowedToSearch="+AllowedToSearch+" SearchComplete="+SearchComplete+" ReadyToSearch="+ReadyToSearch );
			//if ( CanSearch() == true ) {
				SetCurrentAction( "Issuing next URL scan" );
				Class_Brain_ScanController.NewSearch( urlscanned ); //scanner lagrede URLr til søk
				//if ( Class_Brain_ScanController.FirstPageToSearch == null ) {
					
					//Class_Brain_Produkter.NextSearch(); //lagrer URL'r til nye søk
				//}
			//}
		}
	}

	public void SearchURL( Object_Webpage temp, Object Avsender ) {
		//System.out.println( "SearchURL A "+temp.Get_URL()+" "+Avsender );
		
		if ( CanSearch() == false ) {
			return;
		}

		if ( temp == null ) {
			return;
		} else if ( temp.Get_URL().toString().equals("") ) {
			return;
		} 
		temp.Set_Searched();
		SetCurrentAction( "Scanning URL: "+temp.Get_URL().toString() );
		//System.out.println( "SearchURL B "+temp.Get_URL()+" "+Avsender );
		//AddProgressMessage( "Scanning "+temp.Get_URL().toString() );
		if ( temp.Get_URL().toString().indexOf( "/sch/") == -1 ) {
			for ( int A = 0 ; A < AuctionScanThreads.length ; A++ ) {
				if ( AuctionScanThreads[A] == null ) {
					//System.out.println( "Starter AuctionScanThreads#"+A);
					AuctionScanThreads[A] = new Thread_LinkScanner( this, temp );
					AuctionScanThreads[A].start();
					return;
				} else if ( AuctionScanThreads[A].Busy == false ) {
					//System.out.println( "Restarter AuctionScanThreads#"+A);
					AuctionScanThreads[A] = new Thread_LinkScanner( this, temp );
					AuctionScanThreads[A].start();
					return;
				}
			}
			//System.out.println( "Controller venter på at AuctionScanThreads skal bli ferdig" );
		} else {
			for ( int A = 0 ; A < SearchScanThreads.length ; A++ ) {
				if ( SearchScanThreads[A] == null ) {
					//System.out.println( "Starter SearchScanThreads#"+A);
					SearchScanThreads[A] = new Thread_LinkScanner( this, temp );
					SearchScanThreads[A].start();
					return;
				} else if ( SearchScanThreads[A].Busy == false ) {
					//System.out.println( "Restarter SearchScanThreads#"+A);
					SearchScanThreads[A] = new Thread_LinkScanner( this, temp );
					SearchScanThreads[A].start();
					return;
				}
			}	
			//System.out.println( "Controller venter på at SearchScanThreads skal bli ferdig" );
		}

		//alle er opptatt, lagre url igjen så den blir søkt senere
		//System.out.println( "Controller venter på at Thread_LinkScanner skal bli ferdig" );
		
		SaveURL( temp.Get_URL().toString(), "SearchURL", temp.LinkedRelationValue );
	}
	
	public void AddProgressMessage( String Message ) {
		if ( Class_Hovedvindu != null ) {
			this.Class_Hovedvindu.AddProgressMessage( Message );
			Class_Hovedvindu.Update();
		}
	}
	
	public void SetCurrentAction( String ActionText ) {
		if ( Class_Hovedvindu != null ) {
			Class_Hovedvindu.SetCurrentAction( ActionText ) ;
		}
	}
	
	public int GetSearchProgress() {
		//int searchPhrases = Class_Brain_Produkter.Get_SoekeID();
		//int searchPages = Class_Brain_ScanController.Auctionspage;
		int AuctionsScanned = Class_Brain_ScanController.Get_AuctionsScanned();
		/*if ( searchPages > 0 ) {
			searchPhrases = Class_Brain_Produkter.NumberOfDetailsToSearch; //fordi phrasesearching fullfører før pagesearching
		}*/
		
		return AuctionsScanned;
	}

	public int GetMaxSearchProgress() {
		//int searchPhrases = Class_Brain_Produkter.NumberOfDetailsToSearch;
		//int searchPages = Class_Brain_ScanController.Auctionspage_Last;
		int TotalAuctionCount = Class_Brain_ScanController.Get_TotalAuctionCount();
		//System.out.println( "searchPhrases="+searchPhrases+" searchPages="+searchPages);
		return TotalAuctionCount;
	}

	public void SaveURL( String line, String Sender, int LinkedRelationValue ) {
		if ( line.equals("") ) {
			return;
		} else if ( line.equals("http:/") ) {
			return;
		} else if ( line.equals("http:") ) {
			return;
		}

		try {
			SetCurrentAction( "Saving URL for later: "+line );
			//System.out.println( "SaveURL "+line+" "+LinkedRelationValue+" "+Sender );
			//Class_Brain_PageManager.InsertLink( line, LinkedRelationValue, "controller.SaveURL" );
			Class_Brain_ScanController.SaveAuction( line, Sender );
			Class_Hovedvindu.Update();
			//TimeTick( "", null );
			//System.out.println( "SaveURL finished" );
		} catch ( Exception T ) {
			CastErrors( T );
		}
	}

	public Object_Ord[] GetOrdliste() {
		return Class_Brain_Produkter.Get_Ordliste();
		//return this.Class_Brain_Ordbok.Get_Ordliste();
	}

	public void SavePrice( String Name, double Price, String URL, String Beskrivelse, Object Avsender ) {
		SetCurrentAction( "Saving product price: "+Math.floor(Price*Currency)+" for "+Name );
		Name = Name.toLowerCase();
		URL = URL.toLowerCase();
		Beskrivelse = Beskrivelse.toLowerCase();
		
		//System.out.println( this.getClass().toString()+" SavePrice '"+Name+"' "+URL+" "+Beskrivelse+" "+Price+" "+" NOK="+(Price*Currency)+" Currency="+Currency );
		this.Class_Brain_Produkter.SavePrice( Name, Beskrivelse, Math.floor(Price*Currency), URL, Avsender );
		Class_Hovedvindu.Update();
	}

	/*public void SaveNewProduct( String Name, String Producer, String Details ) {
		Class_Brain_Produkter.InsertNewProduct( Name, Producer, Details, "user input" );
	}*/

	public int VurderAuksjonsPrioritet(Object_Webpage auksjon) {
		SetCurrentAction( "Evaluating URL: "+auksjon.Get_URL().toString() );
		return Class_Brain_Produkter.VurderRelevans( auksjon );
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
