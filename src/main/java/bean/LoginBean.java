package bean;

import entity.User;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

@Named("loginBean")
@ViewScoped
public class LoginBean implements Serializable { // GirisBean -> LoginBean

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private User user;
    private User registerUser;
    private String confirmPassword;
    private boolean registerMode = false;
    private boolean showLoginPassword = false;

    @EJB
    private UserFacadeLocal userFacade;

    @Inject
    private FacesContext facesContext;

    public String login() {
        User foundUser = userFacade.login(getUser().getEmail(), getUser().getPassword());
        if (foundUser != null) {
            facesContext.getExternalContext().getSessionMap().put("user", foundUser);
            return "/panel/index.xhtml?faces-redirect=true";
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Giriş Başarısız: E-posta veya şifre hatalı!", null));
            return null;
        }
    }

    public String register() { // kayitOl -> register
        if (!EMAIL_PATTERN.matcher(getRegisterUser().getEmail()).matches()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Format Hatası: Geçerli bir e-posta adresi giriniz (örnek: ad@mail.com).", null));
            return null;
        }

        if (!getRegisterUser().getPassword().equals(confirmPassword)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Şifre Hatası: Girdiğiniz şifreler uyuşmuyor!", null));
            return null;
        }

        if (userFacade.isEmailExists(getRegisterUser().getEmail(), null)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Kayıt Hatası: Bu e-posta adresi zaten kullanımda.", null));
            return null;
        }

        try {
            userFacade.register(getRegisterUser());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Başarılı: Hesabınız oluşturuldu. Giriş yapabilirsiniz.", null));
            registerMode = false;
            registerUser = new User();
            confirmPassword = null;
            return null;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Sistem Hatası: Kayıt işlemi yapılamadı.", null));
            return null;
        }
    }

    public String logout() {
        facesContext.getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public void toggleLoginPasswordVisibility() {
        this.showLoginPassword = !this.showLoginPassword;
    }


    public boolean isRegisterMode() {
        return registerMode;
    }
    public void setRegisterMode(boolean registerMode) {
        this.registerMode = registerMode;
    }
    public boolean isShowLoginPassword() {
        return showLoginPassword;
    }
    public void setShowLoginPassword(boolean showLoginPassword) {
        this.showLoginPassword = showLoginPassword;
    }

    public User getUser() {
        if (user == null) user = new User();
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public User getRegisterUser() {
        if (registerUser == null) registerUser = new User();
        return registerUser;
    }
    public void setRegisterUser(User registerUser) {
        this.registerUser = registerUser;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}