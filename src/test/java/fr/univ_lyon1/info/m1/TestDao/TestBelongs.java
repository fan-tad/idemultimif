package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.BelongsDao;
import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestBelongs extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final int BELONGS_RIGHT_1 = 0;
    private static final String BELONGS_PROJECT_NAME_1 = "nom_projet_belongs_test_1";
    private static final String BELONGS_USER_NAME_1 = "nom_utilisateur_belongs_test_1";

    private static final int BELONGS_RIGHT_2 = 1;
    private static final String BELONGS_PROJECT_NAME_2 = "nom_projet_belongs_test_2";
    private static final String BELONGS_USER_NAME_2 = "nom_utilisateur_belongs_test_2";

    private EntityManager entityManager;
    private BelongsDao belongsDao;

    private Belongs belongs_1;
    private Belongs belongs_2;

    private Project project_1;
    private Project project_2;

    private User user_1;
    private User user_2;

    public TestBelongs(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        belongsDao = new BelongsDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName(BELONGS_PROJECT_NAME_1);
        project_2 = new Project();
        project_2.setProjectName(BELONGS_PROJECT_NAME_2);

        user_1 = new User();
        user_1.setUserName(BELONGS_USER_NAME_1);
        user_2 = new User();
        user_2.setUserName(BELONGS_USER_NAME_2);

        belongs_1 = new Belongs(user_1, project_1, BELONGS_RIGHT_1);
        belongs_2 = new Belongs(user_2, project_2, BELONGS_RIGHT_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);

        entityManager.persist(user_1);
        entityManager.persist(user_2);

        entityManager.persist(belongs_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        belongs_2 = null;
        belongs_1 = null;
        project_2 = null;
        project_1 = null;
        user_2 = null;
        user_1 = null;
        belongsDao = null;
    }

    @Test
    public void testGetBelongsByUserAndProject() {
        assertSame(belongs_1, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()));
        assertNotSame(belongs_2, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()));
    }

    @Test
    public void testGetBelongsUsersByProject() {
        List<Belongs> belongsList = entityManager.createNamedQuery("Belongs.findByProject", Belongs.class).setParameter("projectId", project_1.getProjectId()).getResultList();
        assertEquals(belongsList, belongsDao.getBelongsUsersByProject(project_1.getProjectId()));

    }

    @Test
    public void testGetAllBelongs() {
        List<Belongs> belongsList = entityManager.createNamedQuery("Belongs.findByUserAndProject", Belongs.class).setParameter("projectId", project_1.getProjectId()).setParameter("userId", user_1.getUserId()).getResultList();
        assertEquals(belongsList, belongsDao.getBelongsUsersByProject(project_1.getProjectId()));
    }

    @Test
    public void testCreateBelongs() {
        belongsDao.createBelongs(BELONGS_RIGHT_2, project_2, user_2);
        assertEquals(BELONGS_RIGHT_2, belongsDao.getBelongsByUserAndProject(project_2.getProjectId(), user_2.getUserId()).getBelongsRight());
        assertSame(project_2, belongsDao.getBelongsByUserAndProject(project_2.getProjectId(), user_2.getUserId()).getProject());
        assertSame(user_2, belongsDao.getBelongsByUserAndProject(project_2.getProjectId(), user_2.getUserId()).getUser());
    }

    @Test
    public void testRemoveBelongs() {
        Throwable e = null;

        assertNotNull(belongsDao.getBelongsUsersByProject(project_1.getProjectId()).get(0));
        belongsDao.removeBelongs(project_1.getProjectId(), user_1.getUserId());

        try {
            assertNull(belongsDao.getBelongsUsersByProject(project_1.getProjectId()).get(0));
        } catch (Throwable ex) {
            e = ex;
        }

        assertTrue(e instanceof IndexOutOfBoundsException);
    }

    @Test
    public void testUpdateUserRight() {
        belongsDao.updateUserRight(belongs_1, 2);
        assertNotSame(BELONGS_RIGHT_1, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()).getBelongsRight());
        assertSame(BELONGS_RIGHT_1 + 2, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()).getBelongsRight());
        assertSame(project_1, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()).getProject());
        assertSame(user_1, belongsDao.getBelongsByUserAndProject(project_1.getProjectId(), user_1.getUserId()).getUser());
    }
}
