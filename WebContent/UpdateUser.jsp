<%-- Instantiate the form validation bean and supply the error message map --%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html> <!-- The new doctype -->
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter</title>
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link href='http://fonts.googleapis.com/css?family=Chewy' rel='stylesheet' type='text/css'>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
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
                					<%@include file="LogCheck.jsp" %>     
                <nav class="clear"> <!-- The nav link semantically marks your main site navigation -->
                    <ul>
                    	<li><a href="../Litter">Home</a>
                    	<li><a href="../Litter/User/<%= User.getUserName() %>">Your Profile</a>
                  		<li><a href="Logout">Logout</a></li>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <article id="Update">
                	<h2>Update</h2>
					<P>Fill in the form to update. You are updating for this email: <%= User.getEmail()%></P>
					<span id="forminfo" style="display: none; text-align: center">
						<br />
						<br />
						<h2>Details Updated</h2>
						<br />
					</span>
					<form id="toput">
					    <label for="Name">Name:</label> 
						<input value = "<%= User.getName() %>" style="background-color: white;" type="text" name="Name" required placeholder="Name" />
						<label for="Bio">Bio:</label> 
						<textarea style="background-color: white;" name="Bio" placeholder="Write a bit about yourself"><%= User.getBio() %></textarea> 
						<label for="Avatar">Avatar (Leave blank to use Gravatar):</label>
						<input value = "<%= User.getAvatarUrl() %>" style="background-color: white;" type="text" name="Avatar"></input>
						<input style="background-color: white;" type="submit"  value="Add Yourself">
					</form>
				</article>
            </section>
        <%@ include file="footer.jsp" %>  
		</section> <!-- Closing the #page section -->
        <!-- JavaScript Includes -->
        <script src="script.js"></script>
    </body>
<script>
$("#toput").submit(function() {
	var form = $(this).serialize();
	$.ajax({
			url: "/Litter/User?" + form,
		  	type: "PUT",
		  	complete: function() {
		  		$("#toput").fadeOut('slow');
		  		$('#forminfo').show();
		  	}		
			
	});		

	return false;
});
</script>
</html>