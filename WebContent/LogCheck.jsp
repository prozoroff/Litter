<jsp:useBean id="User"
class="uk.ac.dundee.computing.rosswarren.litter.stores.UserStore"
scope="session"
></jsp:useBean>

<jsp:useBean id="ReturnPoint"
class="uk.ac.dundee.computing.rosswarren.litter.stores.ReturnStore"
scope="session"
></jsp:useBean>
<%
System.out.println("Called From "+request.getRequestURI());
System.out.println("logcheck "+User.isloggedIn());
ReturnPoint.setReturnTo(request.getRequestURI());
%>

<%
if (User == null || User.isloggedIn()==false || User.getAvatarUrl() != ""){
	System.out.println("Log check redirect to home");
	response.sendRedirect("");
}
%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

</body>
</html>