package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//bu filtrenin hangi url adreslerinde calisacagini belirtir.
@WebFilter(filterName = "SessionFilter", urlPatterns = {"/panel/*", "/app/*"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String loginURI = request.getContextPath() + "/login";
        String panelURI = request.getContextPath() + "/panel/index";

        boolean isLoggedIn = request.getSession().getAttribute("user") != null;
        boolean isLoginRequest = request.getRequestURI().equals(loginURI);

        if (isLoggedIn || isLoginRequest) {
            if (isLoginRequest && isLoggedIn) {
                response.sendRedirect(panelURI);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if (isAjaxRequest(request)) {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .write("<?xml version='1.0' encoding='UTF-8'?>"
                                + "<partial-response><redirect url='"
                                + loginURI + "'/></partial-response>");
            } else {
                response.sendRedirect(loginURI);
            }
        }
    }
    private boolean isAjaxRequest(HttpServletRequest request) {
        String facesRequest = request.getHeader("Faces-Request");
        return "partial/ajax".equals(facesRequest);
    }
}