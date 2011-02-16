<%-- Instantiate the form validation bean and supply the error message map --%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="uk.co.ross_warren.litter.stores.*" %>
<%@ page import="java.util.*" %>
<jsp:useBean id="form" class="uk.co.ross_warren.litter.stores.ValidationStore" scope="request">
    <jsp:setProperty name="form" property="errorMessages" value='<%= errorMap %>'/>
</jsp:useBean>

<%
    // If process is true, attempt to validate and process the form
    if ("true".equals(request.getParameter("process"))) {
%>
        <jsp:setProperty name="form" property="*" />
<%
		if (request.getParameter("Name") != null)
		{
			form.setName(request.getParameter("Name"));
		}
		if (request.getParameter("Username") != null)
		{
			form.setUserName(request.getParameter("Username"));
		}
        // Attempt to process the form
        if (form.process()) {
            // Go to success page
            RequestDispatcher rd;
            rd=request.getRequestDispatcher("User");
			rd.forward(request,response);
            return;
        }
    }
%>

<!DOCTYPE html> <!-- The new doctype -->
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter</title>
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link href='http://fonts.googleapis.com/css?family=Chewy' rel='stylesheet' type='text/css'>
        <!-- Internet Explorer HTML5 enabling code: -->       
        <!--[if IE]>
        <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>       
        <style type="text/css">
        .clear {
          zoom: 1;
          display: block;
        }
        </style>       
        <![endif]-->   
    </head> 
    <body>	
    	<section id="page"> <!-- Defining the #page section with the section tag -->
            <header> <!-- Defining the header section of the page with the appropriate tag -->
                <a href="/Litter/"><img style="padding-top: 15px; padding-right: 15px;" align="left" src="/Litter/img/IMG-small.png" /></a>
                <hgroup>
                    <h1>Litter</h1>
                    <h3>Like Twitter but with liking!</h3>
                </hgroup>            
                <nav class="clear"> <!-- The nav link semantically marks your main site navigation -->
                    <ul>
                    	<li><a href="../Litter">Home</a>
                   			<li><a href="Login/logout">Logout</a></li>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <article id="Register">
                	<h2>Register</h2>
					<%@include file="LogCheck.jsp" %>
					<P>Fill in the form to register. You are registering for this email: <%= User.getEmail()%></P>
					<% 
					String tempName;
					if (form.getName() == null || form.getName() == "") {  tempName = User.getName(); } else { tempName = form.getName(); }%>
					<%-- When submitting the form, resubmit to this page --%>
					<form action='<%= request.getRequestURI() %>' method="POST">
					    <label for="Name">Name: <span style="color: red"><%= form.getErrorMessage("name") %></span></label> 
						<input value = "<%= tempName %>" style="background-color: white;" type="text" name="Name" required placeholder="Name" />
						<label for="Username">Username: <span style="color: red"><%= form.getErrorMessage("username") %></span></label> 
						<input value = "<%= form.getUserName() %>" style="background-color: white;" type="text" name="Username" required placeholder="Username" />
						<label for="Bio">Bio:</label> 
						<textarea style="background-color: white;" name="Bio" placeholder="Write a bit about yourself"></textarea> 
						<label for="Avatar">Avatar (Leave blank to use Gravatar):</label>
						<input style="background-color: white;" type="text" name="Avatar"></input>
						<input style="background-color: white;" type="submit"  value="Add Yourself">
						<input type="HIDDEN" name="process" value="true">
					</form>
				</article>
            </section>
        <%@ include file="footer.jsp" %>  
		</section> <!-- Closing the #page section -->
        <!-- JavaScript Includes -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="script.js"></script>
    </body>
</html>


<%!
    // Define error messages
    java.util.Map<Integer, String> errorMap = new java.util.HashMap<Integer, String>();
    public void jspInit() {
        errorMap.put(ValidationStore.ERR_NAME, "Name field is empty");
        errorMap.put(ValidationStore.ERR_USERNAME, "Username is empty");
        errorMap.put(ValidationStore.ERR_USERNAME_TAKEN, "Username is taken by another user");
    }
%>