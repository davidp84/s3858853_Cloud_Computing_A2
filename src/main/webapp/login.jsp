<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<script src="/js/script.js" type="text/javascript"></script>
<title>Login</title>
</head>
<body>

	<h1 class="display-4">Login</h1>

	<div class="row">
		<div class="col-md-4">
			<form action="login" method="post">
				<div class="form-group">
					<label for="email" class="control-label">Email</label> <input
						name="email" type="email" size="30" class="form-control" />
				</div>
				<div class="form-group">
					<label for="password" class="control-label">Password</label> <input
						id="password" name="password" type="password" size="30"
						class="form-control" data-val="true" />
				</div>
				<br>
				<%
				if (request.getAttribute("message") == null) {
				%>

				<%
				} else {
				%>
				<h3>
					<%=request.getAttribute("message")%>
				</h3>
				<%
				}
				%>

				<br>
				<div class="form-group">
					<input type="submit" value="Login" class="btn btn-primary" />
				</div>

			</form>
		</div>
	</div>
	<br>
	<div class="row">
		<div class="col-md-4">
			<form action="register.jsp">
				<div class="form-group">
					<input type="submit" value="Register" name="Register"
						class="btn btn-primary" />
				</div>
			</form>
		</div>
	</div>

</body>
</html>