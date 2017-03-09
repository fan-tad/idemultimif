package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.ForumPost;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.util.Date;

public class TestForumPost extends TestCase {

    private static final int FORUM_POST_ID = 0;
    private static final Date FORUM_POST_CREATE_DATE = new Date(0 - 2000);
    private static final Date FORUM_POST_MODIFY_DATE = new Date(0 - 2001);
    private static final String FORUM_POST_CONTENT = "contenu_message_forum_test";

    private ForumPost forumPost;
    private ForumThread forumThread;
    private User user;

    public TestForumPost(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        forumThread = new ForumThread();
        user = new User();
        forumPost = new ForumPost(FORUM_POST_CREATE_DATE, FORUM_POST_MODIFY_DATE, FORUM_POST_CONTENT, forumThread, user);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        forumPost = null;
    }

    public void testForumPostId() {
        assertEquals(FORUM_POST_ID, forumPost.getForumPostId());
    }

    public void testForumPostCreateDate() {
        assertEquals(FORUM_POST_CREATE_DATE, forumPost.getForumPostCreateDate());
    }

    public void testForumPostModifyDate() {
        assertEquals(FORUM_POST_MODIFY_DATE, forumPost.getForumPostModifyDate());
    }

    public void testForumPostContent() {
        assertEquals(FORUM_POST_CONTENT, forumPost.getForumPostContent());
    }

    public void testForumThread() {
        assertSame(forumThread, forumPost.getForumThread());
    }

    public void testUser() {
        assertSame(user, forumPost.getUser());
    }
}
