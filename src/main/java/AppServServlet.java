import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrClient;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.or.is.DOMData;
import ee.or.is.ISServlet;
import ee.or.is.MException;
import ee.or.is.OptionsList;
import ee.or.is.Sight;

public class AppServServlet extends ISServlet 
{
	private static final long serialVersionUID = 1L;
	private static AppServSight sightG;
	private static Sight aSight = null;

	public static Boolean	ServiceProc = false;
	
	public static Appl		apps = null;
	public static Serv		servs = null;

	public static int		SelectedApp  = 0;
	public static int		SelectedServ = 0;

	   static SolrClient solrClientApps = null;
	   static SolrClient solrClientServs = null;
	   static Boolean	SolrIsReady = false;
	private NodeList slist;
	   

	public void initSight() // throws MException
	{
		sightG = new AppServSight( null, this, getConfig());
		sightG.setTitle( "AppServ rakendus");
	}
 	public void init( ServletConfig servconf) throws ServletException
	{
		System.out.println("************** AppServ Client Servlet init started ************************");
		String sConfigFile = servconf.getInitParameter( "config_file");
		if( sConfigFile != null) setConfigName( sConfigFile);
		super.init( servconf);
		log( "\n************** AppServ Client Servlet init start ************************");
		initSight();
		if( appconfig != null ) {
			Node configroot = appconfig.Doc.getDocumentElement();
			NodeList list =  ((Element) configroot).getElementsByTagName("solr");
			NodeList slist =  list.item( 0).getChildNodes();
			for( int i = 0; i < slist.getLength(); i++ ) {
				Node snode = slist.item( i );
				String sName = snode.getNodeName();
				String sValue = snode.getTextContent();
//				System.out.println("sName = " + sName + " sValue = " + sValue );
				if( sName.equals("localhost") )
					sightG.Localhost = sValue;
				if( sName.equals("node1") )
					sightG.Node1 = sValue;
				if( sName.equals("node2") )
					sightG.Node2 = sValue;
			}
		}
		sightG.init(); // selle kutsus v�lja Sight konstruktor, kuid sinna ta ei sobi

        if( sightG.Db != null ){
//			sightG.setUserStat( sightG.Db.hasTable( "stat_user"));

        	sightG.rand = new Random();
        	
			apps   = new Appl();
			apps.createDataObjects(this.getSight().Db);
			System.out.println( "apps    size = " + apps.getDataObjects().size() );
			sightG.TodAppsl( apps.getDataObjects() );
			System.out.println( "Apps    size = " + sightG.Appsl.size() );

			servs   = new Serv(true);
			servs.createDataObjects(AppServServlet.getSight().Db);
			System.out.println( "allservs   size = " + servs.getDataObjects().size() );
			sightG.ToSolrlServs( servs.getDataObjects() );
			System.out.println( "SOlrServs   size = " + sightG.SolrServsl.size()  );

			servs   = new Serv(false);
			servs.createDataObjects(AppServServlet.getSight().Db);
			System.out.println( "servs   size = " + servs.getDataObjects().size() );
			sightG.ToServs( servs.getDataObjects() );
			System.out.println( "Servs   size = " + sightG.Servsl.size()  );

		}

		log( "\n************** AppServClient Servlet init end ************************");
	}

 	public Sight createSight( HttpServletRequest aRequest) {
 		if(aSight == null ) {
 			try{
 				aSight = sightG.getMySight( aRequest);
 			}catch( MException e ){
 				
 			}
 		}
//		System.out.println( "createSight" );
		return aSight;
	}
 	public static Sight getSight() {
//		System.out.println( "getSight" );
		return sightG;
	}
 	public void destroy() {
		log( "\n************** AppServClient Servlet destroy start ************************");
		super.destroy();
		log( "\n************** AppServClient Servlet destroy end ************************");
	}
	public void reload()
	{
        createLogCatalog();
//		closeTimeoutSights( (new Date()).getTime());
 	}
	public String getRequest( HttpServletRequest aRequest) 
	{
		String sReq = super.getRequest( aRequest);
		if( sReq == null) sReq = "Main";
		return sReq;
	}
}
