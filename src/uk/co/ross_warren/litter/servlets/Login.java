package uk.co.ross_warren.litter.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;

/**
 * Servlet implementation class Login
 */
import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.UserConnector;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * heavily based on sample code by Michael Liao at:
 * http://code.google.com/p/jopenid/
 * Sample servlet using JOpenID.
 * 
 * @author Andy Cobley ()
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */

	static final long ONE_HOUR = 3600000L;
    static final long TWO_HOUR = ONE_HOUR * 2L;
    static final String ATTR_MAC = "openid_mac";
    static final String ATTR_ALIAS = "openid_alias";

    private OpenIdManager manager;

    public Login() {
        super();
        // TODO Auto-generated constructor stub
        
        manager = new OpenIdManager();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		session.setAttribute("ReturnPoint", request.getHeader("referer"));
		String sRealm="http://"+request.getServerName()+":"+request.getServerPort()+"/";
		System.out.println("srealm: "+sRealm);
		manager.setRealm(sRealm);
        manager.setReturnTo(sRealm+"Litter/Login");
        StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);
		String op=null;
		
		switch (args.length){
			case 3: op=args[2];
					break;
		}

        if (op==null) {
	            // check sign on result from Google or Yahoo:
        	try{
	        		checkNonce(request.getParameter("openid.response_nonce"));
	        	}catch (Exception et){
	        		System.out.println("Check Nonce failed "+et);
	        		response.sendRedirect("/Litter/");
	        		return;
	        	}
	            // get authentication:
	            byte[] mac_key = (byte[]) request.getSession().getAttribute(ATTR_MAC);
	            String alias = (String) request.getSession().getAttribute(ATTR_ALIAS);
	            Authentication authentication = manager.getAuthentication(request, mac_key, alias);
	            if (authentication==null) {
	            	response.sendRedirect("/Litter/");
	            }
	            response.setContentType("text/html; charset=UTF-8");
	            showAuthentication(request,response, authentication);
	            return;
	        }
	        else if (op.equals("Google") || op.equals("Yahoo")) {
	            // redirect to Google or Yahoo sign on page:
	            Endpoint endpoint = manager.lookupEndpoint(op);
	            Association association = manager.lookupAssociation(endpoint);

	            request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
	            request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
	            String url = manager.getAuthenticationUrl(endpoint, association);
	            System.out.println(url);
	            response.sendRedirect(url);
	        }
	        else {
	            throw new ServletException("Unsupported OP: " + op);
	        }

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void showAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc==null){
			lc= new UserStore();	
			session.setAttribute("User", lc);
			//response.sendRedirect("/nosession.jsp?Page=ChecklDapLogin");
		}
		String Email=auth.getEmail();
		lc.login(Email);
		lc.setName(auth.getFullname());
		System.out.println("Login "+lc.getEmail());
		//Check to see if user is registered,  You can login but not be registered
		UserConnector au = new UserConnector();
		UserStore ars=au.getUserByEmail(Email);
		if (ars == null){
			System.out.println("Not Registered");
			System.out.flush();
			try{
				response.sendRedirect("RegisterUser.jsp");
			}catch(Exception et){
				System.out.println("Can't forward in login servlet"+et);
			}
		} else {
		//Now we are logged in and registered then we can set them both
		
			lc.login(Email);
			lc.setUserName(ars.getUserName());
			lc.setAvatar(ars.getAvatarUrl());
			lc.setBio(ars.getBio());
			System.out.println("Login: "+lc.getEmail());
			//System.out.println(request.getContextPath());
			
			String returnTo = (String)session.getAttribute("ReturnPoint");
			try{
				response.sendRedirect(returnTo);
			}catch(Exception et){
				System.out.println("Can't forward in login servlet: "+et);
			}		
		}
		
    }

    void checkNonce(String nonce) {
        // check response_nonce to prevent replay-attack:
        if (nonce==null || nonce.length()<20)
            throw new OpenIdException("Verify failed.");
        // make sure the time of server is correct:
        long nonceTime = getNonceTime(nonce);
        long diff = Math.abs(System.currentTimeMillis() - nonceTime);
        if (diff > ONE_HOUR)
            throw new OpenIdException("Bad nonce time.");
        if (isNonceExist(nonce))
            throw new OpenIdException("Verify nonce failed.");
        storeNonce(nonce, nonceTime + TWO_HOUR);
    }

    // simulate a database that store all nonce:
    private Set<String> nonceDb = new HashSet<String>();

    // check if nonce is exist in database:
    boolean isNonceExist(String nonce) {
        return nonceDb.contains(nonce);
    }

    // store nonce in database:
    void storeNonce(String nonce, long expires) {
        nonceDb.add(nonce);
    }

    long getNonceTime(String nonce) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .parse(nonce.substring(0, 19) + "+0000")
                    .getTime();
        }
        catch(ParseException e) {
            throw new OpenIdException("Bad nonce time.");
        }
    }

}