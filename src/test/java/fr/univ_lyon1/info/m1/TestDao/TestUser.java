package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.UserDao;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestUser extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String USER_NAME_1 = "nom_utilisateur_test_1";
    private static final String USER_PASSWORD_1 = "mot_de_passe_utilisateur_test_1";

    private static final String USER_NAME_2 = "nom_utilisateur_test_2";
    private static final String USER_PASSWORD_2 = "mot_de_passe_utilisateur_test_2";

    private EntityManager entityManager;
    private UserDao userDao;

    private User user_1;
    private User user_2;

    public TestUser(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        userDao = new UserDao(entityManager);

        user_1 = new User(USER_NAME_1, USER_PASSWORD_1);
        user_2 = new User(USER_NAME_2, USER_PASSWORD_2);

        entityManager.getTransaction().begin();

        entityManager.persist(user_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        user_2 = null;
        user_1 = null;
        userDao = null;
    }

    @Test
    public void testGetUserByName() {
        assertSame(user_1, userDao.getUserByName(USER_NAME_1));
        assertNotSame(user_2, userDao.getUserByName(USER_NAME_1));
    }

    @Test
    public void testGetUserById() {
        assertSame(user_1, userDao.getUserById(userDao.getUserByName(USER_NAME_1).getUserId()));
        assertNotSame(user_2, userDao.getUserById(userDao.getUserByName(USER_NAME_1).getUserId()));
    }

    @Test
    public void testGetUserByNamePassword() {
        assertSame(user_1, userDao.getUserByNamePassword(USER_NAME_1, USER_PASSWORD_1));
        assertNotSame(user_2, userDao.getUserByNamePassword(USER_NAME_1, USER_PASSWORD_1));
    }

    @Test
    public void testGetAllUsers() {
        List<User> usersList = entityManager.createNamedQuery("User.findAll", User.class).getResultList();
        assertEquals(usersList, userDao.getAllUsers());
    }

    @Test
    public void testCreateUser() {
        userDao.createUser(USER_NAME_2, USER_PASSWORD_2);
        assertEquals(USER_NAME_2, userDao.getUserByName(USER_NAME_2).getUserName());
        assertEquals(USER_PASSWORD_2, userDao.getUserByName(USER_NAME_2).getUserPassword());
    }

    @Test
    public void testRemoveUser() {
        assertNotNull(userDao.getUserByName(USER_NAME_1));
        userDao.removeUser(USER_NAME_1);
        assertNull(userDao.getUserByName(USER_NAME_1));
    }
}
