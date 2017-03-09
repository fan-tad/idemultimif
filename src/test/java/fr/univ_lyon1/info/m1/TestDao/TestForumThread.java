package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.ForumThreadDao;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestForumThread extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String FORUM_THREAD_NAME_1 = "nom_sujet_forum_test_1";
    private static final String FORUM_THREAD_PROJECT_NAME_1 = "nom_projet_sujet_forum_test_1";
    private static final String FORUM_THREAD_USER_NAME_1 = "nom_utilisateur_sujet_forum_test_1";

    private static final String FORUM_THREAD_NAME_2 = "nom_sujet_forum_test_2";
    private static final String FORUM_THREAD_PROJECT_NAME_2 = "nom_projet_sujet_forum_test_2";
    private static final String FORUM_THREAD_USER_NAME_2 = "nom_utilisateur_sujet_forum_test_2";

    private EntityManager entityManager;
    private ForumThreadDao forumThreadDao;

    private Project project_1;
    private Project project_2;

    private User user_1;
    private User user_2;

    private ForumThread forumThread_1;
    private ForumThread forumThread_2;

    public TestForumThread(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        forumThreadDao = new ForumThreadDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName(FORUM_THREAD_PROJECT_NAME_1);
        project_2 = new Project();
        project_2.setProjectName(FORUM_THREAD_PROJECT_NAME_2);

        user_1 = new User();
        user_1.setUserName(FORUM_THREAD_USER_NAME_1);
        user_2 = new User();
        user_2.setUserName(FORUM_THREAD_USER_NAME_2);

        forumThread_1 = new ForumThread(FORUM_THREAD_NAME_1, project_1, user_1);
        forumThread_2 = new ForumThread(FORUM_THREAD_NAME_2, project_2, user_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);

        entityManager.persist(user_1);
        entityManager.persist(user_2);

        entityManager.persist(forumThread_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        forumThread_2 = null;
        forumThread_1 = null;
        user_2 = null;
        user_1 = null;
        project_2 = null;
        project_1 = null;
        forumThreadDao = null;
    }

    @Test
    public void testGetForumThreadById() {
        assertSame(forumThread_1, forumThreadDao.getForumThreadById(forumThreadDao.getForumThreadsByProject(project_1).get(0).getForumThreadId()));
        assertNotSame(forumThread_2, forumThreadDao.getForumThreadById(forumThreadDao.getForumThreadsByProject(project_1).get(0).getForumThreadId()));
    }

    @Test
    public void testGetForumThreadsByProject() {
        List<ForumThread> forumThreadsList = entityManager.createNamedQuery("ForumThread.findAllByProject", ForumThread.class).setParameter("project", project_1).getResultList();
        assertEquals(forumThreadsList, forumThreadDao.getForumThreadsByProject(project_1));
    }

    @Test
    public void testCreateForumThread() {
        forumThreadDao.createForumThread(FORUM_THREAD_NAME_2, project_2, user_2);
        assertEquals(FORUM_THREAD_NAME_2, forumThreadDao.getForumThreadsByProject(project_2).get(0).getForumThreadName());
        assertSame(project_2, forumThreadDao.getForumThreadsByProject(project_2).get(0).getProject());
        assertSame(user_2, forumThreadDao.getForumThreadsByProject(project_2).get(0).getUser());
    }

    @Test
    public void testRemoveForumThread() {
        Throwable e = null;

        assertNotNull(forumThreadDao.getForumThreadsByProject(project_1).get(0));
        forumThreadDao.removeForumThread(forumThreadDao.getForumThreadsByProject(project_1).get(0).getForumThreadId());

        try {
            assertNull(forumThreadDao.getForumThreadsByProject(project_1).get(0));
        } catch (Throwable ex) {
            e = ex;
        }

        assertTrue(e instanceof IndexOutOfBoundsException);
    }
}
