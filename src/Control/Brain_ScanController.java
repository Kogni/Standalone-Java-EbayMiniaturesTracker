package Control;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import Objects.Object_TreeNode_Webpage;
import Objects.Object_Webpage;

public class Brain_ScanController {
	Controller Class_Controller;
	Object_TreeNode_Webpage FirstPageToSearch = null;
	Object_TreeNode_Webpage AllPagesSaved_First = null;
	int Auctionspage;
	int Auctionspage_Last;
	String AuctionsTrackURL = "";
	
	boolean Soeker = false;
	
	public Brain_ScanController( Controller Class_Controller ) {
		this.Class_Controller = Class_Controller;
	}
	
	private void StartAuctionsTracker( ) {
		if ( Soeker == false ) {
			//System.out.println( "-> StartAuctionsTracker" );
			Soeker = true;
			System.gc();
			Auctionspage ++;
			//System.out.println( "-> "+Auctionspage+" "+Auctionspage_Last );
			URL New = null;
			//bare gaming stuff:
			Auctionspage_Last = 700;
			//AuctionsTrackURL = "http://www.ebay.com/sch/Miniatures-War-Games-/16486/i.html?LH_PrefLoc=2&LH_Price=2..35%40c&_trkparms=65%253A12%257C66%253A4%257C39%253A1%257C72%253A5829&rt=nc&_catref=1&_mPrRngCbx=1&_trksid=p3286.c0.m14.l1513&_pgn="+Auctionspage;
			AuctionsTrackURL = "http://www.ebay.co.uk/sch/Wargames-RolePlaying-/16486/i.html?_sac=1&_from=R40&_nkw=&LH_PrefLoc=2&_pgn="+Auctionspage+"&_skc=200&rt=nc";
			//alt:
			//AuctionsTrackURL = "http://www.ebay.com/sch/i.html?LH_PayPal=1&LH_PrefLoc=2&_nkw=%28a%2C%20i%2C%20e%2C%20u%2C%20o%29&_trkparms=65%253A15%257C66%253A4%257C39%253A1&rt=nc&_stpos=&_trksid=p3286.c0.m14.l1513&gbr=1&_pgn="+Auctionspage;
			//Auctionspage_Last = 130000;
			Date Idag = new Date();
			System.out.println( Idag.getHours()+"."+Idag.getMinutes()+":"+Idag.getSeconds()+" Tracker auksjoner på Ebay..."+Auctionspage+"/"+Auctionspage_Last );
			
			try {
				New = new URL( AuctionsTrackURL );
			    URLConnection myURLConnection = New.openConnection();
			    myURLConnection.connect();
			    if ( New != null ) {
					Object_Webpage Temp = new Object_Webpage( New, 0, 0 );
					//this.Class_Controller.AddProgressMessage( "Browsing all auctions on Ebay..."+Auctionspage+"/"+Auctionspage_Last );
					Class_Controller.SearchURL( Temp, this );
				}
			} catch ( MalformedURLException e ) {     // new URL() failed
				Auctionspage = 1;
			} catch ( Exception T ) {
				Auctionspage = 1;
				CastErrors( T );
			}
			//System.out.println( "-> "+New );
			
			Soeker = false;
		}
	}
	
	public void NewSearch( URL urlscanned ) {
		//System.out.println( "NewSearch" );
		Object_TreeNode_Webpage CurrentNode = FirstPageToSearch;
		if ( FirstPageToSearch == null ) {
			StartAuctionsTracker( );
			return;
		} else {
			if ( FirstPageToSearch.Left == null ) {
				if ( FirstPageToSearch.Right != null ) {
					FirstPageToSearch = FirstPageToSearch.Right;
				} else {
					FirstPageToSearch = null;
				}
				Class_Controller.SearchURL( CurrentNode.Link, this );
			} else {
				while ( CurrentNode.Left.Left != null ) {
					CurrentNode = CurrentNode.Left;
				}
				Object_TreeNode_Webpage Temp = CurrentNode.Left;
				CurrentNode.Left = null;
				Class_Controller.SearchURL( Temp.Link, this );
				return;
			}
		}
	}

