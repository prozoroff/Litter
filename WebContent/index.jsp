<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="uk.co.ross_warren.litter.stores.*" %>
<%@ page import="java.util.*" %>
    <jsp:useBean id="User"
class="uk.co.ross_warren.litter.stores.UserStore"
scope="session"
></jsp:useBean>
<%
	Boolean loggedin = false;
	if (User != null)
	{
		loggedin = User.isloggedIn();
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
                    <h1><a href="/Litter/" style="color: white;">Litter</a></h1>
                    <h3>Like Twitter but with liking!</h3>
                </hgroup>            
                <nav class="clear"> <!-- The nav link semantically marks your main site navigation -->
                    <ul>
                    	<%
                    		if (!loggedin)
                    		{
                    			%>
                    			<li><a href="Login/Google">Login With Google</a></li>
                    			<li><a href="Login/Yahoo">Login With Yahoo</a></li>
                    		<%
                    		} else {
                    			%>
                    			<li><a href="User/<%=User.getUserName() %>">Your Profile</a></li>
                    			<li><a href="Login/logout">Logout</a></li>
                    			<%
                    		}
                    	%>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <% if (!loggedin) { %>
                <article id="Welcome">
                    <h2>Welcome!</h2>
					<div class="line"></div>  <!-- Dividing line -->
					<img align="right" src="/Litter/img/IMG.png" />
					<p>Welcome to Litter. To begin, click Login with Google or Yahoo at the top left of the page. </p>
					<p style="margin-bottom: 200px;">Currently in Alpha development. Please be patient!</p>
                </article>
                <% } else { %>
                <article id="Welcome">
                    <h2>Welcome back <%=User.getUserName() %></h2>
					<div class="line"></div>  <!-- Dividing line -->
					<form style="width: 80%" action='/Litter/Tweet' method="POST">
						<!-- <label for="ReplyToUser">At Username:</label> 
						<input style="background-color: white;" type="text" name="ReplyToUser" placeholder="Username" />
						-->
						<table style="width: 100%">
						<tr>
						<td>
						<textarea style="background: none; width: 90%; min-height: 42px; background-color: white;" name="Content" required placeholder="Write your post here"></textarea> 
						</td>
						<td>
						<input style="width: 90%; background-color: white;" type="submit"  value="Tweet">
						</td>
						</tr>
						</table>
					</form>
					
                </article>
                <% } %>
            </section>
        <%@ include file="footer.jsp" %>                 
		</section> <!-- Closing the #page section -->
        
        <!-- JavaScript Includes -->
        
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="script.js"></script>
    </body>
</html>
