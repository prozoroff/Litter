package uk.co.ross_warren.litter.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.prettyprint.cassandra.service.CassandraHost;
import uk.co.ross_warren.litter.Utils.CassandraHosts;
import uk.co.ross_warren.litter.Utils.StringSplitter;
import uk.co.ross_warren.litter.connectors.UserConnector;
import uk.co.ross_warren.litter.stores.CassandraStore;
import uk.co.ross_warren.litter.stores.UserStore;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Admin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session=request.getSession();
		UserStore admin = (UserStore)session.getAttribute("User");
		Boolean usercheck = false;
		if (admin.getUserName().equals("Admin") || admin.getUserName().equals(CassandraStore.instance().getAdmin()))
		{
			usercheck = true;
		}
		if (admin == null || !usercheck)
		{
			return;
		}
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);
		PrintWriter pw = response.getWriter();
		if (args.length == 3)
		{
			if (args[2].equals("Host")) pw.print(CassandraStore.instance().getHost());
			else if (args[2].equals("Cluster")) pw.print(CassandraStore.instance().getClusterName());
			else if (args[2].equals("Port")) pw.print(CassandraStore.instance().getPort());
			else if (args[2].equals("Test"))
			{
				UserConnector connect = new UserConnector();
				pw.print(connect.connectionTest());
			} else if (args[2].equals("Pool"))
			{
				CassandraHosts hosts = new CassandraHosts();
				java.util.List<CassandraHost> poolhosts = hosts.getPoolHosts();
				request.setAttribute("Data", poolhosts);
				RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
				rdjson.forward(request,response);
			} else if (args[2].equals("Down"))
			{
				CassandraHosts hosts = new CassandraHosts();
				java.util.List<CassandraHost> poolhosts = hosts.getDownedPoolHosts();
				request.setAttribute("Data", poolhosts);
				RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
				rdjson.forward(request,response);
			}
		}
		else
		{
			try
			{
				RequestDispatcher rd=request.getRequestDispatcher("/Admin.jsp");
				rd.forward(request, response);
			}
			catch(Exception e)
			{
				System.out.println("Problem forwarding to admin page");
			}
		}
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		StringSplitter split = new StringSplitter();
		String args[]=split.SplitRequestPath(request);
		HttpSession session=request.getSession();
		UserStore admin = (UserStore)session.getAttribute("User");
		Boolean usercheck = false;
		if (admin.getUserName().equals("Admin") || admin.getUserName().equals(CassandraStore.instance().getAdmin()))
		{
			usercheck = true;
		}
		if (admin == null || !usercheck)
		{
			return;
		}
		
		if (args.length == 4)
		{
			if (args[2].equals("Host")) CassandraStore.instance().setHost(args[3]);
			if (args[2].equals("Cluster")) CassandraStore.instance().setClusterName(args[3]);
			if (args[2].equals("Port")) CassandraStore.instance().setPort(args[3]);
		}
	}

}
