package filter;

import entity.User;
import enums.RoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "RoleFilter", urlPatterns = {"/panel/users.xhtml", "/panel/system-log.xhtml"})
public class RoleFilter implements Filter { // RolFilter -> RoleFilter

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);

        User currentUser = (session != null)
                ? (User) session.getAttribute("user")
                : null;

        if (currentUser != null && currentUser.getRole() == RoleEnum.ADMIN) {
            chain.doFilter(request, response);
        } else {
            if (isAjaxRequest(request)) {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                String panelURI = request.getContextPath() + "/panel/index.xhtml";
                response.getWriter()
                        .write("<?xml version='1.0' encoding='UTF-8'?>"
                                + "<partial-response><redirect url='"
                                + panelURI + "'/></partial-response>");
            } else {
                response.sendRedirect(request.getContextPath() + "/panel/index.xhtml");
            }
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String facesRequest = request.getHeader("Faces-Request");
        return "partial/ajax".equals(facesRequest);
    }
}