package GUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Control.Controller;

public class Hovedvindu extends JFrame implements ActionListener {

	private static final long	serialVersionUID	= 1L;
	public JButton[]			MenyKnapp			= new JButton[6];
	Controller				Class_Controller;
	
	
	JPanel						TotalPanel;
	JPanel						MessagePanel;
	JPanel						ResultsPanel;
	JPanel						ToolPanel;
		JPanel					StaticPanel;
		JPanel					ProgressPanel;
		JPanel					ActionPanel;
	
	JScrollPane					GearPanelScrollPanel	= new JScrollPane ( );
	JTable						GearTable				= new JTable ( );
	DefaultTableModel			GearTableModel;
	Vector						GearCollumnNames;
	Vector						GearListTableContent;
	
	JTextField Ny_Productname;
	JTextField Ny_Producer;
	JTextField Ny_Details;
	JButton Save_Ny;
	
	JTextArea MessageText;
	
	String[] InfoContent;
	
	JToolBar MenuBar;
	JButton  StartSearch;
	JButton  StopSearch;
	
	JProgressBar progressBar;
	
	JLabel Currency;
	JLabel CurrentAction;
	
	boolean Updating = true;
	
	MouseListener mouseListener;

	public Hovedvindu ( Controller Class_CommunicatorT) {

		super ( "Ebay Offers Scanner - by Berit Larsen" );
		Class_Controller = Class_CommunicatorT;
		//System.out.println ( "Class Vindu_Menu created" );
		
		this.GearListTableContent = new Vector ( );
		this.GearCollumnNames = new Vector ( );
		this.GearTableModel = new DefaultTableModel ( );
		InfoContent = new String[10];
	}

