package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class BelongsDao {

    private EntityManager em;

    public BelongsDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère les droits d'un utilisateur sur un projet en fonction des Id
     * 0 = admin, 1 = dev, 2 = reporter
     * @param projectId l'Id du projet
     * @param userId l'Id de l'utilisateur
     * @return les droits de l'utilisateur sur le proje
     */
    public Belongs getBelongsByUserAndProject(int projectId, int userId) {
        Belongs b = null;
        Query query = em.createNamedQuery("Belongs.findByUserAndProject", Belongs.class).setParameter("projectId", projectId).setParameter("userId", userId);
        try {
            b = (Belongs) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return b;
    }

    public List<Belongs> getBelongsUsersByProject(int projectId){

        List<Belongs> listBelongs = null;
        Query query = em.createNamedQuery("Belongs.findByProject", Belongs.class).setParameter("projectId", projectId);

        try {
            listBelongs =  query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return listBelongs;
    }

    /**
     * récupère tous les droits contenus dans la BDD
     * @return la liste de droits
     */
    public List<Belongs> getAllBelongs() {
        List<Belongs> lb = null;
        Query query = em.createNamedQuery("Belongs.findAll", Belongs.class);

        try {
            lb = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lb;
    }

    /**
     * crée des droits d'un utilisateur sur un projet
     * @param belongsRight la valeur du droit
     * @param project le projet
     * @param user l'utilisateur
     * @return le droit crée
     */
    public Belongs createBelongs(int belongsRight, Project project, User user) {
        Belongs b = getBelongsByUserAndProject(project.getProjectId(), user.getUserId());
        if (b == null) {
            b = new Belongs(user, project, belongsRight);
            em.persist(b);
            return b;
        }
        else {
            return b;
        }
    }

    /**
     * supprime les droits d'un utilisateur sur un projet en fonction des Id
     * @param projectId l'Id du projet
     * @param userId l'Id de l'utilisateur
     */
    public void removeBelongs(int projectId, int userId) {
        Belongs b = getBelongsByUserAndProject(projectId, userId);
        if(b != null) {
            em.remove(b);
        }
    }

    public Belongs updateUserRight(Belongs belongs, int userRight){
        belongs.setBelongsRight(userRight);
        return belongs;
    }
}
