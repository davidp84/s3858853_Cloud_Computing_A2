package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Song;

@WebServlet("/query")
public class UserQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserQueryServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String artist = request.getParameter("artist");
		String title = request.getParameter("title");
		int year = 0;

		boolean checkYear = false;

		HashMap<String, String> query = new HashMap<String, String>();
		List<Song> songs = new ArrayList<Song>();
		MainController main = new MainController();

		// Only sets the int year if a number has been received from the form
		if (request.getParameter("year").length() > 0) {
			year = Integer.parseInt(request.getParameter("year"));
			checkYear = true;
		}

		// Only adds the artist and/or the title to the HashMap
		// if data has been received
		if (artist != null && artist.length() > 0) {
			query.put("artist", artist);
		}
		if (title != null && title.length() > 0) {
			query.put("title", title);
		}

		try {
			// Calls the relevant method based on what information has been received
			// from the form
			if (query.size() == 0) {
				songs = main.querySongs(year);
			} else if (query.size() >= 1 && checkYear == true) {
				songs = main.querySongs(query, year);
			} else {
				songs = main.querySongs(query);
			}

			String destPage = "main.jsp";

			if (songs == null || songs.size() == 0) {
				String message = "No result is retrieved. Please query again";
				request.setAttribute("message", message);
			} else {				
				request.setAttribute("queryResponse", songs);
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher(destPage);
			dispatcher.forward(request, response);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

}
