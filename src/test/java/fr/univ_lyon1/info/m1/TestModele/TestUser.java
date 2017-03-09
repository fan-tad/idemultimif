package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.*;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestUser extends TestCase {

    private static final int USER_ID = 0;
    private static final String USER_NAME = "nom_utilisateur_test";
    private static final String USER_PASSWORD = "mot_de_passe_utilisateur_test";

    private User user;

    public TestUser(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        user = new User(USER_NAME, USER_PASSWORD);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        user = null;
    }

    public void testUserId() {
        assertEquals(USER_ID, user.getUserId());
    }

    public void testUserName() {
        assertEquals(USER_NAME, user.getUserName());
    }

    public void testUserPassword() {
        assertEquals(USER_PASSWORD, user.getUserPassword());
    }

    public void testBelongs() {
        Project project = new Project();
        Belongs belongs = new Belongs(user, project, 0);
        user.addBelongs(belongs);

        assertSame(belongs, user.getBelongs().get(0));
        assertSame(user, user.getBelongs().get(0).getUser());
        assertSame(project, user.getBelongs().get(0).getProject());
        assertEquals(0, user.getBelongs().get(0).getBelongsRight());
    }

    public void testTicketOwnerCollection() {
        Ticket ticket = new Ticket();
        ticket.setUserOwner(user);
        user.getTicketOwnerCollection().add(ticket);

        assertSame(ticket, user.getTicketOwnerCollection().get(0));
        assertSame(user, user.getTicketOwnerCollection().get(0).getUserOwner());
    }

    public void testTicketAssignedCollection() {
        Ticket ticket = new Ticket();
        ticket.setUserAssigned(user);
        user.getTicketAssignedCollection().add(ticket);

        assertSame(ticket, user.getTicketAssignedCollection().get(0));
        assertSame(user, user.getTicketAssignedCollection().get(0).getUserAssigned());
    }

    public void testforumThreadsCollection() {
        ForumThread forumThread = new ForumThread();
        forumThread.setUser(user);
        user.getForumThreadsCollection().add(forumThread);

        assertSame(forumThread, user.getForumThreadsCollection().get(0));
        assertSame(user, user.getForumThreadsCollection().get(0).getUser());
    }

    public void testforumPostsCollection() {
        ForumPost forumPost = new ForumPost();
        forumPost.setUser(user);
        user.getForumPostsCollection().add(forumPost);

        assertSame(forumPost, user.getForumPostsCollection().get(0));
        assertSame(user, user.getForumPostsCollection().get(0).getUser());
    }
}