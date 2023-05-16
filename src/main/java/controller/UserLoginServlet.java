/*N. H. Minh, “How to code login and logout with Java Servlet, JSP and MySQL,” 
 * CodeJava, 04-Jul-2019. [Online]. Available: 
 * https://www.codejava.net/coding/how-to-code-login-and-logout-with-java-servlet-jsp-and-mysql.
 *  [Accessed: 24-Apr-2022]. 
 */

package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.LoggedInUser;
import model.User;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserLoginServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		LoginController login = new LoginController();

		try {
			User user = login.checkLogin(email, password);
			String destPage = "login.jsp";
			
			if (user != null) {
				String username = user.getUser_name();
				// Set user_name
				LoggedInUser temp = new LoggedInUser();
				temp.setUser_name(username);
//              LoggedInUser.user_name = username; // Make variable in LoggedInUser public for this method
				destPage = "main.jsp";
			} else {
				String message = "email or password is invalid";
				request.setAttribute("message", message);
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher(destPage);
			dispatcher.forward(request, response);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
}
