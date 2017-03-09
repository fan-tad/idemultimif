package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.ForumPostDao;
import fr.univ_lyon1.info.m1.Modele.ForumPost;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

public class TestForumPost extends TestCase {
    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final int FORUM_POST_ID_1 = 0;
    private static final Date FORUM_POST_CREATE_DATE_1 = new Date(0 - 2000);
    private static final Date FORUM_POST_MODIFY_DATE_1 = new Date(0 - 2001);
    private static final String FORUM_POST_CONTENT_1 = "contenu_message_forum_test_1";


    private static final int FORUM_POST_ID_2 = 0;
    private static final Date FORUM_POST_CREATE_DATE_2 = new Date(0 - 2000);
    private static final Date FORUM_POST_MODIFY_DATE_2 = new Date(0 - 2001);
    private static final String FORUM_POST_CONTENT_2 = "contenu_message_forum_test_1";


    private EntityManager entityManager;
    private ForumPostDao forumPostDao;

    private Project project_1;
    private Project project_2;

    private ForumThread forumThread_1;
    private ForumThread forumThread_2;

    private User user_1;
    private User user_2;

    private ForumPost forumPost_1;
    private ForumPost forumPost_2;

    public TestForumPost(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        forumPostDao = new ForumPostDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName("a");
        project_2 = new Project();
        project_2.setProjectName("b");

        user_1 = new User();
        user_1.setUserName("a");
        //user_1.setUserName(FORUM_THREAD_USER_NAME_1);
        user_2 = new User();
        user_2.setUserName("b");
        //user_2.setUserName(FORUM_THREAD_USER_NAME_2);

        forumThread_1 = new ForumThread();
        forumThread_1.setProject(project_1);
        forumThread_1.setUser(user_1);
        //forumThread_1.setForumThreadName(FORUM_THREAD_PROJECT_NAME_1);
        forumThread_2 = new ForumThread();
        forumThread_2.setProject(project_2);
        forumThread_2.setUser(user_2);

        //forumThread_2.setForumThreadName(FORUM_THREAD_PROJECT_NAME_2);



        forumPost_1 = new ForumPost(FORUM_POST_CREATE_DATE_1, FORUM_POST_MODIFY_DATE_1, FORUM_POST_CONTENT_1, forumThread_1, user_1);
        forumPost_2 = new ForumPost(FORUM_POST_CREATE_DATE_2, FORUM_POST_MODIFY_DATE_2, FORUM_POST_CONTENT_2, forumThread_2, user_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);

        entityManager.persist(user_1);
        entityManager.persist(user_2);

        entityManager.persist(forumThread_1);
        entityManager.persist(forumThread_2);

        entityManager.persist(forumPost_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        forumPost_2 = null;
        forumPost_1 = null;
        user_2 = null;
        user_1 = null;
        forumThread_2 = null;
        forumThread_1 = null;
        forumPostDao = null;
    }

    @Test
    public void testGetForumPostByForumThread() {
        List<ForumPost> forumPostsList = entityManager.createNamedQuery("ForumPost.findAllByForumThread", ForumPost.class).setParameter("forumThread", forumThread_1).getResultList();
        assertEquals(forumPostsList, forumPostDao.getForumPostsByForumThread(forumThread_1));
    }

    @Test
    public void testGetForumPostById() {
        assertSame(forumPost_1, forumPostDao.getForumPostById(forumPostDao.getForumPostsByForumThread(forumThread_1).get(0).getForumPostId()));
        assertNotSame(forumPost_2, forumPostDao.getForumPostById(forumPostDao.getForumPostsByForumThread(forumThread_1).get(0).getForumPostId()));
    }

    @Test
    public void testCreateForumPost() {
        forumPostDao.createForumPost(FORUM_POST_CREATE_DATE_2, FORUM_POST_MODIFY_DATE_2, FORUM_POST_CONTENT_2, forumThread_2, user_2);
        assertEquals(FORUM_POST_CREATE_DATE_2, forumPostDao.getForumPostsByForumThread(forumThread_2).get(0).getForumPostCreateDate());
        assertEquals(FORUM_POST_MODIFY_DATE_2, forumPostDao.getForumPostsByForumThread(forumThread_2).get(0).getForumPostModifyDate());
        assertEquals(FORUM_POST_CONTENT_2, forumPostDao.getForumPostsByForumThread(forumThread_2).get(0).getForumPostContent());
        assertEquals(forumThread_2, forumPostDao.getForumPostsByForumThread(forumThread_2).get(0).getForumThread());
        assertEquals(user_2, forumPostDao.getForumPostsByForumThread(forumThread_2).get(0).getUser());
    }

    @Test
    public void testRemoveForumPost() {
        Throwable e = null;

        assertNotNull(forumPostDao.getForumPostsByForumThread(forumThread_1).get(0));
        forumPostDao.removeForumPost(forumPostDao.getForumPostsByForumThread(forumThread_1).get(0).getForumPostId());

        try {
            assertNull(forumPostDao.getForumPostsByForumThread(forumThread_1).get(0));
        } catch (Throwable ex) {
            e = ex;
        }

        assertTrue(e instanceof IndexOutOfBoundsException);
    }
}