	public void SaveAuction( String line, String Sender ) {
		Object_Webpage NewPage;
		try {
			NewPage = new Object_Webpage( new URL(line), 0, 0 );
		} catch ( MalformedURLException T ) {
			return;
		} catch ( Exception T ) {
			CastErrors( T );
			return;
		}
		if( SearchForURL( NewPage, AllPagesSaved_First ) == true ) {
			return;
		}
		SettAuksjonsprioritet( NewPage );
		//System.out.println( "SaveAuction Auksjonsprioritet="+NewPage.LinkedRelationValue );
		if ( NewPage.LinkedRelationValue <= 0 ) {
			if ( NewPage.Get_URL().toString().indexOf( "/itm/") > -1 ) { //url er til en uinteressant auksjon
				return;
			}
		}
		if ( NewPage.Get_URL().toString().indexOf( "/sch/") > -1 ) {
			if ( Sender.equals("SearchURL") == false ) { //alle tråder er opptatt, url må lagres
				Class_Controller.SearchURL( NewPage, this );
				return;
			}
		}
		if ( NewPage.Get_URL().toString().indexOf( "/sch/") > -1 ) {
			if ( NewPage.Get_URL().toString().indexOf( "/itm/") > -1 ) {
				if ( NewPage.Get_URL().toString().indexOf( "//stores.ebay") > -1 ) {
					if ( NewPage.Get_URL().toString().length() > "http://www.ebay.com/sch/".length() ) {
					
						return; //uinteressant url
					}
				}
			}
		}
		if ( FirstPageToSearch == null ) {
			FirstPageToSearch = new Object_TreeNode_Webpage ( NewPage );
			SaveURLToTotalList( line );
			return;
		} else {
			Object_TreeNode_Webpage CurrentNode = FirstPageToSearch;
			while ( true ) {
				if ( NewPage.LinkedRelationValue >= CurrentNode.Link.LinkedRelationValue ) {
					if ( CurrentNode.Left == null ) {
						CurrentNode.Left = new Object_TreeNode_Webpage ( NewPage );
						SaveURLToTotalList( line );
						return;
					} else {
						CurrentNode = CurrentNode.Left;
					}
				} else {
					if ( CurrentNode.Right == null ) {
						CurrentNode.Right = new Object_TreeNode_Webpage ( NewPage );
						SaveURLToTotalList( line );
						return;
					} else {
						CurrentNode = CurrentNode.Right;
					}
				}
			}
		}
	}

	public void SaveURLToTotalList( String line ) {
		Object_Webpage NewPage;
		try {
			NewPage = new Object_Webpage( new URL(line), 0, 0 );
		} catch ( MalformedURLException T ) {
			System.out.println( "URL rejected, ugyldig URL: "+line );
			return;
		} catch ( Exception T ) {
			CastErrors( T );
			System.out.println( "URL rejected B: "+line );
			return;
		}
		if( SearchForURL( NewPage, AllPagesSaved_First ) == true ) {
			return;
		}
		if ( AllPagesSaved_First == null ) {
			AllPagesSaved_First = new Object_TreeNode_Webpage ( NewPage );
			return;
		} else {
			Object_TreeNode_Webpage CurrentNode = AllPagesSaved_First;
			while ( true ) {
				if ( NewPage.Get_URL().toString().compareTo( CurrentNode.Link.Get_URL().toString() ) > 0 ) {
					if ( CurrentNode.Left == null ) {
						CurrentNode.Left = new Object_TreeNode_Webpage ( NewPage );
						return;
					} else {
						CurrentNode = CurrentNode.Left;
					}
				} else {
					if ( CurrentNode.Right == null ) {
						CurrentNode.Right = new Object_TreeNode_Webpage ( NewPage );
						return;
					} else {
						CurrentNode = CurrentNode.Right;
					}
				}
			}
		}
	}
	
	private boolean SearchForURL(  Object_Webpage newpage, Object_TreeNode_Webpage SearchStart ) {
		//System.out.println( "SearchForURL" );
		if ( SearchStart == null ) {
			return false;
		}
		String AuksjonsID_A = newpage.Get_URL().toString();
		String AuksjonsID_B = SearchStart.Link.Get_URL().toString();
		if ( AuksjonsID_A.indexOf("/sch/") == -1 ) {
			if ( AuksjonsID_B.indexOf("/sch/") == -1 ) {
				while ( AuksjonsID_A.indexOf("/") > -1) {
					AuksjonsID_A = AuksjonsID_A.substring( (AuksjonsID_A.indexOf("/")+1) );
				}
				while ( AuksjonsID_B.indexOf("/") > -1) {
					AuksjonsID_B = AuksjonsID_B.substring( (AuksjonsID_B.indexOf("/")+1) );
				}
				if ( AuksjonsID_A.equals( AuksjonsID_B ) ) {
					return true;
				}
			}
		} else {
			if ( newpage.Get_URL().toString().compareTo( SearchStart.Link.Get_URL().toString() ) == 0 ) {
				return true;
			}
		}
		if ( newpage.Get_URL().toString().compareTo( SearchStart.Link.Get_URL().toString() ) > 0 ) {
			return SearchForURL(  newpage, SearchStart.Left );
		} else {
			return SearchForURL(  newpage, SearchStart.Right );
		}
	}
	
	private void SettAuksjonsprioritet( Object_Webpage Auksjon ) {
		Auksjon.LinkedRelationValue = Class_Controller.VurderAuksjonsPrioritet( Auksjon );
	}
	
	public int Get_TotalAuctionCount() {
		return CountAuctions( AllPagesSaved_First );
	}
	
	private int CountAuctions( Object_TreeNode_Webpage CurrentNode ) {
		if ( CurrentNode == null ) {
			return 0;
		} else {
			return 1 + CountAuctions( CurrentNode.Left ) + CountAuctions( CurrentNode.Right );
		}
	}

	public int Get_AuctionsScanned() {
		int All = Get_TotalAuctionCount();
		int Unscanned = CountAuctions( FirstPageToSearch );
		return All-Unscanned;
	}
	
	private void CastErrors( Exception T ) {
		System.err.println( this.getClass().getName() );
		System.err.println( T );
		
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
