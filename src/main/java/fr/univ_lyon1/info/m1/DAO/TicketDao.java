package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Ticket;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class TicketDao {

    private EntityManager em;

    public TicketDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère un ticket en fonction de son Id
     * @param ticketId l'Id du ticket
     * @return le ticket
     */
    public Ticket getTicketById (int ticketId) {
        Ticket t = null;
        Query query = em.createNamedQuery("Ticket.findById", Ticket.class).setParameter("ticketId", ticketId);
        try {
            t = (Ticket) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return t;
    }

    /**
     * Récupère les tickets d'un projet
     * @param project le projet
     * @return la liste des ticket en fonction du projet
     */
    public List<Ticket> getTicketsByProject (Project project) {
        List<Ticket> lt = null;
        Query query = em.createNamedQuery("Ticket.findAllByProject", Ticket.class).setParameter("project", project);
        try {
            lt = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lt;
    }

    /**
     * Crée un ticket où un utilisateur assigne un autre utilisateur
     * @param ticketName le nom du ticket
     * @param ticketBadge le badge du ticket
     * @param ticketContent le contenu du ticket
     * @param project le projet auquel le ticket est rattaché
     * @param userOwner le créateur du ticket
     * @param userAssigned l'utilisateur auquel le ticket est assigné
     * @return le ticket crée
     */
    public Ticket createTicketWithUserAssigned(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner, User userAssigned) {
        Ticket t = new Ticket(ticketName, ticketBadge, ticketContent, project, userOwner, userAssigned);
        em.persist(t);
        return t;
    }

    /**
     * Crée un ticket sans assignation d'utilisateur
     * @param ticketName le nom du ticket
     * @param ticketBadge le badge du ticket
     * @param ticketContent le contenu du ticket
     * @param project le projet auquel le ticket est rattaché
     * @param userOwner le créateur du ticket
     * @return le ticket crée
     */
    public Ticket createTicketWithoutUserAssigned(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner) {
        Ticket t = new Ticket(ticketName, ticketBadge, ticketContent, project, userOwner);
        em.persist(t);
        return t;
    }

    /**
     * Supprime un ticket en fonction de son Id
     * @param ticketId l'Id du ticket
     */
    public void removeTicket(int ticketId) {
        Ticket t = getTicketById(ticketId);
        if(t != null) {
            em.remove(t);
        }
    }
}
