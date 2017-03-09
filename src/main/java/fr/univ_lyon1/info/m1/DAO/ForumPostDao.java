package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.ForumPost;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class ForumPostDao {

    private EntityManager em;

    public ForumPostDao(EntityManager em) {
        this.em = em;
    }

    /**
     * récupère tous les posts en fonction d'un thread
     * @param forumThread un sujet du forum
     * @return la liste de posts
     */
    public List<ForumPost> getForumPostsByForumThread(ForumThread forumThread) {
        List<ForumPost> lfP = null;
        Query query = em.createNamedQuery("ForumPost.findAllByForumThread", ForumPost.class).setParameter("forumThread", forumThread);
        try {
            lfP = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lfP;
    }

    /**
     * récupère un post en fonction de son Id
     * @param forumPostId l'Id du post
     * @return le post
     */
    public ForumPost getForumPostById (int forumPostId) {
        ForumPost fp = null;
        Query query = em.createNamedQuery("ForumPost.findById", ForumPost.class).setParameter("forumPostId", forumPostId);
        try {
            fp = (ForumPost) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return fp;
    }

    /**
     * crée un post dans un sujet du forum
     * @param forumPostCreateDate la date de création du post
     * @param forumPostModifyDate la date de modification du post
     * @param forumPostContent le contenu du post
     * @param forumThread le sujet auquel le post est rattaché
     * @param user l'utilisateur qui crée le post
     * @return le post crée
     */
    public ForumPost createForumPost(Date forumPostCreateDate, Date forumPostModifyDate, String forumPostContent, ForumThread forumThread, User user) {
        ForumPost fP = new ForumPost(forumPostCreateDate, forumPostModifyDate, forumPostContent, forumThread, user);
        em.persist(fP);
        return fP;
    }

    /**
     * supprime le post d'un forum en fonction de son Id
     * @param forumPostId l'id du post
     */
    public void removeForumPost(int forumPostId) {
        ForumPost fp = getForumPostById(forumPostId);
        if(fp != null) {
            em.remove(fp);
        }
    }
}