	public void Startup ( ) {

		//System.out.println ( "Class Vindu_Menu started" );
		
		setSize ( 1200, 660 );

		Container pane = getContentPane ( );
		//pane.setLayout ( new GridLayout ( 1, 1 ) );
		pane.setLayout ( new BoxLayout ( pane, BoxLayout.PAGE_AXIS ));
		GridBagConstraints c = new GridBagConstraints ( );
		
		TotalPanel = new JPanel ( );
		TotalPanel.setLayout ( new BoxLayout ( this.TotalPanel, BoxLayout.PAGE_AXIS ) );
		TotalPanel.setBackground ( new Color ( ( 210 ), ( 225 ), ( 240 ) ) );
		
		ToolPanel = new JPanel ( );
		ToolPanel.setLayout ( new BoxLayout ( this.ToolPanel, BoxLayout.LINE_AXIS ) );
		ToolPanel.setPreferredSize( new Dimension( 1200, 30 ) );
		ToolPanel.setMaximumSize( new Dimension( 1200, 50 ) );
		ToolPanel.setMinimumSize( new Dimension( 1200, 20 ) );
		TotalPanel.add ( this.ToolPanel );
		
		StaticPanel = new JPanel ( );
		StaticPanel.setLayout ( new BoxLayout ( this.StaticPanel, BoxLayout.LINE_AXIS ) );
		StaticPanel.setPreferredSize( new Dimension( 400, 30 ) );
		StaticPanel.setMaximumSize( new Dimension( 400, 50 ) );
		StaticPanel.setMinimumSize( new Dimension( 400, 20 ) );
		ToolPanel.add ( this.StaticPanel );
		
		ProgressPanel = new JPanel ( );
		ProgressPanel.setLayout ( new BoxLayout ( this.ProgressPanel, BoxLayout.LINE_AXIS ) );
		ProgressPanel.setPreferredSize( new Dimension( 400, 30 ) );
		ProgressPanel.setMaximumSize( new Dimension( 400, 50 ) );
		ProgressPanel.setMinimumSize( new Dimension( 400, 20 ) );
		ToolPanel.add ( this.ProgressPanel );
		
		ActionPanel = new JPanel ( );
		ActionPanel.setLayout ( new BoxLayout ( this.ActionPanel, BoxLayout.LINE_AXIS ) );
		ActionPanel.setPreferredSize( new Dimension( 400, 30 ) );
		ActionPanel.setMaximumSize( new Dimension( 400, 50 ) );
		ActionPanel.setMinimumSize( new Dimension( 400, 20 ) );
		ToolPanel.add ( this.ActionPanel );

		ResultsPanel = new JPanel ( );
		ResultsPanel.setLayout ( new BoxLayout ( this.ResultsPanel, BoxLayout.LINE_AXIS ) );
		ResultsPanel.setPreferredSize( new Dimension( 1200, 440 ) );
		//TopPanel1.setMaximumSize( new Dimension( 1200, 440 ) );
		TotalPanel.add ( this.ResultsPanel );
		
		MessagePanel = new JPanel ( );
		MessagePanel.setLayout ( new BoxLayout ( this.MessagePanel, BoxLayout.PAGE_AXIS ) );
		MessagePanel.setBackground ( new Color ( ( 255 ), ( 0 ), ( 0 ) ) );
		MessagePanel.setPreferredSize( new Dimension( 1200, 200 ) );
		MessagePanel.setMaximumSize( new Dimension( 1200, 300 ) );
		MessagePanel.setMinimumSize( new Dimension( 1200, 200 ) );
		TotalPanel.add ( this.MessagePanel );
		
		this.GearCollumnNames.addElement ( "Productname" );
		this.GearCollumnNames.addElement ( "Producer" );
		this.GearCollumnNames.addElement ( "Details" );
		this.GearCollumnNames.addElement ( "Max Price" );
		this.GearCollumnNames.addElement ( "Offers" );
		this.GearCollumnNames.addElement ( "Median" );
		this.GearCollumnNames.addElement ( "Lowest" );
		this.GearTableModel.setDataVector ( this.GearListTableContent, this.GearCollumnNames );
		this.GearTable = new JTable ( this.GearTableModel );

		TableColumn column;
		column = this.GearTable.getColumnModel ( ).getColumn ( 0 );
		column.setPreferredWidth ( 200 );//Productname
		column = this.GearTable.getColumnModel ( ).getColumn ( 1 );
		column.setPreferredWidth ( 75 );//Producer
		column = this.GearTable.getColumnModel ( ).getColumn ( 2 );
		column.setPreferredWidth ( 500 );//Details
		column = this.GearTable.getColumnModel ( ).getColumn ( 3 );
		column.setPreferredWidth ( 20 );//price
		column = this.GearTable.getColumnModel ( ).getColumn ( 4 );
		column.setPreferredWidth ( 20 );//Offers
		column = this.GearTable.getColumnModel ( ).getColumn ( 5 );
		column.setPreferredWidth ( 20 );//Median
		column = this.GearTable.getColumnModel ( ).getColumn ( 6 );
		column.setPreferredWidth ( 20 );//Lowest

		this.GearPanelScrollPanel = new JScrollPane ( this.GearTable );
		this.GearPanelScrollPanel.setPreferredSize ( new Dimension ( 500, 420 ) );
		this.ResultsPanel.add ( this.GearPanelScrollPanel );
		
		
		/*this.Ny_Productname = new JTextField ( "" );
		this.Ny_Producer = new JTextField ( "" );
		this.Ny_Details = new JTextField ( "" );
		Save_Ny = new JButton("Save new product");*/
		
		MessageText = new JTextArea( 10, 1 );
		MessageText.setEditable( false);
		JScrollPane scrollPane = new JScrollPane(MessageText); 
		MessagePanel.add ( scrollPane );
		
		MenuBar = new JToolBar();
		
		StartSearch = new JButton ( "Start search" );
		StartSearch.getAccessibleContext().setAccessibleDescription( "Loads your config file and starts searching");
		MenuBar.add(StartSearch);
		
		StopSearch = new JButton ( "Stop search" );
		StopSearch.getAccessibleContext().setAccessibleDescription( "Stops searching.");
		MenuBar.add(StopSearch);
		
		StaticPanel.add(MenuBar);
		
		Currency =  new JLabel( "" );
		Currency.setMinimumSize( new Dimension( 300, 20 ) );
		StaticPanel.add(Currency);
		
		progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        progressBar.setMaximumSize( new Dimension( 400, 20 ) );
        ProgressPanel.add(progressBar);
		
        CurrentAction = new JLabel("Waiting to start");
        CurrentAction.setSize(20, 100);
        CurrentAction.setMinimumSize( new Dimension( 300, 20 ) );
        ActionPanel.add(CurrentAction);

		//pane.add ( this.MenuBar, c );
		pane.add ( this.TotalPanel, c );
		Updating = false;
		Update ( );
		
		setVisible ( true );
		AddListeners();

	}
	
	private void AddListeners() {
		try {
			//Save_Ny.addActionListener ( this );
			StartSearch.addActionListener( this );
			StopSearch.addActionListener( this );
			StartSearch.addMouseListener( mouseListener );
			StopSearch.addMouseListener( mouseListener );
		} catch ( Exception T ) {
			
		}
	}
	
