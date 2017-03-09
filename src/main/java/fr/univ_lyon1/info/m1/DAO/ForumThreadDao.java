package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ForumThreadDao {

    private EntityManager em;

    public ForumThreadDao(EntityManager em) {
        this.em = em;
    }

    /**
     * récupère le sujet d'un forum par son Id
     * @param forumThreadId l'Id du Sujet
     * @return le ForumThread
     */
    public ForumThread getForumThreadById (int forumThreadId) {
        ForumThread fT = null;
        Query query = em.createNamedQuery("ForumThread.findById", ForumThread.class).setParameter("forumThreadId", forumThreadId);
        try {
            fT = (ForumThread) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return fT;
    }

    /**
     * Recupère tous les sujets du forum d'un projet
     * @param project un projet
     * @return la liste de sujets du projet
     */
    public List<ForumThread> getForumThreadsByProject(Project project) {
        List<ForumThread> lfT = null;
        Query query = em.createNamedQuery("ForumThread.findAllByProject", ForumThread.class).setParameter("project", project);
        try {
            lfT = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lfT;
    }

    /**
     * crée un ForumThread
     * @param forumThreadName le nom du sujet est rattaché
     * @param project le projet auquel le sujet est rattaché
     * @param user l'utilisateur qui a crée le sujet
     * @return le ForumThread crée
     */
    public ForumThread createForumThread(String forumThreadName, Project project, User user) {
        ForumThread fT = new ForumThread(forumThreadName, project, user);
        em.persist(fT);
        return fT;
    }

    /**
     * Supprime un sujet du forum par son Id
     * @param forumThreadId l'Id du sujet
     */
    public void removeForumThread(int forumThreadId) {
        ForumThread ft = getForumThreadById(forumThreadId);
        if(ft != null) {
            em.remove(ft);
        }
    }
}
