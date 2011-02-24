<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html> <!-- The new doctype -->
<html>
    <head>
    
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Litter</title>
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link href='http://fonts.googleapis.com/css?family=Chewy' rel='stylesheet' type='text/css'>
		<link rel="stylesheet" type="text/css" href="styles.css" />
		<script src="js/jquery-1.4.4.min.js"></script>
		<link type="text/css" href="css/vader/jquery-ui-1.8.9.custom.css" rel="Stylesheet" />
		<script src="js/jquery-ui-1.8.9.custom.min.js"></script>
		<title>Admin Page</title>
	</head>
	<body>	
    	<section id="page"> <!-- Defining the #page section with the section tag -->
            <header> <!-- Defining the header section of the page with the appropriate tag -->
                <a href="/Litter/"><img style="padding-top: 15px; padding-right: 15px;" align="left" src="/Litter/img/IMG-small.png" /></a>
                <hgroup>
                    <h1><a href="/Litter/" style="color: white">Litter</a></h1>
                    <h3>Like Twitter but with liking!</h3>
                </hgroup>            
                <nav class="clear"> <!-- The nav link semantically marks your main site navigation -->
                    <ul>
                   			<li><a href="Logout">Logout</a></li>
                    </ul>
                </nav>
            </header>
            <section id="articles"> <!-- A new section with the articles -->
                <div class="line"></div>  <!-- Dividing line -->   
                <article id="Welcome">
                    <h2>Administration</h2>
					<div class="line"></div>  <!-- Dividing line -->
					<div class="settings">
						<table style="width: 100%">
						<tr>
						<td>
						<label>Cassandra IP</label>
						<input id="Host" style="background: none; background-color: white; width: 200px;"/>
						<button id ="BHost">Change</button>
						</td>
						<td>
						<label>Cassandra Port</label>
						<input id="Port" style="background: none; background-color: white; width: 200px;"/>
						<button id ="BPort">Change</button>
						</td>
						<td>
						<label>Cassandra Cluster Name</label>
						<input id="Cluster" style="background: none; background-color: white; width: 200px;"/>
	               		<button id ="BCluster">Change</button>
	               		</td>
	               		</tr>
	               		</table>
	               		<button style="margin-top: 20px" id="test">Test Connection</button>
	               		<p id="testtext">Click to test Cassandra connection</p>
	               		<p>Note: Changing the IP and Port will not cause the connection to fail, as the old ones will still be in the known pool.</p>
               		</div>
                </article>
            </section>
            
            
        <%@ include file="footer.jsp" %>                 
		</section> <!-- Closing the #page section -->
        <!-- JavaScript Includes -->
        <script src="jquery.scrollTo-1.4.2/jquery.scrollTo-min.js"></script>
        <script src="script.js"></script>
        <script>
        
        $("#BHost").click( function() {
        	$("#BHost").text("Changing..");
        	$.ajax({
    			aysnc: true,
				type: "POST",
				url: '/Litter/Admin/Host/' + $("#Host").val(),
				dataType: "text",
				success: function(msg){
					loadHost();
				}
	     	});
        });
        
        $("#BPort").click( function() {
        	$("#BPort").text("Changing..");
        	$.ajax({
    			aysnc: true,
				type: "POST",
				url: '/Litter/Admin/Port/' + $("#Port").val(),
				dataType: "text",
				success: function(msg){
					loadPort();
				}
	     	});
        });
        
        $("#BCluster").click( function() {
        	$("#BCluster").text("Changing..");
        	$.ajax({
    			aysnc: true,
				type: "POST",
				url: '/Litter/Admin/Cluster/' + $("#Cluster").val(),
				dataType: "text",
				success: function(msg){
					loadCluster();
					
				}
	     	});
        });
        
        
        
        $("#test").click( function() {
        	$.ajax({
    			aysnc: true,
				type: "GET",
				url: '/Litter/Admin/Test/',
				cache: false,
				dataType: "text",
				success: function(msg){
					loadHost();
		        	loadPort();
		        	loadCluster();
					$("#testtext").text(msg);
				}
	     	});
        });
        $(function () {
        	$(".settings").hide();
        	loadHost();
        	loadPort();
        	loadCluster();
        	$(".settings").slideDown('slow');
        });
        
        $(function() {
			$( "button", ".settings" ).button();
			$( "a", ".settings" ).click(function() { return false; });
		});
        </script>
    </body>