	public void mousePressed( MouseEvent e) {
		try {
			System.out.println( e+" "+e.getSource() );
			if ( e.getSource().equals ( this.StartSearch.getText ( ) ) ) {
				System.out.println( "StartSearch ble trykket" );
				Class_Controller.UserCommand_StartSearching( "UserCommand" );
			} else if ( e.getSource ( ).equals ( this.StopSearch.getText ( ) ) ) {
				System.out.println( "StopSearch ble trykket" );
				Class_Controller.UserCommand_StopSearching();
			}
		} catch ( Exception T ) {
			
		}
    }


	public void actionPerformed ( ActionEvent e ) {
		try {
			//System.out.println( e+" "+e.getSource() );
			/*if ( e.getActionCommand ( ).equals ( this.Save_Ny.getText ( ) ) ) {
				this.Updating = true;
				this.Class_Controller.SaveNewProduct( Ny_Productname.getText(), Ny_Producer.getText(), Ny_Details.getText() );
				Ny_Productname.setText("");
				Ny_Producer.setText("");
				Ny_Details.setText("");
				Update();
				this.Updating = false;
			} else */
			if ( e.getActionCommand().equals ( this.StartSearch.getText ( ) ) ) {
				//System.out.println( "StartSearch ble trykket" );
				Class_Controller.UserCommand_StartSearching( "UserCommand" );
			} else if ( e.getActionCommand ( ).equals ( this.StopSearch.getText ( ) ) ) {
				//System.out.println( "StopSearch ble trykket" );
				Class_Controller.UserCommand_StopSearching();
			}
		} catch ( Exception T ) {
			
		}
	}
	
	public void Update() {
		if ( Updating == false ) {
			this.Updating = true;
			try {
				Currency.setText( "Currency: 1$="+Class_Controller.Currency+"NOK" );
				
				progressBar.setValue( (int) ((100.0/Class_Controller.GetMaxSearchProgress())*Class_Controller.GetSearchProgress()) );
				progressBar.setMaximum( 100 );
				progressBar.setString( Class_Controller.GetSearchProgress()+"/"+Class_Controller.GetMaxSearchProgress() );
				//System.out.println( progressBar.getValue()+" "+Class_Controller.GetMaxSearchProgress());
				//progressBar.setMaximum( Class_Controller.GetMaxSearchProgress() );
			} catch ( Exception T ) {
				
			}
			try {
				if ( GearListTableContent.isEmpty() == false ) {
					this.GearListTableContent.removeAllElements ( );
					//removeAllElements( GearListTableContent );
				}
				/*int rc= GearTableModel.getRowCount();
	            for(int i = 0;i<rc;i++){
	            	//GearTableModel.removeRow(0);
	            	GearTableModel.removeRow(i);
	            } */
				for ( int y = 0 ; y < this.Class_Controller.Class_Brain_Produkter.Produktliste.length ; y++ ) {
					if ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y] != null ) {
						if ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].NumberOfOffers > 0 ) {
							//System.out.println( "Update "+y );
							try {
								Vector Temp = new Vector ( );
				
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].OfficialName );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].Producer );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].DetailsToString() );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].MaxPrice );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].NumberOfOffers );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].MedianOffer() );
								Temp.addElement ( this.Class_Controller.Class_Brain_Produkter.Produktliste[y].LowestOffer() );
				
								this.GearListTableContent.addElement ( Temp );
							} catch ( Exception T ) {
								CastErrors( T );
							}
						}
					}
				}
				//System.out.println( "Update etter loop" );
				try {
					this.GearTableModel.fireTableDataChanged ( );
				} catch ( Exception T ) {
					CastErrors( T );
				}
			} catch ( Exception T ) {
				CastErrors( T );
			}
			this.Updating = false;
		}
	}
	
	public void removeAllElements( List elements ) {
        Iterator itr = elements.iterator(); 
        while(itr.hasNext()) {
        	itr.next();
        	itr.remove();
        }
    }
	
	public void AddProgressMessage( String Message ) {
		try {
			Date Idag = new Date();
			MessageText.append( Idag.getHours()+"."+Idag.getMinutes()+":"+Idag.getSeconds()+" "+Message+"\n" );
			MessageText.selectAll();
			MessageText.setCaretPosition( MessageText.getDocument().getLength());
		} catch ( Exception T ) {	
		}
	}
	
	public void SetCurrentAction( String ActionText ) {
		try {
			//System.out.println( CurrentAction );
			CurrentAction.setText( " "+ActionText );
		} catch ( Exception T ) {
			
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
