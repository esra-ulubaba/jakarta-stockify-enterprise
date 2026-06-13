package facadeLocal;

import entity.User;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface UserFacadeLocal {
    void createUser(User user, User modifierUser);
    User updateUser(User user, User modifiedUser);
    void deleteUser(User user, User modifiedUser);
    List<User> userList();
    User login(String email, String password);
    User findById(Long id);
    boolean isEmailExists(String email, Long currentId);
    void register(User user);
}