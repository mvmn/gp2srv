package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class BasicAuthFilter implements Filter {
	private final String username;
	private final String password;

	public BasicAuthFilter(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
			final HttpServletResponse httpReponse = (HttpServletResponse) response;

			String authHeader = httpRequest.getHeader("Authorization");
			if (authHeader != null) {
				String[] splits = authHeader.split(" ");
				if (splits.length > 1) {
					String basic = splits[0];

					if (basic.equalsIgnoreCase("Basic")) {
						String credentials = new String(Base64.getDecoder().decode(splits[1]), "UTF-8");
						int separatorIndex = credentials.indexOf(":");
						if (separatorIndex > 0) {
							final String username = credentials.substring(0, separatorIndex);
							final String password = credentials.substring(separatorIndex + 1);
							if (this.username.equals(username) && this.password.equals(password)) {
								httpRequest.getSession().setAttribute("loggedin", Boolean.TRUE);
							}
						}
					}
				}
			}

			final Object loggedIn = httpRequest.getSession().getAttribute("loggedin");
			if (loggedIn != null && loggedIn.equals(Boolean.TRUE)) {
				chain.doFilter(request, response);
			} else {
				httpReponse.setStatus(401);
				httpReponse.setHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"");
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}
}