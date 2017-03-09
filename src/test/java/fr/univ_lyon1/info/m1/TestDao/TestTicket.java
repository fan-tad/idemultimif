package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.TicketDao;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Ticket;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestTicket extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String TICKET_NAME_1 = "nom_ticket_test_1";
    private static final String TICKET_BADGE_1 = "badge_ticket_test_1";
    private static final String TICKET_CONTENT_1 = "contenu_ticket_test_1";
    private static final String TICKET_PROJECT_NAME_1 = "nom_projet_ticket_test_1";
    private static final String TICKET_USER_NAME_1 = "nom_utilisateur_ticket_test_1";

    private static final String TICKET_NAME_2 = "nom_ticket_test_2";
    private static final String TICKET_BADGE_2 = "badge_ticket_test_2";
    private static final String TICKET_CONTENT_2 = "contenu_ticket_test_2";
    private static final String TICKET_PROJECT_NAME_2 = "nom_projet_ticket_test_2";
    private static final String TICKET_USER_NAME_2 = "nom_utilisateur_ticket_test_2";

    private EntityManager entityManager;
    private TicketDao ticketDao;

    private Project project_1;
    private Project project_2;

    private User user_1;
    private User user_2;

    private Ticket ticket_1;
    private Ticket ticket_2;

    public TestTicket(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        ticketDao = new TicketDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName(TICKET_PROJECT_NAME_1);
        project_2 = new Project();
        project_2.setProjectName(TICKET_PROJECT_NAME_2);

        user_1 = new User();
        user_1.setUserName(TICKET_USER_NAME_1);
        user_2 = new User();
        user_2.setUserName(TICKET_USER_NAME_2);

        ticket_1 = new Ticket(TICKET_NAME_1, TICKET_BADGE_1, TICKET_CONTENT_1, project_1, user_1);
        ticket_2 = new Ticket(TICKET_NAME_2, TICKET_BADGE_2, TICKET_CONTENT_2, project_2, user_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);

        entityManager.persist(user_1);
        entityManager.persist(user_2);

        entityManager.persist(ticket_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        ticket_2 = null;
        ticket_1 = null;
        user_2 = null;
        user_1 = null;
        project_2 = null;
        project_1 = null;
        ticketDao = null;
    }

    @Test
    public void testGetTicketById() {
        assertSame(ticket_1, ticketDao.getTicketById(ticketDao.getTicketsByProject(project_1).get(0).getTicketId()));
        assertNotSame(ticket_2, ticketDao.getTicketById(ticketDao.getTicketsByProject(project_1).get(0).getTicketId()));
    }

    @Test
    public void testGetTicketsByProject() {
        List<Ticket> ticketsList = entityManager.createNamedQuery("Ticket.findAllByProject", Ticket.class).setParameter("project", project_1).getResultList();
        assertEquals(ticketsList, ticketDao.getTicketsByProject(project_1));
    }

    @Test
    public void testCreateTicketWithUserAssigned() {
        ticketDao.createTicketWithUserAssigned(TICKET_NAME_2, TICKET_BADGE_2, TICKET_CONTENT_2, project_2, user_2, user_2);
        assertEquals(TICKET_NAME_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketName());
        assertEquals(TICKET_BADGE_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketBadge());
        assertEquals(TICKET_CONTENT_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketContent());
        assertSame(project_2, ticketDao.getTicketsByProject(project_2).get(0).getProject());
        assertSame(user_2, ticketDao.getTicketsByProject(project_2).get(0).getUserOwner());
        assertSame(user_2, ticketDao.getTicketsByProject(project_2).get(0).getUserAssigned());
    }

    @Test
    public void testCreateTicketWithoutUserAssigned() {
        ticketDao.createTicketWithoutUserAssigned(TICKET_NAME_2, TICKET_BADGE_2, TICKET_CONTENT_2, project_2, user_2);
        assertEquals(TICKET_NAME_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketName());
        assertEquals(TICKET_BADGE_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketBadge());
        assertEquals(TICKET_CONTENT_2, ticketDao.getTicketsByProject(project_2).get(0).getTicketContent());
        assertSame(project_2, ticketDao.getTicketsByProject(project_2).get(0).getProject());
        assertSame(user_2, ticketDao.getTicketsByProject(project_2).get(0).getUserOwner());
    }

    @Test
    public void testRemoveTicket() {
        Throwable e = null;

        assertNotNull(ticketDao.getTicketsByProject(project_1).get(0));
        ticketDao.removeTicket(ticketDao.getTicketsByProject(project_1).get(0).getTicketId());

        try {
        assertNull(ticketDao.getTicketsByProject(project_1).get(0));
        } catch (Throwable ex) {
        e = ex;
        }

        assertTrue(e instanceof IndexOutOfBoundsException);
    }
}
