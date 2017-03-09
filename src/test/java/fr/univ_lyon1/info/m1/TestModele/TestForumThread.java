package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.ForumPost;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestForumThread extends TestCase {

    private static final int FORUM_THREAD_ID = 0;
    private static final String FORUM_THREAD_NAME = "nom_sujet_forum_test";

    private ForumThread forumThread;
    private Project project;
    private User user;

    public TestForumThread(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        user = new User();
        forumThread = new ForumThread(FORUM_THREAD_NAME, project, user);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        forumThread = null;
    }

    public void testForumThreadId() {
        assertEquals(FORUM_THREAD_ID, forumThread.getForumThreadId());
    }

    public void testForumThreadName() {
        assertEquals(FORUM_THREAD_NAME, forumThread.getForumThreadName());
    }

    public void testProject() {
        assertSame(project, forumThread.getProject());
    }

    public void testUser() {
        assertSame(user, forumThread.getUser());
    }

    public void testForumPostsCollection() {
        ForumPost forumPost = new ForumPost();
        forumPost.setForumThread(forumThread);
        forumThread.getForumPostsCollection().add(forumPost);

        assertSame(forumPost, forumThread.getForumPostsCollection().get(0));
        assertSame(forumThread, forumThread.getForumPostsCollection().get(0).getForumThread());
    }
}
