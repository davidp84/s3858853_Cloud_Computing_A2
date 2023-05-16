<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ page import="model.LoggedInUser"%>
<%@ page import="controller.MainController"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="model.Song"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Main Page</title>
</head>
<body>

	<h1 class="display-4">Main Page</h1>
	<div>
		<h3><%=LoggedInUser.USER_NAME%></h3>
		<%
		MainController main = new MainController();
		%>
	</div>
	<div>
		<h2>Subscribed Songs</h2>
	</div>
	<div class="row">
		<div class="col-md-4">
			<table class="table table-striped">
				<tr>
					<th>Title</th>
					<th>Year</th>
					<th>Artist</th>
					<th>Image</th>
					<th>Action</th>
				</tr>

				<%
				List<Song> subscribedSongs = main.getSubscribedSongs(LoggedInUser.USER_NAME);

				if (subscribedSongs == null) {
				%>
				<h3>No Subscribed Songs</h3>
				<%
				} else {
				%>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td><img src=@postImage width="80px" height="80px" /></td>
					<td><button>Remove</button></td>
				</tr>
				<%
				for (Song song : subscribedSongs) {
				%>

				<tr class="table-row">
					<td><%=song.getTitle()%></td>
					<td><%=song.getArtist()%></td>
					<td><%=song.getYear()%></td>
					<td><img src=<%=main.getImage()%> width="60px" height="60px" /></td>
					<td><form action="remove" method="post">
							<input name="removeTitle" value="<%=song.getTitle()%>"
								type="hidden"> <input name="removeArtist"
								value="<%=song.getArtist()%>" type="hidden"> <input
								name="removeYear" value="<%=song.getYear()%>" type="hidden">
								<input name="removeUser" value="<%=LoggedInUser.USER_NAME%>"
							type="hidden">
							<input value="Remove" type="submit">
						</form></td>
				</tr>
				<%
				}
				%>
			</table>
		</div>
		<%
		}
		%>



	</div>
	</div>
	<div>
		<h2>Query Songs</h2>
	</div>
	<div class="row">
		<div class="col-md-4">
			<form action="query" method="post">
				<div class="form-group">
					<label for="title" class="control-label">Title</label> <input
						id="title" name="title" type="text" size="30" class="form-control" />
				</div>
				<div class="form-group">
					<label for="year" class="control-label">Year</label> <input
						id="year" name="year" type="text" size="30" class="form-control" />
				</div>
				<div class="form-group">
					<label for="artist" class="control-label">Artist</label> <input
						name="artist" type="text" size="30" class="form-control" />
				</div>

				<br>
				<%
				if (request.getAttribute("message") == null) {

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
					<input type="submit" value="Query" class="btn btn-primary" />
				</div>

			</form>
		</div>
	</div>

	<%
	if (request.getAttribute("message") == null) {
	%>

	<%
	List<Song> songs = (ArrayList<Song>) request.getAttribute("queryResponse");

	if (songs == null) {

	} else {
	%>
	<div>
		<table class="main-table">
			<tr class="table-header">
				<th>Title</th>
				<th>Artist</th>
				<th>Year</th>
				<th>Image</th>
				<th>Action</th>
			</tr>
			<%
			for (Song song : songs) {
			%>

			<tr class="table-row">
				<td><%=song.getTitle()%></td>
				<td><%=song.getArtist()%></td>
				<td><%=song.getYear()%></td>
				<td><img src=<%=main.getImage()%> width="60px" height="60px" /></td>
				<td><form action="subscribe" method="post">
						<input name="subscribeTitle" value="<%=song.getTitle()%>"
							type="hidden"> <input name="subscribeArtist"
							value="<%=song.getArtist()%>" type="hidden"> <input
							name="subscribeYear" value="<%=song.getYear()%>" type="hidden">
							<input name="subscribeUser" value="<%=LoggedInUser.USER_NAME%>"
							type="hidden">
						<input value="Subscribe" type="submit">
					</form></td>
			</tr>
			<%
			}
			%>
		</table>
	</div>
	<%
	}
	}
	%>


	<br>
	<div class="row">
		<div class="col-md-4">
			<form action="login.jsp">
				<div class="form-group">
					<input type="submit" value="Log Out" name="Log Out"
						class="btn btn-primary" />
				</div>
			</form>
		</div>
	</div>
</body>
</html>