
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>upload the text file</title>
</head>
<body>
<h2></h2>
<br>
<h3>Date=<%= new Date() %>
</h3>

<form action="${pageContext.request.contextPath}/Controller" method="post" enctype="multipart/form-data">
	<input type="submit" name="Controller" value="Controller" />
</form>
			
</body>
</html>