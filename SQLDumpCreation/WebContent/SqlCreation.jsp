<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<form action="${pageContext.request.contextPath}/SQLDumpCreation" method="post" enctype="multipart/form-data">
	<input type="submit" name="button1" value="SQL Dump Creation" />
</form>


<form action="${pageContext.request.contextPath}/Sql_Insert_Statement_One_Day" method="post" enctype="multipart/form-data">
	<input type="submit" name="Sql_Insert_Statement_One_Day" value="Sql_Insert_Statement_One_Day" />
</form>


<form action="${pageContext.request.contextPath}/Sql_Insert_Statement_One_Hour" method="post" enctype="multipart/form-data">
	<input type="submit" name="Sql_Insert_Statement_One_Hour" value="Sql_Insert_Statement_One_Hour" />
</form>

<form action="${pageContext.request.contextPath}/Sql_Insert_Statement_Five_minute" method="post" enctype="multipart/form-data">
	<input type="submit" name="Sql_Insert_Statement_Five_minute" value="Sql_Insert_Statement_Five_minute" />
</form>


<form action="${pageContext.request.contextPath}/Sql_Insert_Statement_One_minute" method="post" enctype="multipart/form-data">
	<input type="submit" name="Sql_Insert_Statement_One_minute" value="Sql_Insert_Statement_One_minute" />
</form>
