package Objects;

import java.net.URL;

public class Object_Website extends Object_Webpage {
	
	//Object_Webpage[] SiteMap;
	Object_TreeNode_Webpage FirstNode_Searched;
	Object_TreeNode_Webpage FirstNode_Waiting;

	public Object_Website( URL Adresse, int LinkedRelationValue, int SelfRelationValue ) {
		
		super( Adresse, LinkedRelationValue, SelfRelationValue);
		//SiteMap = new Object_Webpage[999];
		
	}

	public void InsertPage( Object_Webpage newpage ) {
		//System.out.println( "InsertPage "+Adresse+" "+newpage.Get_URL() );
		if( SearchForURL(  newpage, FirstNode_Searched ) == true ) {
			return;
		}
		if( SearchForURL(  newpage, FirstNode_Waiting ) == true ) {
			return;
		}
		
		if ( FirstNode_Waiting == null ) {
			FirstNode_Waiting = new Object_TreeNode_Webpage( newpage );
			return;
		}
		
		Object_TreeNode_Webpage CurrentNode = FirstNode_Waiting;
		while ( CurrentNode != null ) {
			if ( CurrentNode.Link.LinkedRelationValue <= newpage.LinkedRelationValue ) {
				//System.out.println( "InsertPage "+CurrentNode.Link.LinkedRelationValue+" <= "+newpage.LinkedRelationValue+" adding left" );
				if ( CurrentNode.Left != null ) {
					CurrentNode = CurrentNode.Left;
				} else {
					CurrentNode.Left = new Object_TreeNode_Webpage( newpage );
					//System.out.println( "InsertPage added left" );
					return;
				}
			} else {
				if ( CurrentNode.Right != null ) {
					CurrentNode = CurrentNode.Right;
				} else {
					CurrentNode.Right = new Object_TreeNode_Webpage( newpage );
					//System.out.println( "InsertPage added right" );
					return;
				}
			}
		}
		CurrentNode = new Object_TreeNode_Webpage( newpage );
		//System.out.println( Adresse+" first node: "+FirstNode_Waiting.Link.Adresse );
	}
	
	private boolean SearchForURL(  Object_Webpage newpage, Object_TreeNode_Webpage SearchStart ) {
		
		while ( SearchStart != null ) {
			if ( SearchStart.Link.Get_URL().toString().compareTo( newpage.Get_URL().toString() ) == 0 ) {
				return true;
			} else {
				if ( SearchForURL(  newpage, SearchStart.Left ) == true ) {
					return true;
				} else {
					return SearchForURL(  newpage, SearchStart.Right );
				}
			}
		}
		
		return false;
	}
	
	public void InsertSearched( Object_Webpage newpage ) {
		
		if ( FirstNode_Searched == null ) {
			FirstNode_Searched = new Object_TreeNode_Webpage( newpage );
		}
		
		Object_TreeNode_Webpage CurrentNode = FirstNode_Searched;
		while ( CurrentNode != null ) {
			if ( CurrentNode.Link.LinkedRelationValue < newpage.LinkedRelationValue ) {
				if ( CurrentNode.Left != null ) {
					CurrentNode = CurrentNode.Left;
				} else {
					CurrentNode.Left = new Object_TreeNode_Webpage( newpage );
					return;
				}
			} else {
				if ( CurrentNode.Right != null ) {
					CurrentNode = CurrentNode.Right;
				} else {
					CurrentNode.Right = new Object_TreeNode_Webpage( newpage );
					return;
				}
			}
		}
		CurrentNode = new Object_TreeNode_Webpage( newpage );
		//System.out.println( Adresse+" first node: "+FirstNode_Waiting.Link.Adresse );
	}

	public Object_Webpage Get_NextPageSearch() {
		//System.out.println( "Get_NextPageSearch "+FirstNode_Waiting+" "+Adresse );
		try {
			Object_TreeNode_Webpage CurrentNode = FirstNode_Waiting;
			if ( CurrentNode == null ) {
				return null;
			}
			if ( CurrentNode.Left == null ) {
				//System.out.println( "Returnerer CurrentNode.Left A: "+FirstNode_Waiting.Link.Get_URL() );
				InsertSearched( FirstNode_Waiting.Link );
				Object_Webpage NodeToSearch = FirstNode_Waiting.Link;
				//det finnes ingen left, og current fjernes, så right må bli root
				FirstNode_Waiting = CurrentNode.Right;
				return NodeToSearch; 
			} else {
				while ( CurrentNode.Left.Left != null ) {
					CurrentNode = CurrentNode.Left;
				}
			}
			//System.out.println( "Returnerer CurrentNode.Left B: "+CurrentNode.Left.Link.Get_URL() );
			InsertSearched( CurrentNode.Left.Link );
			Object_TreeNode_Webpage NodeToSearch = CurrentNode.Left;
			CurrentNode.Left = null;
			
			return NodeToSearch.Link;
		} catch ( Exception T ) {
			CastErrors( T );
		}
		return null;
	}

	public int Get_SiteRelationValue() {

		return Get_Relationvalue( FirstNode_Searched );
	}
	
	private int Get_Relationvalue( Object_TreeNode_Webpage Startnode ) {
		int SiteRelationValue = 0;
		if ( Startnode.Left != null ) {
			SiteRelationValue = SiteRelationValue + Get_Relationvalue( Startnode.Left );
		}
		if ( Startnode.Right != null ) {
			SiteRelationValue = SiteRelationValue + Get_Relationvalue( Startnode.Right );
		}
		
		return SiteRelationValue;
	}
	
	
	
	private void CastErrors( Exception T ) {
		System.err.println( this.getClass().getName() );
		System.err.println( T );
		/*
		System.err.println ( "Origin stack "+1+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[0].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[0].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[0].getLineNumber ( ) );
		*/
		System.err.println ( "Origin stack "+1+": ");
		System.err.println ( "Class: " + T.getStackTrace ( )[1].getClassName ( ) );
		System.err.println ( "Method: " + T.getStackTrace ( )[1].getMethodName ( ) );
		System.err.println ( "Line: " + T.getStackTrace ( )[1].getLineNumber ( ) );
		
		for ( int y = 2 ; y < T.getStackTrace().length ; y++ ) {
			System.err.println (" ");
			System.err.println ( "Origin stack "+y+": ");
			System.err.println ( "Class: " + T.getStackTrace ( )[y].getClassName ( ) );
			System.err.println ( "Method: " + T.getStackTrace ( )[y].getMethodName ( ) );
			System.err.println ( "Line: " + T.getStackTrace ( )[y].getLineNumber ( ) );
		}
	}
	
}
