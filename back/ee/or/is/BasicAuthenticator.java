package ee.or.is;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BasicAuthenticator extends Authenticator 
{    
		private String username;    
		private String password;  
		private String sSessionId;
		public String getSessionId(){ return sSessionId;}
		public void setSessionId( String sSessionId){ this.sSessionId = sSessionId;}
		
		public BasicAuthenticator() 
		{        
			super();       
		}    
		public BasicAuthenticator(String username, String password) 
		{        
			super();       
			this.username = username;        
			this.password = password;    
		}    
		public PasswordAuthentication getPasswordAuthentication() {           
	// LogOR.info( "MsgToServlet: " + username);
		    return new PasswordAuthentication(username,password.toCharArray());    
		}
}
