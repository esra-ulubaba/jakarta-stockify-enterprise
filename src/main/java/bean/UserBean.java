package bean;

import entity.User;
import enums.RoleEnum;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private User user;
    private List<User> users;

    @EJB
    private UserFacadeLocal userFacade;

    @Inject
    private FacesContext facesContext;

    public void clearForm() {
        user = new User();
        users = null;
    }

    public void saveUser() {
        String email = user.getEmail() != null ? user.getEmail().trim() : null;

        if (!isEmailValid(email)) return;

        if (userFacade.isEmailExists(email, null)) {
            addError("Bu e-posta adresi zaten kullanılıyor.");
            return;
        }
        try {
            user.setEmail(email);
            User activeUser = (User) facesContext.getExternalContext().getSessionMap().get("user");

            userFacade.createUser(user,  activeUser);

            users = null;
            user = new User();
            addSuccessMessage("Kullanıcı başarıyla oluşturuldu.");
        } catch (Exception e) {
            addError("Kullanıcı kaydedilemedi: " + e.getMessage());
        }
    }

    public void updateUser() {
        User activeUser = (User) facesContext.getExternalContext()
                .getSessionMap().get("user");

        if (activeUser != null && activeUser.getId().equals(user.getId())
                && !activeUser.getRole().equals(user.getRole())) {
            addError("Kendi rolünüzü değiştiremezsiniz.");
            return;
        }

        String email = user.getEmail() != null ? user.getEmail().trim() : null;

        if (!isEmailValid(email)) return;

        if (userFacade.isEmailExists(email, user.getId())) {
            addError("Bu e-posta adresi başka bir kullanıcı tarafından kullanılıyor.");
            return;
        }

        try {
            user.setEmail(email);
            userFacade.updateUser(this.user, activeUser);

            users = null;
            user = new User();
            addSuccessMessage("Kullanıcı güncellendi.");
        } catch (Exception e) {
            addError("Güncelleme başarısız: " + e.getMessage());
        }
    }

    public void prepareForEdit(User u) {
        this.user = u;
    }

    public void deleteUser(User u) {
        User activeUser = (User) facesContext.getExternalContext()
                .getSessionMap().get("user");
        if (activeUser != null && activeUser.getId().equals(u.getId())) {
            addError("Kendinizi silemezsiniz.");
            return;
        }
        try {
            userFacade.deleteUser(u, activeUser);
            users = null;
            addSuccessMessage("Kullanıcı silindi.");
        } catch (Exception e) {
            addError("Silme işlemi başarısız.");
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null || email.isBlank()) {
            addError("E-posta adresi boş bırakılamaz.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            addError("Geçersiz e-posta formatı. Lütfen geçerli bir adres girin. Örnek: kullanici@ornek.com");
            return false;
        }
        return true;
    }

    public RoleEnum[] getRoles() {
        return RoleEnum.values();
    }

    public User getUser() {
        if (user == null) user = new User();
        return user;
    }

    public void setUser(User user) { this.user = user; }

    public List<User> getUsers() {
        if (users == null) users = userFacade.userList();
        return users;
    }

    public void setUsers(List<User> users) { this.users = users; }

    private void addSuccessMessage(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addError(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}