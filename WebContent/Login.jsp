<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <jsp:useBean id="User"
class="uk.co.ross_warren.litter.stores.UserStore"
scope="session"
></jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choose Login Destination</title>
</head>
<body>

<h1>Sign on with OpenID from:</h1>
  <p><a href="Login/Google">Google</a></p>
  <p><a href="Login/Yahoo">Yahoo</a></p>
  <p><a href="http://code.google.com/p/jopenid/" target="_blank">About JOpenID</a></p>

</body>
</html>