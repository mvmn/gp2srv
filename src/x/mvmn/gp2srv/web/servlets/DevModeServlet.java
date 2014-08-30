package x.mvmn.gp2srv.web.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import x.mvmn.gp2srv.GPhoto2Server;

public class DevModeServlet extends AbstractErrorHandlingServlet {

	private static final long serialVersionUID = -810341607948659887L;

	private final GPhoto2Server gPhoto2Server;

	public DevModeServlet(final GPhoto2Server gPhoto2Server) {
		super(gPhoto2Server, gPhoto2Server.getLogger());
		this.gPhoto2Server = gPhoto2Server;
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		final Context context = createContext(request, response);
		if ("/rst".equals(request.getPathInfo())) {
			gPhoto2Server.reReadTemplates();
			context.put("commandSuccess", true);
			context.put("message", "Templates reloaded");
		}
		serveTempalteUTF8Safely("devmode.vm", context, response, logger);
	}
}
