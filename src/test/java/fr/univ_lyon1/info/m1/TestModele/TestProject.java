package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.*;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.util.Date;

public class TestProject extends TestCase {

    private static final int PROJECT_ID = 0;
    private static final String PROJECT_NAME= "nom_projet_test";
    private static final String PROJECT_PATH = "chemin_projet_test";
    private static final String PROJECT_DESCRIPTION = "description_projet_test";

    private Project project;

    public TestProject(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project(PROJECT_NAME, PROJECT_PATH, PROJECT_DESCRIPTION);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        project = null;
    }

    public void testProjetId() {
        assertEquals(PROJECT_ID, project.getProjectId());
    }

    public void testProjectName() {
        assertEquals(PROJECT_NAME, project.getProjectName());
    }

    public void testProjectPath() {
        assertEquals(PROJECT_PATH, project.getProjectPath());
    }

    public void testProjectDescription() {
        assertEquals(PROJECT_DESCRIPTION, project.getProjectDescription());
    }

    public void testBelongs() {
        User user = new User();
        Belongs belongs = new Belongs(user, project, 0);
        project.addBelongs(belongs);

        assertSame(belongs, project.getBelongs().get(0));
        assertSame(project, project.getBelongs().get(0).getProject());
        assertSame(user, project.getBelongs().get(0).getUser());
        assertEquals(0, project.getBelongs().get(0).getBelongsRight());
    }

    public void testTodosCollection() {
        Todo todo = new Todo();
        todo.setProject(project);
        project.getTodosCollection().add(todo);

        assertSame(todo, project.getTodosCollection().get(0));
        assertSame(project, project.getTodosCollection().get(0).getProject());
    }

    public void testTicketsCollection() {
        Ticket ticket = new Ticket();
        ticket.setProject(project);
        project.getTicketsCollection().add(ticket);

        assertSame(ticket, project.getTicketsCollection().get(0));
        assertSame(project, project.getTicketsCollection().get(0).getProject());
    }

    public void testForumThreadsCollection() {
        ForumThread forumThread = new ForumThread();
        forumThread.setProject(project);
        project.getForumThreadsCollection().add(forumThread);

        assertSame(forumThread, project.getForumThreadsCollection().get(0));
        assertSame(project, project.getForumThreadsCollection().get(0).getProject());
    }
}
