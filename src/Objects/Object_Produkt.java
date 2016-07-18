package Objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Object_Produkt {

	public String OfficialName;
	public String Producer;
	public String[] Details;
	public double MaxPrice;
	public Object_Offer[] Offers;
	Object_Offer[] SortedOffers;
	
	public int NumberOfOffers = 0;
	
	public Object_Produkt( String OfficialName, String Producer, String[] Details, double price ){
		
		this.OfficialName = OfficialName;
		this.Producer = Producer;
		this.Details = Details;
		this.MaxPrice = price;
		
		Offers = new Object_Offer[500];
	}
	
	public String GiveInfo() {
		return OfficialName+", "+Producer+", "+DetailsToString();
	}
		
	public int VurderRelevans( URL AdresseURL ) {

		String Adresse = AdresseURL.toString();
		int Relevans = 0;

		if ( OfficialName.length() > 0 ) {
			if ( Adresse.indexOf( OfficialName ) > -1 ) {
				Relevans++;
			} else {
				if ( Adresse.indexOf( OfficialName.replaceAll(" ", "-") ) > -1 ) {
					Relevans++;
				}
			}
		}
		
		for ( int A = 0; A < Details.length ; A++ ) {
			if ( Details[A] != null ) {
				if ( Details[A].length() > 0 ) {
					if ( Adresse.indexOf( Details[A] ) > -1 ) {
						if ( Details[A].indexOf( "-" ) == 0 ) {//detaljen skal IKKE være med
							return 0;
						} else {
							Relevans++;
						}
					} else {
						if ( Adresse.indexOf( Details[A].replaceAll(" ", "-") ) > -1 ) {
							if ( Details[A].replaceAll(" ", "-").indexOf( "-" ) == 0 ) {//detaljen skal IKKE være med
								return 0;
							}
						}
					}
				}
			}
		}
		
		if ( Producer.length() > 0 ) {
			if ( Adresse.indexOf( Producer ) > -1 ) {
				Relevans++;
			} else {
				if ( Adresse.indexOf( Producer.replaceAll(" ", "-") ) > -1 ) {
					Relevans++;
				}
			}
		}
		if ( Relevans > 0 ) {
			//System.out.println( "Relevans="+Relevans+" "+GiveInfo()+" for "+AdresseURL.toString() );
		}
		return Relevans;
	}

	public int ConsiderDescription( Object_Offer Offer ) {
		
		String Info = Offer.Name.replaceAll(" ", "-")+" "+Offer.Beskrivelse.replaceAll(" ", "-");
		//String name = Offer.Name.replaceAll(" ", "-");
		//String beskrivelse = Offer.Beskrivelse.replaceAll(" ", "-");
		double Price = Offer.Price;
		//System.out.println( this.getClass().toString()+" ConsiderDescription A Offer.Beskrivelse="+Offer.Beskrivelse );
		
		Offer.MaxScore = 0;
		Offer.IDmatch = 0;
		Offer.NumberOfDetails = 0;
		Offer.DetaljScore = 0;
		
		if ( OfficialName.length() > 0 ) {
			Offer.MaxScore = Offer.MaxScore + 50;
			if ( Info.indexOf( OfficialName ) > -1 ) {
				Offer.IDmatch ++;
			} else {
				return 0;
			}
		}
		/*
		 * Feks:
		 * MaxScore = 50
		 * IDmatch = 0-1
		 */
		
		for ( int A = 0; A < Details.length ; A++ ) {
			if ( Details[A] != null ) {
				if ( Details[A].length() > 0 ) {

					if ( Details[A].indexOf( "-" ) == 0 ) {//detaljen skal IKKE være med
						//System.out.println( Details[A].substring(0)+" "+Details[A].substring(1) );
						if ( Info.indexOf( Details[A].substring(1) ) > -1 ) {
							return 0;
						} else if ( Info.indexOf( Details[A].replaceAll(" ", "-") ) > -1 ) {
							return 0;
						}
					} else {
						Offer.NumberOfDetails++;
						if ( Info.indexOf( Details[A] ) > -1 ) {
							Offer.DetaljScore++;
						} else if ( Info.indexOf( Details[A].replaceAll(" ", "-") ) > -1 ) {
							Offer.DetaljScore++;
						}
						
					}
				
				}
			}
		}
		/*
		 * Feks:
		 * MaxScore = 50
		 * IDmatch = 0-1
		 * DetaljScore = detaljer
		 */
		
		if ( Producer.length() > 0 ) {
			Offer.MaxScore = Offer.MaxScore + 50;
			if ( Info.indexOf( Producer ) > -1 ) {
					Offer.IDmatch ++;
			} else {
				return 0;
			}
		}
		/*
		 * Feks:
		 * MaxScore = 100
		 * IDmatch = 0-2
		 * DetaljScore = detaljer
		 */
		
		if ( Offer.NumberOfDetails > 0 ) {
			Offer.MaxScore = Offer.MaxScore + 50;
			//DetaljScore = (DetaljScore / NumberOfDetails) * 50;
			Offer.DetaljScore = Offer.DetaljScore * 10;
		}
		/*
		 * Feks:
		 * MaxScore = 150
		 * IDmatch = 0-2
		 * DetaljScore = detaljer
		 */
		Offer.IDmatch = Offer.IDmatch * 50;
		/*
		 * Feks:
		 * MaxScore = 150
		 * IDmatch = 0-100+(detaljer*50)
		 */
		//System.out.println( "MaxScore="+MaxScore+" IDmatch="+IDmatch+" DetaljScore="+DetaljScore+" ="+(((IDmatch+DetaljScore)*100)/MaxScore)+" "+GiveInfo() );
		//treffkriterier:
		//a) navn+producer matcher, ingen detaljer matcher. 100p. MaxScore = 100+. Return ((100+0)*100)/100=100
		//b) navn -eller- producer pluss alle detaljer matcher. 50+50p. MaxScore = 150+. Return ((50+20)*100)/150=47
		//c) ikke spesifisert navn eller producer. Alle detaljer. 50p. MaxScore = 50. Return = (20*100)/50=40
		//d) ingenting matcher. 0p. Maxscore = 150+. Return 0/100=
		//sjekker pris
		//System.out.println( Price+" "+this.MaxPrice );
		
		if ( Price <= this.MaxPrice ) {
			return ((( Offer.IDmatch + Offer.DetaljScore )*100)/Offer.MaxScore);
		} else {
			return ((( Offer.IDmatch + Offer.DetaljScore )*100)/Offer.MaxScore)/2;
		}
		
		//return (((IDmatch+DetaljScore)*100)/MaxScore);
		//return 100;
	}

	public boolean SaveOffer( Object_Offer NewOffer ) {
		
		String AuksjonsID_A;
		String AuksjonsID_B = NewOffer.Url.toString();
		if ( VurderRelevans( NewOffer.Url ) <= 0 ) {
			System.out.println( "Produkt nekter å ta imot pris for "+NewOffer.Url.toString()+" ("+GiveInfo()+")" );
			return false;
		} else {
			//sjekker at offer ikke er lagret fra før
			for ( int A = 0 ; A < Offers.length ; A++ ) {
				if ( Offers[A] == null ) {
					//System.out.println( uRL +" "+price+" lagres " );
					Offers[A] = NewOffer;
					NumberOfOffers ++;
					SaveToFile( Offers[A] );
					return true;
				} else if ( Offers[A].Url.equals( NewOffer.Url )) {
					//System.out.println( uRL +" duplicate" );
					return false;
				} else {
					AuksjonsID_A = Offers[A].Url.toString();
					while ( AuksjonsID_A.indexOf("/") > -1) {
						AuksjonsID_A = AuksjonsID_A.substring( (AuksjonsID_A.indexOf("/")+1) );
					}
					while ( AuksjonsID_B.indexOf("/") > -1) {
						AuksjonsID_B = AuksjonsID_B.substring( (AuksjonsID_B.indexOf("/")+1) );
					}
					if ( AuksjonsID_A.equals( AuksjonsID_B ) ) {
						return false;
					}
				}
			}
		}
		
		System.out.println( GiveInfo()+" har ikke plass i array til å lagre flere offers!" );
		return false;
	}
	
	private void SaveToFile( Object_Offer NewOffer ) {

		try {
			Date Idag = new Date();
			DateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd");
			String Dato = formatter.format(Idag);
			  

			File Folder = new File("Hits");
			if ( !Folder.exists() ) {
				Folder.mkdir();
			}
			/*String Filnavn = Dato+" "+Producer+" - "+OfficialName+".txt";
			File filen = new File ( Folder.getAbsolutePath()+"//"+Filnavn );
			if ( !filen.exists() ) {
				filen.createNewFile();
			}*/
			String Filnavn = Dato+" "+Producer+" - "+OfficialName+".txt";
			File filen = new File ( Folder.getAbsolutePath()+"//"+Filnavn );

			if (!filen.getParentFile().exists())
				filen.getParentFile().mkdirs();

			if ( !filen.exists() ) {
				filen.createNewFile();
			}
			
			PrintStream utfil;
			FileOutputStream appendFilen = new FileOutputStream ( filen, true );
			utfil = new PrintStream ( appendFilen );

			utfil.println( "Price: "+NewOffer.Price+" Auction: "+NewOffer.Url+" Hit%="+NewOffer.HitPrcnt+" MaxScore="+NewOffer.MaxScore+" IDmatch="+NewOffer.IDmatch+" NumberOfDetails="+NewOffer.NumberOfDetails+" DetaljScore="+NewOffer.DetaljScore );

			utfil.close ( );
			//System.out.println( "Offer saved to file "+Filnavn );
		} catch ( IOException T ) {
			if ( T.getMessage ( ).equals("Access is denied")) {
				SaveToFile( NewOffer );
			} else {
				CastErrors( T );
			}
		} catch ( Exception T ) {
			CastErrors( T );
		}
	}

	public Object DetailsToString() {
		String aer = "";
		for ( int A = 0 ; A < Details.length ; A++ ) {
			if ( Details[A] != null ) {
				aer = aer+Details[A]+", ";
			}
		}
		return aer;
	}
	
	private void SortOffers() {
		SortedOffers = Offers;
		NumberOfOffers = 0;
		for ( int A = 0 ; A < SortedOffers.length ; A++ ) {
			if ( SortedOffers[A] != null ) {
				NumberOfOffers ++;
				for ( int B = 0 ; B < SortedOffers.length ; B++ ) {
					if ( SortedOffers[B] != null ) {
						
						if ( SortedOffers[A].Price < SortedOffers[B].Price ) {
							Object_Offer OfferA = SortedOffers[A];
							Object_Offer OfferB = SortedOffers[B];
							SortedOffers[A] = OfferB;
							SortedOffers[B] = OfferA;
						}
						
					}
				}				
			}
		}
	}

	public Object MedianOffer() {
	
		SortOffers();
		if ( SortedOffers[(NumberOfOffers/2)] != null ) {
			return SortedOffers[(NumberOfOffers/2)].Price;
		} else {
			return "e";
		}
	}

	public Object LowestOffer() {
		
		SortOffers();
		if ( SortedOffers[0] != null ) {
			return SortedOffers[0].Price;
		} else {
			return "e";
		}
		
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
