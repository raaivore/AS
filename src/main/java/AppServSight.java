
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.w3c.dom.Node;

import ee.or.is.Config;
import ee.or.is.DOMData;
import ee.or.is.ExceptionIS;
import ee.or.is.ISServlet;
import ee.or.is.MException;
import ee.or.is.OptionsList;
import ee.or.is.Sight;
import ee.or.is.User;

public class AppServSight extends Sight
{
	private static final long serialVersionUID = 1L;
	
	static String	Localhost = null;
	static String	Node1 = null;
	static String	Node2 = null;
	   
	public static List<Appl>		Appsl = null;
	public static List<Serv>		Servsl = null;

	public static List<SolrApps>	SolrAppsl = null;
	public static List<SolrServs>	SolrServsl = null;

	Random rand = null;

	public AppServSight() {
		super();
	}
	public AppServSight( HttpServletRequest Request, ISServlet Servlet, Config appconfig){
		super( Request, Servlet, appconfig);
	}
	public void init() {
		super.init();
	}
	public void clearAll() {
		super.clearAll(); 
	}
	public synchronized void reload()
	{
	}
	public static	int GetAppCode() {
		return AppServServlet.SelectedApp;	
	}
	
	public void SortApps() {
		Collections.sort(Appsl, new Comparator<Appl>() {
			  public int compare(Appl u1, Appl u2) {
			    return u1.App_code - u2.App_code;
			  }
			});		
	}
	
	public void SortServs() {
		Collections.sort(Servsl, new Comparator<Serv>() {
			  public int compare(Serv u1, Serv u2) {
			    return u1.Service_code - u2.Service_code;
			  }
			});		
	}
	
	public void TodAppsl(OptionsList Obs) {
		if(  Appsl != null ) 	Appsl.clear();
		else 					Appsl = new ArrayList<Appl>();

		if(  SolrAppsl != null ) 	SolrAppsl.clear();
		else 						SolrAppsl = new ArrayList<SolrApps>();
		
		for( int i = 0; i < Obs.size(); i++ ) {
			Appl appl = (Appl)Obs.get(i);
			SolrApps solappl = new SolrApps();
		    int int_random = rand.nextInt(100); 
		    solappl.id = String.format("%010d", int_random );
		    solappl.App_code = String.valueOf( appl.App_code);
		    solappl.Name = appl.Name;
			SolrAppsl.add(solappl);
			Appsl.add(appl);
		}
		SortApps();
	}

	public void ToServs(OptionsList Obs) {
		if(  Servsl != null )	Servsl.clear();
		else 					Servsl = new ArrayList<Serv>();
		for( int i = 0; i < Obs.size(); i++ )		
			Servsl.add((Serv)Obs.get(i));
		SortServs();
	}
	
	public void ToSolrlServs(OptionsList Obs) {
		if(  SolrServsl != null )	SolrServsl.clear();
		else 						SolrServsl = new ArrayList<SolrServs>();

		for( int i = 0; i < Obs.size(); i++ ) {
			Serv serv = (Serv)Obs.get(i);
			SolrServs solserv = new SolrServs();
		    int int_random = rand.nextInt(100); 
		    solserv.id = (String.format("%010d", int_random ));
		    solserv.App_code = String.valueOf( serv.App_code );
		    solserv.Service_code = String.valueOf( serv.Service_code );
		    solserv.Name = serv.Name;
		    SolrServsl.add(solserv);
		}
	}
	
	public Sight getMySight(  HttpServletRequest request) throws MException 
	{
		Config	  appconfig;
		
		AppServSight MySight = new AppServSight();
		super.setMySight( MySight, request);
		MySight.User = new User( request);
		if( MySight.User.getName() == null ){
			MySight.User.setName( isDebug( 98)? request.getSession().getId(): "User");
		}
		MySight.createLogCat();
		
		return MySight;		
	}
	public int GetMaxServ() {
		int	MaxServCode = -1;
		for( int i = 0; i < Servsl.size(); ++i){
			Serv serv = Servsl.get(i);
			if(  serv.Service_code > MaxServCode ) MaxServCode = serv.Service_code;
		}
		return MaxServCode;
	}

	public int GetMaxApp() {
		int	MaxAppCode = -1;
		for( int i = 0; i < Appsl.size(); ++i){
			Appl App = Appsl.get(i);
			if(  App.App_code > MaxAppCode ) MaxAppCode = App.App_code;
		}
		return MaxAppCode;
	}

	public String GetSolAppId(int App_code) {
		for( int i = SolrAppsl.size() - 1; i != 0; --i){
			SolrApps app = SolrAppsl.get(i);
			if(app.App_code.compareTo( String.valueOf(App_code)) == 0 )
				return app.id;
		}
		return null;	
	}
	
	public String GetSolServId(int App_code, int Serv_code) {
		for( int i = SolrServsl.size() - 1; i != 0; --i){
			SolrServs serv = SolrServsl.get(i);
			if(serv.App_code.compareTo( String.valueOf(App_code)) == 0 && 
					serv.Service_code.compareTo( String.valueOf(Serv_code)) == 0 )
				return serv.id;
		}
		return null;
	}
	
	public static String GetUrl(String Url) {
        URL url;
		try {
			url = new URL(Url);
		} catch (IOException e) {
			return null;
		}

		Scanner sc;
		try {
			sc = new Scanner(url.openStream());
		} catch (IOException e) {
			return null;
		}

        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()) {
           sb.append(sc.next());
        }

