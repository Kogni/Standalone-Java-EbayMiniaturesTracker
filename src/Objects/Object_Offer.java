package Objects;

import java.net.URL;

public class Object_Offer {

	public URL Url;
	public Double Price;
	public String Name;
	public String Beskrivelse;
	
	public int HitPrcnt = 0;
	public int MaxScore;
	public int IDmatch;
	public int NumberOfDetails;
	public int DetaljScore;
	
	public Object_Offer( URL Url, Double Price, String name, String beskrivelse ) {
		
		this.Url = Url;
		this.Price = Price;
		this.Name  = name;
		this.Beskrivelse = beskrivelse;
	}
}
