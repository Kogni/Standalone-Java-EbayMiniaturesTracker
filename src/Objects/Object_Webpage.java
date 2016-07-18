package Objects;

import java.net.URL;

public class Object_Webpage {

	URL Adresse;
	public int LinkedRelationValue;
	int SelfRelationValue;
	boolean Searched = false;
	
	public Object_Webpage( URL Adresse, int LinkedRelationValue, int SelfRelationValue ) {
		this.Adresse = Adresse;
		this.LinkedRelationValue = LinkedRelationValue;
		this.SelfRelationValue = SelfRelationValue;
	}
	
	
	public URL Get_URL() {
		return Adresse;
	}

	public int Get_LinkedRelationValue() {
		return LinkedRelationValue;
	}
	
	public boolean Get_Searched() {
		return Searched;
	}
	
	public int Get_SelfRelationValue() {
		return SelfRelationValue;
	}
	
	
	
	public void Set_LinkedRelationValue( int value ) {
		LinkedRelationValue = value;
	}
	
	public void Set_Searched() {
		Searched = true;
	}
	
	public void Set_SearchFailed() {
		Searched = false;
	}
	
	public void Set_SelfRelationValue( int value ) {
		SelfRelationValue = value;
	}
}
