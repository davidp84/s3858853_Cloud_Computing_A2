<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Register</title>
</head>
<body>

	<h1 class="display-4">Register</h1>

	<div class="row">
		<div class="col-md-4">
			<form action="register" method="post">
				<div class="form-group">
					<label for="email" class="control-label">Email</label> <input
						name="email" type="email" size="30" class="form-control" />

				</div>

				<div class="form-group">
					<label for="username" class="control-label">Username</label> <input
						name="username" type="text" class="form-control" size="30" />
				</div>

				<div class="form-group">
					<label for="password" class="control-label">Password</label> <input
						id="password" name="password" type="password" size="30"
						class="form-control" data-val="true" />
				</div>

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
					<input type="submit" value="Register" class="btn btn-primary" />
				</div>

			</form>
		</div>
	</div>

</body>
</html>