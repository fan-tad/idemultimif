package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Ticket;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class TicketManager {

    private ManagerUtility managerUtility;
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;

    private TicketDao ticketDao;

    public TicketManager(EntityManager em) {
        //this.em = em;
        managerUtility = new ManagerUtility();
        this.ticketDao = new TicketDao(em);
    }

    public Ticket getTicket(int idTicket) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Ticket ticket = ticketDao.getTicketById(idTicket);

        transaction.commit();
        return ticket;
    }

    public List<Ticket> getTickets(Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        List<Ticket> lt = ticketDao.getTicketsByProject(project);

        transaction.commit();
        return lt;
    }

    public void deleteTicket(int idTicket) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        ticketDao.removeTicket(idTicket);

        transaction.commit();
    }

    public Ticket newTicketWithUserAssigned(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner, User userAssigned) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Ticket ticket = ticketDao.createTicketWithUserAssigned(ticketName, ticketBadge, ticketContent, project, userOwner, userAssigned);

        project.getTicketsCollection().add(ticket);
        userOwner.getTicketOwnerCollection().add(ticket);
        userAssigned.getTicketAssignedCollection().add(ticket);

        transaction.commit();
        return ticket;
    }

    public Ticket newTicketWithoutUserAssigned(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Ticket ticket = ticketDao.createTicketWithoutUserAssigned(ticketName, ticketBadge, ticketContent, project, userOwner);
        project.getTicketsCollection().add(ticket);
        userOwner.getTicketOwnerCollection().add(ticket);

        transaction.commit();
        return ticket;
    }
}
