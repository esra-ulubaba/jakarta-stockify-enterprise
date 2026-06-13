package bean;

import entity.User;
import enums.RoleEnum;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    private User getActiveUser() {
        return (User) facesContext.getExternalContext()
                .getSessionMap().get("user");
    }

    public boolean isAdmin() {
        User u = getActiveUser();
        return u != null && u.getRole() == RoleEnum.ADMIN;
    }

    public boolean isStaff() {
        User u = getActiveUser();
        return u != null && u.getRole() == RoleEnum.PERSONEL;
    }

    public String getActiveUsername() {
        User u = getActiveUser();
        if (u == null) return "";
        return u.getFirstName() + " " + u.getLastName();
    }

    public String getRoleLabel() {
        User u = getActiveUser();
        if (u == null) return "";
        return u.getRole().getName();
    }
}