package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.lang.util.DateHelper;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = -810341607948659887L;

	private final GPhoto2Server gPhoto2Server;

	public AdminServlet(GPhoto2Server gPhoto2Server) {
		this.gPhoto2Server = gPhoto2Server;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		if ("/rst".equals(request.getPathInfo())) {
			gPhoto2Server.reReadTemplates();
			try {
				response.getWriter().write(DateHelper.getDateSortFriendlyStr() + " - Templates reloaded.");
			} catch (IOException e) {
				gPhoto2Server.getLogger().error(e);
			}
		}
	}
}
