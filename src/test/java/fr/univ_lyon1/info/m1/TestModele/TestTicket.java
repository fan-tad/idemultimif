package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Ticket;
import fr.univ_lyon1.info.m1.Modele.Todo;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestTicket extends TestCase {

    private static final int TICKET_ID = 0;
    private static final String TICKET_NAME= "nom_ticket_test";
    private static final String TICKET_BADGE = "badge_ticket_test";
    private static final String TICKET_CONTENT = "contenu_ticket_test";

    private Ticket ticket;
    private Project project;
    private User user_1;
    private User user_2;

    public TestTicket(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        user_1 = new User();
        user_2 = new User();
        user_2.setUserId(1);
        ticket = new Ticket(TICKET_NAME, TICKET_BADGE, TICKET_CONTENT, project, user_1, user_2);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        ticket = null;
    }

    public void testTicketId() {
        assertEquals(TICKET_ID, ticket.getTicketId());
    }

    public void testTicketName() {
        assertEquals(TICKET_NAME, ticket.getTicketName());
    }

    public void testTicketBadge() {
        assertEquals(TICKET_BADGE, ticket.getTicketBadge());
    }

    public void testTicketContent() {
        assertEquals(TICKET_CONTENT, ticket.getTicketContent());
    }

    public void testProject() {
        assertSame(project, ticket.getProject());
    }

    public void testUserOwner() {
        assertSame(user_1, ticket.getUserOwner());
    }

    public void testUserAssigned() {
        assertSame(user_2, ticket.getUserAssigned());
    }
}