        String result = sb.toString();
        return result;
    }
    public static Boolean SetUpSolr() throws IOException {
    	String responseStr = null;
    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node1 + "/solr/admin/collections?action=LIST");
    	if(responseStr == null ) return false;
		if(responseStr.contains("applications")) {
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node1 + "/solr/admin/collections?action=DELETE&name=applications");
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node1 + "/solr/admin/collections?action=CREATE&name=applications&numShards=1&replicationFactor=1");
		}
		else {
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node1 + "/solr/admin/collections?action=CREATE&name=applications&numShards=1&replicationFactor=1");
		}
    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node2 + "/solr/admin/collections?action=LIST");
    	if(responseStr == null ) return false;
		if(responseStr.contains("services")) {
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node2 + "/solr/admin/collections?action=DELETE&name=services");
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node2 + "/solr/admin/collections?action=CREATE&name=services&numShards=1&replicationFactor=1");

		}
		else {
	    	responseStr = GetUrl("http://"  + Localhost + ":"  + Node2 + "/solr/admin/collections?action=CREATE&name=services&numShards=1&replicationFactor=1");
		}

	    return true;
    }

	public DOMData doRequest(HttpServletRequest Request, HttpServletResponse Response) throws ExceptionIS 
	{ 
		DOMData Doc = null;
		String sReq =  getRequest( Request);
		String Button = Request.getParameter( "Button");
		String SApp_code = Request.getParameter( "Selected_App");
		String SServ_code = Request.getParameter( "Selected_Serv");
		int	   PrevApp = AppServServlet.SelectedApp;
	
		if( rand == null) rand = new Random();

		while (AppServServlet.ServiceProc);

		System.out.println("*** REQ =  " + sReq + " Button = " + Button + " App_code = " + SApp_code + " Serv_code = " + SServ_code);

		AppServServlet.ServiceProc = true;
		
		if(!AppServServlet.SolrIsReady)
			try {
				AppServServlet.SolrIsReady = SetUpSolr();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if( !AppServServlet.SolrIsReady )
			System.out.println("!!!!!!!!!!    Solr isn't running. Please run solr cloud -nopromt" );
				
		if(AppServServlet.SolrIsReady) {
			if( AppServServlet.solrClientApps == null ) {
				AppServServlet.solrClientApps = new HttpSolrClient.Builder("http://"  + Localhost + ":"  + Node1 + "/solr/applications").build();
				try {
					AppServServlet.solrClientApps.addBeans(SolrAppsl);
					AppServServlet.solrClientApps.commit();
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if( AppServServlet.solrClientServs == null ) {
				AppServServlet.solrClientServs = new HttpSolrClient.Builder("http://"  + Localhost + ":"  + Node2 + "/solr/services").build();
				try {
					AppServServlet.solrClientServs.addBeans(SolrServsl);
					AppServServlet.solrClientServs.commit();
				} catch (SolrServerException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
      
		if( SApp_code != null  )
			AppServServlet.SelectedApp = Integer.parseInt(SApp_code);
			
		if( SServ_code != null  )
			AppServServlet.SelectedServ = Integer.parseInt(SServ_code);
			
		if( Button != null && Button.compareTo("AppDelete") == 0 ) {
			int	PrevAppCode = -1;
//			System.out.println("*** PrevApp  =  " + String.valueOf(PrevApp) );
			for( int i = 0; i < Appsl.size(); ++i){
				Appl app = Appsl.get(i);
				if(  app.App_code > PrevAppCode ) {
//					System.out.println("*** App_code  =  " + String.valueOf(app.App_code) + " I = " + String.valueOf(i) );
					if( app.App_code == PrevApp ) 	break;
					else							PrevAppCode = app.App_code;
				}
			}
//			System.out.println("*** AppCode  =  " + String.valueOf(AppServServlet.SelectedApp) );
//			System.out.println("*** PrevAppCode  =  " + String.valueOf(PrevAppCode) );
			if( PrevAppCode == -1) {
				int	NextAppCode = Integer.MAX_VALUE;
				for( int i = Appsl.size() - 1; i != 0; --i){
					Appl app = Appsl.get(i);
					if(  app.App_code  < NextAppCode ) {
						if( app.App_code == PrevApp ) 	break;
						else							NextAppCode = app.App_code;
					}
				}
//				System.out.println("*** NextAppCode  =  " + 	String.valueOf(NextAppCode) );
				if( NextAppCode != Integer.MAX_VALUE )
					AppServServlet.SelectedApp = NextAppCode;
			}
			else
				AppServServlet.SelectedApp = PrevAppCode;
			
			if( AppServServlet.SelectedApp != PrevApp ) {
				for( int i = 0; i < Appsl.size(); ++i){
					Appl app = Appsl.get(i);
					if(  app.App_code == PrevApp )	 
						Appsl.remove(i);
				}
				String Query = "DELETE FROM app_service WHERE app_code = " + PrevApp + ";";

//				System.out.println ( Query );
				AppServServlet.getSight().Db.exec( Query );

				for( int i = SolrServsl.size() - 1; i != 0; --i){
					SolrServs serv = SolrServsl.get(i);
					if(serv.App_code.compareTo( String.valueOf(PrevApp)) == 0 )
					try {
						AppServServlet.solrClientServs.deleteById(serv.id);
					} catch (SolrServerException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				Query = "DELETE FROM application WHERE app_code = " + PrevApp + ";";

//				System.out.println ( Query );
				AppServServlet.getSight().Db.exec( Query );

				try {
					for( int i = SolrAppsl.size() - 1; i != 0; --i){
						SolrApps app = SolrAppsl.get(i);
						if(app.App_code.compareTo( String.valueOf(PrevApp)) == 0 ) {
							AppServServlet.solrClientApps.deleteById(app.id);	
							SolrAppsl.remove(i);
						}
					}
				} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

//		System.out.println("*** SelectedApp  =  " + AppServServlet.SelectedApp + " PrevApp = " + PrevApp );

		if( Button != null && Button.compareTo("ServDelete") == 0 ) {
			int	PrevSelected = AppServServlet.SelectedServ;
			
			String Query = "DELETE FROM app_service WHERE app_code = " + AppServServlet.SelectedApp +
						" and  service_code = " +  PrevSelected + ";";

//			System.out.println ( Query );
			AppServServlet.getSight().Db.exec( Query );
				
			int	PrevServCode = -1;
			for( int i = 0; i < Servsl.size(); ++i){
				Serv serv = Servsl.get(i);
//				System.out.println("*** ServCode  =  " + String.valueOf(serv.Service_code) + " I = " + String.valueOf(i) );
				if(  serv.Service_code > PrevServCode ) {
					if( serv.Service_code == PrevSelected ) 	break;
					else										PrevServCode = serv.Service_code;
				}
			}
//			System.out.println("*** PrevServCode  =  " + String.valueOf(PrevServCode) );
			if( PrevServCode == -1) {
				int	NextServCode = Integer.MAX_VALUE;
				int i = 0;
				if( Servsl.size() >  1 )	
					i = Servsl.size() - 1;
				if(Servsl.size() != 0) {
					for( ; i >=  0; --i){
						Serv serv = Servsl.get(i);
						if(  serv.Service_code  < NextServCode ) {
							if( serv.Service_code == PrevSelected ) 	break;
							else										NextServCode = serv.Service_code;
						}
					}					
				}
//				System.out.println("*** NextServCode  =  " + 	String.valueOf(NextServCode) );
				if( NextServCode != Integer.MAX_VALUE )
					AppServServlet.SelectedServ = NextServCode;
			}
			else
				AppServServlet.SelectedServ = PrevServCode;				

//			System.out.println("*** SelecteServ =  " + AppServServlet.SelectedServ + " PrevSelected = " + PrevSelected );

			for( int i = 0; i < Servsl.size(); ++i){
				Serv serv = Servsl.get(i);
				if(  serv.Service_code == PrevSelected ) Servsl.remove(i);
			}

				for( int i = SolrServsl.size() - 1; i != 0; --i){
					SolrServs solserv = SolrServsl.get(i);
					if(solserv.App_code.compareTo( String.valueOf(AppServServlet.SelectedApp)) == 0 && 
							solserv.Service_code.compareTo( String.valueOf(PrevSelected)) == 0 ) {
						try {
						AppServServlet.solrClientServs.deleteById(solserv.id);
						} catch (SolrServerException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SolrServsl.remove(solserv);
					}
				}
			
			if( AppServServlet.SelectedServ == PrevSelected ) {
				AppServServlet.SelectedServ = 0;

				Serv serv = new Serv();
				serv.App_code  = AppServServlet.SelectedApp;
				serv.Service_code = 0;
				if(  Servsl != null )	Servsl.clear();
				else 					Servsl = new ArrayList<Serv>();
				Servsl.add(serv);
				
				SolrServs solserv = new SolrServs();
			    int int_random = rand.nextInt(1000); 
			    solserv.id = String.format("%010d", int_random );
			    solserv.App_code = String.valueOf( AppServServlet.SelectedApp) ;
			    solserv.Service_code = String.valueOf( AppServServlet.SelectedServ );
				SolrServsl.add( solserv );
			}
		}

		if( Button != null && Button.compareTo("AppNext") == 0 ) {
			int i;
			if(Appsl.size() > 0 ) {
				for( i = 0; i < Appsl.size(); ++i)
					if(  Appsl.get(i).App_code > AppServServlet.SelectedApp ) 
						break;
				if( i != Appsl.size() )	
					AppServServlet.SelectedApp = Appsl.get(i).App_code;
			}
		}
		if( Button != null && Button.compareTo("AppPrev") == 0 ) {
			int i;
			if(Appsl.size() > 0 ) {
				for( i = Appsl.size() - 1; i >= 0 ; --i)
					if(  Appsl.get(i).App_code < AppServServlet.SelectedApp )
						break;
				if( i >= 0 )			
					AppServServlet.SelectedApp = Appsl.get(i).App_code;
			}
		}
		if( AppServServlet.SelectedApp !=  PrevApp ) {
	//		System.out.println("*** SelectedApp  =  " + String.valueOf(AppServServlet.SelectedApp) );
			AppServServlet.servs.createDataObjects(AppServServlet.getSight().Db);
			ToServs( AppServServlet.servs.getDataObjects() );

			int	MinServCode = Integer.MAX_VALUE;
			for( int i = 0; i < Servsl.size(); ++i){
				Serv serv = Servsl.get(i);
				if(  serv.Service_code < MinServCode ) MinServCode = serv.Service_code;
			}
			if( MinServCode != Integer.MAX_VALUE )	
				AppServServlet.SelectedServ = MinServCode;
//			System.out.println("Service_code  =  " + String.valueOf(AppServServlet.SelectedServ) + "  App_code  =  " + String.valueOf(AppServServlet.SelectedApp) );
		}
		if( Button != null && Button.compareTo("ServNext") == 0 ) {
			int i;
			if(Servsl.size() > 0 ) {
				for( i = 0; i < Servsl.size(); ++i)
					if(  Servsl.get(i).Service_code > AppServServlet.SelectedServ ) break;
				if( i != Servsl.size() ) 	AppServServlet.SelectedServ = Servsl.get(i).Service_code;
			}
		}
		if( Button != null && Button.compareTo("ServPrev") == 0 ) {
			int i;
			if(Appsl.size() > 0 ) {
				for( i = Servsl.size() - 1; i > 0 ; --i)
					if(  Servsl.get(i).Service_code < AppServServlet.SelectedServ ) break;
				if( i >= 0)					AppServServlet.SelectedServ = Servsl.get(i).Service_code;
			}
		}
		sReq = sReq.trim();
		if( sReq != null ){
			if( sReq.compareToIgnoreCase("AppForm") == 0 ){
				Doc = AppFormXML(Request);
			}
			else if( sReq.compareToIgnoreCase("ServForm") == 0 ){
				Doc = ServFormXML(Request);
			}
			else if( sReq.compareToIgnoreCase("AppUpdateForm") == 0 ){
				Doc = AppUpdateFormXML(Request, Button );
			}
			else if( sReq.compareToIgnoreCase("ServUpdateForm") == 0 ){
				Doc = ServUpdateFormXML(Request, Button );
			}
			else if( sReq.compareToIgnoreCase("AppInsertForm") == 0 ){
				Doc = AppInsertFormXML(Request, Button);
			}
			else if( sReq.compareToIgnoreCase("AppSolrForm") == 0 ){
				Doc = AppSolrFormXML(Request);
			}
			else if( sReq.compareToIgnoreCase("ServInsertForm") == 0 ){
				Doc = ServInsertFormXML(Request, Button);
			}
			else if( sReq.compareToIgnoreCase("ServSolrForm") == 0 ){
				Doc = ServSolrFormXML(Request);
			}
			else{
				Doc = super.doRequest(Request, Response);
			}
		}
		else	Doc = super.doRequest(Request, Response);

		AppServServlet.ServiceProc = false;

		return Doc;
	}
	
	public int doInputRequest( int iOper, HttpServletRequest Request) throws ExceptionIS 
	{
		int iRet = 0;
		if( iOper == 50 ){
		}
		return iRet;
	}
	public DOMData AppFormXML( HttpServletRequest Request) throws ExceptionIS {
		DOMData Doc = getTemplate();
		if( Appsl == null ) return Doc;
		
		Node Root = Doc.getRootNode();
		Node AppsNode = Doc.createChildNode( Root, "Application");
		
		for( int i = 0; i < Appsl.size(); ++i){
			Appl app = Appsl.get(i);
			if( app.App_code ==  AppServServlet.SelectedApp ) {
				Doc.addChildNode( AppsNode, "App_code", app.App_code);
				Doc.addChildNode( AppsNode, "Name", app.Name);
				Doc.addChildNode( AppsNode, "App_group", app.App_group);
				Doc.addChildNode( AppsNode, "App_type", app.App_type);
				Doc.addChildNode( AppsNode, "Description", app.Description);
				Doc.addChildNode( AppsNode, "App_cost", app.App_cost);
				Doc.addChildNode( AppsNode, "Last_modified", app.Last_modified);
			}
		}
		return Doc;
	}

	public DOMData AppSolrFormXML( HttpServletRequest Request) {
		DOMData Doc = null;
		try {
			Doc = getTemplate();
		} catch (ExceptionIS e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Node Root = Doc.getRootNode();

		String Pattern = Request.getParameter( "Pattern");

		if( Pattern == null || Pattern.length()  < 1 )	return Doc;
		else {
			Node SolrNode = Doc.createChildNode( Root, "Solr");		
			if(AppServServlet.SolrIsReady) {
				Doc.addChildNode( SolrNode, "IsReady", 1 );
				Doc.addChildNode( SolrNode, "Pattern", Pattern );
				try {
					AppServServlet.solrClientApps.commit();
				} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
				}
//			    System.out.println("Querying by using SolrQuery...");
			    SolrQuery solrQuery = new SolrQuery("Name:" + Pattern);
			    solrQuery.addField("id");
			    solrQuery.addField("App_code");
			    solrQuery.addField("Name");
			    solrQuery.setSort("App_code", ORDER.asc);
			    solrQuery.setRows(10);
		        QueryResponse response = null;
		        try {
		            response = AppServServlet.solrClientApps.query(solrQuery);
		        } catch (SolrServerException | IOException e) {
		            System.err.printf("\nFailed to search articles: %s", e.getMessage());
		        }
		        SolrDocumentList documents = null;
			    if (response != null) {
		            documents = response.getResults();
//		            System.out.printf("Found %d documents\n", documents.getNumFound());
		            for (SolrDocument document : documents) {
		    			Node AppsNode = Doc.createChildNode( SolrNode, "Application");		
		                String id = (String) document.getFirstValue("id");
		                String name = (String) document.getFirstValue("Name");
		                long App_code1 = (long) document.getFirstValue("App_code");
						Doc.addChildNode( AppsNode, "App_code", App_code1);
						Doc.addChildNode( AppsNode, "Name", name );
//		                System.out.printf("id=%s, App_code=%s, name=%s\n", id, App_code1, name);
		            } 
			    }
			}
			else {
				Doc.addChildNode( SolrNode, "IsReady", 0 );
			}
		}
		return Doc;
	}

	public DOMData AppUpdateFormXML( HttpServletRequest Request, String Button) throws ExceptionIS {
		DOMData Doc = getTemplate();
	
		if( Appsl == null ) return Doc;
		
		Node Root = Doc.getRootNode();
		Node AppsNode = Doc.createChildNode( Root, "Application");
	
		if( Button != null ) {
			if( Button.compareTo("AppUpdate") == 0 ) {
				for( int i = 0; i < Appsl.size(); ++i){
					Appl app = Appsl.get(i);
					if( app.App_code ==  AppServServlet.SelectedApp ) {
						Doc.addChildNode( AppsNode, "App_code", app.App_code);
						Doc.addChildNode( AppsNode, "Name", app.Name);
						Doc.addChildNode( AppsNode, "App_group", app.App_group);
						Doc.addChildNode( AppsNode, "App_type", app.App_type);
						Doc.addChildNode( AppsNode, "Description", app.Description);
						Doc.addChildNode( AppsNode, "App_cost", app.App_cost);
						Doc.addChildNode( AppsNode, "Last_modified", app.Last_modified);
					}
				}
			}
		}
		else {
			String App_code = Request.getParameter( "App_code").trim();
			String Name = Request.getParameter( "Name").trim();
			String Desc = Request.getParameter( "Description").trim();
			String App_cost = Request.getParameter( "App_cost").trim();
			String App_group = Request.getParameter( "App_group").trim();
			String App_type = Request.getParameter( "App_type").trim();
			String Last_modified = Request.getParameter( "Last_modified").trim();

			if( Last_modified.length()  < 1 ){
				LocalDate date = LocalDate.now();
				Last_modified = date.toString();
			}
			

			if(App_code.length() < 1 )	App_code = String.valueOf(AppServServlet.SelectedApp);
			if(App_cost.length() < 1 )	App_cost = "0.0";
			
			String Query = "DELETE FROM application WHERE app_code = " + App_code + ";";
/*
			System.out.println("App_code = " + App_code  + 
				" Name  = " + Name +
				" Description  = " + Desc +
				" Last_modified  = " + Last_modified );
*/		
//			System.out.println ( Query );
			AppServServlet.getSight().Db.exec( Query );

			Query = "INSERT INTO application (App_code, Name, App_group , App_type, Description, App_cost, Last_modified ) \nVALUES ( ";
			Query = Query + App_code;
			Query = Query + ", '" + Name + "'";
			Query = Query + ", '" +  App_group + "'";
			Query = Query + ", '" +  App_type + "'";
			Query = Query + ",  '" + Desc + "'";
			Query = Query + ", " +  App_cost;
			Query = Query + ", '" +  Last_modified  + "' ); ";
		
//			System.out.println ( Query );
			AppServServlet.getSight().Db.exec( Query );

			for( int i = SolrAppsl.size() - 1; i != 0; --i){
				SolrApps app = SolrAppsl.get(i);
				if(app.App_code.compareTo( String.valueOf(AppServServlet.SelectedApp)) == 0 ) {
					try {
						AppServServlet.solrClientApps.deleteById(app.id);
					} catch (SolrServerException | IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}

					app.Name = Name;
					try {
						AppServServlet.solrClientApps.addBean(app);
					} catch (SolrServerException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			for( int i = 0; i < Appsl.size(); ++i){
				Appl app = Appsl.get(i);
				if( app.App_code ==  AppServServlet.SelectedApp ) {
					app.Name = Name;
					app.App_group = App_group;
					app.App_type = App_type;
					app.Description = Desc;
					app.App_cost = Float.parseFloat(App_cost);
					app.Last_modified = Last_modified;
					Doc.addChildNode( AppsNode, "App_code", app.App_code);
					Doc.addChildNode( AppsNode, "Name", app.Name);
					Doc.addChildNode( AppsNode, "App_group", app.App_group);
					Doc.addChildNode( AppsNode, "App_type", app.App_type);
					Doc.addChildNode( AppsNode, "Description", app.Description);
					Doc.addChildNode( AppsNode, "App_cost", app.App_cost);
					Doc.addChildNode( AppsNode, "Last_modified", app.Last_modified);
				}
			}

		}
		return Doc;
	}

	public Appl NewAppl() {
		Appl app = new Appl();
		
		app.App_code  = GetMaxApp() + 1;
		Appsl.add(app);
		SortApps();

		AppServServlet.SelectedApp = app.App_code;

		Serv serv = new Serv();
		serv.App_code  = AppServServlet.SelectedApp;
		serv.Service_code = 0;
		if(  Servsl != null )	Servsl.clear();
		else 					Servsl = new ArrayList<Serv>();
		Servsl.add(serv);

		AppServServlet.SelectedServ = serv.Service_code;
		
		return app;
		
	}
	
	public DOMData AppInsertFormXML( HttpServletRequest Request, String Button) throws ExceptionIS {
		DOMData Doc = getTemplate();

		if( Appsl == null ) return Doc;
		
		Node Root = Doc.getRootNode();
		Node AppsNode = Doc.createChildNode( Root, "Application");
		
		if( Button != null ) {
			if( Button.compareTo("AppInsert") == 0 ) 
				Doc.addChildNode( AppsNode, "App_code", NewAppl().App_code);
		}
		else {
			String App_code = Request.getParameter( "App_code").trim();
			String Name = Request.getParameter( "Name").trim();
			String Desc = Request.getParameter( "Description").trim();
			String App_cost = Request.getParameter( "App_cost").trim();
			String App_group = Request.getParameter( "App_group").trim();
			String App_type = Request.getParameter( "App_type").trim();
			String Last_modified = Request.getParameter( "Last_modified").trim();
			
			Appl app = null;
			for( int i = 0; i < Appsl.size(); ++i) {
				app = Appsl.get(i);
				if(  app.App_code == Integer.parseInt(App_code) ) 
					break;
			}
		
			if( !app.InDb ) {
				String Query = "INSERT INTO application (App_code, Name, App_group , App_type, Description, App_cost, Last_modified ) \nVALUES ( ";

				if( Last_modified.length()  < 1 ){
					LocalDate date = LocalDate.now();
					Last_modified = date.toString();
				}
				
				if( App_cost.length()  < 1 )	App_cost = "0";
				
				Query = Query + App_code;
				Query = Query + ", '" + Name + "'";
				Query = Query + ", '" +  App_group + "'";
				Query = Query + ", '" +  App_type + "'";
				Query = Query + ",  '" + Desc + "'";
				Query = Query + ", " +  App_cost;
				Query = Query + ", '" +  Last_modified  + "' ); ";
			
//				System.out.println ( Query );
				AppServServlet.getSight().Db.exec( Query );

				app.InDb = true;
				
				SolrApps solappl = new SolrApps();
			    int int_random = rand.nextInt(1000); 
			    solappl.id = String.format("%010d", int_random );
			    solappl.App_code = String.valueOf( App_code);
			    solappl.Name = Name;
			    SolrAppsl.add(solappl);
			    try {
					AppServServlet.solrClientApps.addBean(solappl);
				} catch (IOException | SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for( int i1 = 0; i1 < Appsl.size(); ++i1){
					app = Appsl.get(i1);
					if( app.App_code ==  AppServServlet.SelectedApp ) {
						app.Name = Name;
						app.App_group = App_group;
						app.App_type = App_type;
						app.Description = Desc;
						app.App_cost = Float.parseFloat(App_cost);
						app.Last_modified = Last_modified;
						Doc.addChildNode( AppsNode, "App_code", app.App_code);
						Doc.addChildNode( AppsNode, "Name", app.Name);
						Doc.addChildNode( AppsNode, "App_group", app.App_group);
						Doc.addChildNode( AppsNode, "App_type", app.App_type);
						Doc.addChildNode( AppsNode, "Description", app.Description);
						Doc.addChildNode( AppsNode, "App_cost", app.App_cost);
						Doc.addChildNode( AppsNode, "Last_modified", app.Last_modified);
					}
				}
			}
			else 
				Doc.addChildNode( AppsNode, "App_code", NewAppl().App_code);
		}
		return Doc;
	}

	public DOMData ServFormXML( HttpServletRequest Request) throws ExceptionIS {
			DOMData Doc = getTemplate();
			if( Servsl == null ) return Doc;
			
			Node Root = Doc.getRootNode();
			Node ServsNode = Doc.createChildNode( Root, "Service");
			
			for( int i = 0; i < Servsl.size(); ++i){
				Serv serv = Servsl.get(i);
				if(  serv.Service_code == AppServServlet.SelectedServ ) {
					Doc.addChildNode( ServsNode, "App_code", serv.App_code);
					Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
					Doc.addChildNode( ServsNode, "Name", serv.Name);
					Doc.addChildNode( ServsNode, "Type", serv.Type);
					Doc.addChildNode( ServsNode, "Sub_type", serv.Sub_type);
					Doc.addChildNode( ServsNode, "Description", serv.Description);
					Doc.addChildNode( ServsNode, "Last_modified", serv.Last_modified);
				}
			}

		return Doc;
	}

	public DOMData ServSolrFormXML( HttpServletRequest Request) {
		DOMData Doc = null;
		try {
			Doc = getTemplate();
		} catch (ExceptionIS e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Node Root = Doc.getRootNode();

		String Pattern = Request.getParameter( "Pattern");

		if( Pattern == null ||  Pattern.length()  < 1 )	return Doc;
		else {
			Node SolrNode = Doc.createChildNode( Root, "Solr");		
			if(AppServServlet.SolrIsReady) {
				Doc.addChildNode( SolrNode, "IsReady", 1 );
				Doc.addChildNode( SolrNode, "Pattern", Pattern );
				try {
					AppServServlet.solrClientServs.commit();
				} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
				}
//			    System.out.println("Querying by using SolrQuery...");
			    SolrQuery solrQuery = new SolrQuery("Name:" + Pattern);
			    solrQuery.addField("id");
			    solrQuery.addField("App_code");
			    solrQuery.addField("Service_code");
			    solrQuery.addField("Name");
			    solrQuery.setSort("App_code", ORDER.asc);
			    solrQuery.setRows(10);
		        QueryResponse response = null;
		        try {
		            response = AppServServlet.solrClientServs.query(solrQuery);
		        } catch (SolrServerException | IOException e) {
		            System.err.printf("\nFailed to search articles: %s", e.getMessage());
		        }
		        SolrDocumentList documents = null;
			    if (response != null) {
		            documents = response.getResults();
//		            System.out.printf("Found %d documents\n", documents.getNumFound());
		            for (SolrDocument document : documents) {
		    			Node ServNode = Doc.createChildNode( SolrNode, "Service");		
		                String id = (String) document.getFirstValue("id");
		                String name = (String) document.getFirstValue("Name");
		                long App_code = (long) document.getFirstValue("App_code");
	                	long Service_code = (long) document.getFirstValue("Service_code");
						Doc.addChildNode( ServNode, "Service_code", Service_code);
						Doc.addChildNode( ServNode, "App_code", App_code);
						Doc.addChildNode( ServNode, "Name", name );
//		                System.out.printf("id=%s, App_code=%s, Service_code=%s  name=%s\n", id, App_code, Service_code, name);
		            } 
			    }
			}
			else {
				Doc.addChildNode( SolrNode, "IsReady", 0 );
			}
		}
	return Doc;
}
public DOMData ServUpdateFormXML( HttpServletRequest Request, String Button ) throws ExceptionIS {
		DOMData Doc = getTemplate();
		Node Root = Doc.getRootNode();
		Node ServsNode = Doc.createChildNode( Root, "Service");
		
		if( Button != null ) {
			if( Button.compareTo("servUpdate") == 0 ) {
				for( int i = 0; i < Servsl.size(); ++i){
					Serv serv = Servsl.get(i);
					if(  serv.Service_code == AppServServlet.SelectedServ ) {
						Doc.addChildNode( ServsNode, "App_code", serv.App_code);
						Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
						Doc.addChildNode( ServsNode, "Name", serv.Name);
						Doc.addChildNode( ServsNode, "Type", serv.Type);
						Doc.addChildNode( ServsNode, "Sub_type", serv.Sub_type);
						Doc.addChildNode( ServsNode, "Description", serv.Description);
						Doc.addChildNode( ServsNode, "Last_modified", serv.Last_modified);
					}
				}
			}
		}
		else {
			String App_code = Request.getParameter( "App_code");
			String Service_code = Request.getParameter( "Service_code");
			String Name = Request.getParameter( "Name");
			String Desc = Request.getParameter( "Description");
			String Type = Request.getParameter( "Type");
			String Sub_type = Request.getParameter( "Sub_type");
			String Last_modified = Request.getParameter( "Last_modified");

			if( Last_modified.length()  < 1 ){
				LocalDate date = LocalDate.now();
				Last_modified = date.toString();
			}
			
			if(Service_code.length() < 1 ) {			
					Serv serf = new Serv();
					serf.App_code  = AppServServlet.SelectedApp;
					serf.Service_code = AppServServlet.SelectedServ;
					serf.Name = Name;
					serf.Type = Type;
					serf.Sub_type = Sub_type;
					serf.Description = Desc;

					if( Last_modified.length()  < 1 ){
						LocalDate date = LocalDate.now();
						Last_modified = date.toString();
					}

					serf.Last_modified = Last_modified;
					Servsl.add(serf);
					SortServs();

					String Query = "INSERT INTO App_service ( Service_code, App_code, Name, Type , Sub_type, Description, Last_modified ) \nVALUES ( ";

					Query = Query + AppServServlet.SelectedServ;
					Query = Query + ", " +  AppServServlet.SelectedApp;;
					Query = Query + ", '" +  Name + "'";
					Query = Query + ", '" +  Type + "'";
					Query = Query + ", '" + Sub_type + "'";
					Query = Query + ", '" +  Desc + "'";
					Query = Query + ", '" +  Last_modified  + "' ); ";
					
//					System.out.println ( Query );
					AppServServlet.getSight().Db.exec( Query );

					SolrServs solserv = new SolrServs();
				    int int_random = rand.nextInt(1000); 
				    solserv.id = String.format("%010d", int_random );
				    solserv.App_code = String.valueOf( AppServServlet.SelectedApp) ;
				    solserv.Service_code = String.valueOf( AppServServlet.SelectedServ );
				    solserv.Name = Name;
					SolrServsl.add( solserv );
				    try {
						AppServServlet.solrClientServs.addBean(solserv);
					} catch (IOException | SolrServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
					for( int i = 0; i < Servsl.size(); ++i){
						Serv serv = Servsl.get(i);
						if(  serv.Service_code == AppServServlet.SelectedServ ) {
							serv.Name = Name;
							serv.Type = Type;
							serv.Sub_type = Sub_type;
							serv.Description = Desc;
							serv.Last_modified = Last_modified;
							
							Doc.addChildNode( ServsNode, "App_code", serv.App_code);
							Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
							Doc.addChildNode( ServsNode, "Name", serv.Name);
							Doc.addChildNode( ServsNode, "Type", serv.Type);
							Doc.addChildNode( ServsNode, "Sub_type", serv.Sub_type);
							Doc.addChildNode( ServsNode, "Description", serv.Description);
							Doc.addChildNode( ServsNode, "Last_modified", serv.Last_modified);
						}
					}
			}
			else {
				String Query = "UPDATE app_service SET ";
				Query = Query + "name = '" + Name + "' , description = '" + Desc + "'";
				Query = Query + ", Type = '" +  Type + "'";
				Query = Query + ", Sub_type ='" +  Sub_type + "'";
				Query = Query + ", last_modified = '" +  Last_modified  + "' ";
				Query = Query + "WHERE app_code = " + AppServServlet.SelectedApp + " and Service_code = " + AppServServlet.SelectedServ ;
				
//				System.out.println ( Query );
				AppServServlet.getSight().Db.exec( Query );
				
				for( int i = SolrServsl.size() - 1; i != 0; --i){
					SolrServs serv = SolrServsl.get(i);
					if(serv.App_code.compareTo( String.valueOf(AppServServlet.SelectedApp)) == 0 && 
							serv.Service_code.compareTo( String.valueOf(AppServServlet.SelectedServ)) == 0  ) {
						try {
							AppServServlet.solrClientServs.deleteById(serv.id);
						} catch (SolrServerException | IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
						serv.App_code = String.valueOf( App_code);
						serv.Service_code = String.valueOf( Service_code);
						serv.Name = Name;
						try {
							AppServServlet.solrClientServs.addBean(serv);
						} catch (SolrServerException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				for( int i = 0; i < Servsl.size(); ++i){
					Serv serv = Servsl.get(i);
					if(  serv.Service_code == AppServServlet.SelectedServ ) {
						serv.Name = Name;
						serv.Type = Type;
						serv.Sub_type = Sub_type;
						serv.Description = Desc;
						serv.Last_modified = Last_modified;
					
						Doc.addChildNode( ServsNode, "App_code", serv.App_code);
						Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
						Doc.addChildNode( ServsNode, "Name", serv.Name);
						Doc.addChildNode( ServsNode, "Type", serv.Type);
						Doc.addChildNode( ServsNode, "Sub_type", serv.Sub_type);
						Doc.addChildNode( ServsNode, "Description", serv.Description);
						Doc.addChildNode( ServsNode, "Last_modified", serv.Last_modified);
					}
				}

			}
		}

		return Doc;
	}

	public DOMData ServInsertFormXML( HttpServletRequest Request, String Button) throws ExceptionIS {
		DOMData Doc = getTemplate();
		if( Servsl == null ) return Doc;
		Node Root = Doc.getRootNode();
		Node ServsNode = Doc.createChildNode( Root, "Service");
		

		if( Button != null ) {
			if( Button.compareTo("ServInsert") == 0 ) {
				Serv serv = new Serv();
				serv.App_code  = AppServServlet.SelectedApp;
				serv.Service_code = GetMaxServ() + 1;
				AppServServlet.SelectedServ = serv.Service_code;
				Servsl.add(serv);
				SortServs();
				Doc.addChildNode( ServsNode, "App_code", serv.App_code);
				Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
			}
		}
		else {
			String App_code = Request.getParameter( "App_code").trim();
			String Service_code = Request.getParameter( "Service_code").trim();
			String Name = Request.getParameter( "Name").trim();
			String Desc = Request.getParameter( "Description").trim();
			String Type = Request.getParameter( "Type").trim();
			String Sub_type = Request.getParameter( "Sub_type").trim();
			String Last_modified = Request.getParameter( "Last_modified").trim();
			
			Serv serv = null;
			for( int i = 0; i < Servsl.size(); ++i) {
				serv = Servsl.get(i);
				if(  serv.Service_code == Integer.parseInt(Service_code) ) 
					break;
			}
		
			if( !serv.InDb ) {
				String Query = "INSERT INTO App_service ( Service_code, App_code, Name, Type , Sub_type, Description, Last_modified ) \nVALUES ( ";
				if( Last_modified.length()  < 1 ){
					LocalDate date = LocalDate.now();
					Last_modified = date.toString();
				}

				Query = Query + Service_code;
				Query = Query + ", " + App_code;
				Query = Query + ", '" +  Name + "'";
				Query = Query + ", '" +  Type + "'";
				Query = Query + ", '" + Sub_type + "'";
				Query = Query + ", '" +  Desc + "'";
				Query = Query + ", '" +  Last_modified  + "' ); ";
				
//				System.out.println ( Query );
				AppServServlet.getSight().Db.exec( Query );

				serv.InDb = true;
				
				SolrServs solserv = new SolrServs();
			    int int_random = rand.nextInt(1000); 
			    solserv.id = String.format("%010d", int_random );
			    solserv.App_code = String.valueOf( App_code);
			    solserv.Service_code = String.valueOf( Service_code);
			    solserv.Name = Name;
			    SolrServsl.add(solserv);
			    try {
					AppServServlet.solrClientServs.addBean(solserv);
				} catch (IOException | SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
				for( int i = 0; i < Servsl.size(); ++i){
					serv = Servsl.get(i);
					if(  serv.Service_code == AppServServlet.SelectedServ ) {
						serv.Name = Name;
						serv.Type = Type;
						serv.Sub_type = Sub_type;
						serv.Description = Desc;
						serv.Last_modified = Last_modified;
						
						Doc.addChildNode( ServsNode, "App_code", serv.App_code);
						Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
						Doc.addChildNode( ServsNode, "Name", serv.Name);
						Doc.addChildNode( ServsNode, "Type", serv.Type);
						Doc.addChildNode( ServsNode, "Sub_type", serv.Sub_type);
						Doc.addChildNode( ServsNode, "Description", serv.Description);
						Doc.addChildNode( ServsNode, "Last_modified", serv.Last_modified);
					}
				}
			}
			else {
				serv = new Serv();
				serv.App_code  = AppServServlet.SelectedApp;
				serv.Service_code = GetMaxServ() + 1;
				AppServServlet.SelectedServ = serv.Service_code;
				Servsl.add(serv);
				SortServs();
				Doc.addChildNode( ServsNode, "App_code", serv.App_code);
				Doc.addChildNode( ServsNode, "Service_code", serv.Service_code);
			}
		}

		return Doc;
	}

	public boolean isValidStart( HttpServletRequest Request)
	{
		return true;
	}
	public boolean isVisible()
	{
		return true;
	}
	public boolean isLive() {
		return false;
	}


	   public static class SolrServs {
	        @Field
	        private String 	id;
	        @Field
	        private String 	App_code;
	        @Field
	        private String 	Service_code;
	        @Field
	        private String 	Name;

	        public SolrServs() {
	        }

	   }

	   public static class SolrApps {
	        @Field
	        private String 	id;
	        @Field
	        private String 	App_code;
	        @Field
	        private String 	Name;

	        public SolrApps() {
	        }
	   }

}

