package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.BelongsDao;
import fr.univ_lyon1.info.m1.DAO.UserDao;
import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Transactional
public class UserManager {

    private ManagerUtility managerUtility;
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;

    private UserDao userDao;

    private BelongsDao belongsDao;

    public UserManager(EntityManager em) {
        //this.em = em;
        managerUtility = new ManagerUtility();
        this.userDao = new UserDao(em);
        this.belongsDao = new BelongsDao(em);
    }

    public void deleteUser(String userName) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        userDao.removeUser(userName);

        transaction.commit();
    }

    public User newUser(String userName, String userPassword) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        User user = userDao.createUser(userName, userPassword);

        transaction.commit();
        return user;
    }

    public User checkUser(String userName, String userPassword) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        User user = userDao.getUserByNamePassword(userName, userPassword);

        transaction.commit();
        return user;
    }

    public User getUser(String userName) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        User user = userDao.getUserByName(userName);

        transaction.commit();
        return user;
    }

    public User getUser(int userId) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        User user = userDao.getUserById(userId);

        transaction.commit();
        return user;
    }

    public List<User> getUsers() {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        List<User> users = userDao.getAllUsers();

        transaction.commit();
        return users;
    }
}
