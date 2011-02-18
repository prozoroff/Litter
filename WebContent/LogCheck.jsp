    <jsp:useBean id="User"
class="uk.co.ross_warren.litter.stores.UserStore"
scope="session"
></jsp:useBean>

<%
System.out.println("Called From "+request.getRequestURI());
System.out.println("logcheck "+User.isloggedIn());
//ReturnPoint.setReturnTo(request.getRequestURI());
%>

<%
if (User == null || User.isloggedIn()==false){
	System.out.println("Log check redirect to home");
	response.sendRedirect("");
}
%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>