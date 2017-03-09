package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.Project;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ProjectDao {

    private EntityManager em;

    public ProjectDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère un projet par son nom
     * @param projectName le nom du projet
     * @return le projet
     */
    public Project getProjectByName(String projectName) {
        Project p = null;
        Query query = em.createNamedQuery("Project.findByName", Project.class).setParameter("projectName", projectName);

        try {
            p = (Project) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return p;
    }

    public Project getProjectById(int projectId) {
        Project p = null;
        Query query = em.createNamedQuery("Project.findById", Project.class).setParameter("projectId", projectId);

        try {
            p = (Project) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return p;
    }

    /**
     * Récupère tous les projets
     * @return la liste de projets
     */
    public List<Project> getAllProjects() {
        List<Project> lp = null;
        Query query = em.createNamedQuery("Project.findAll", Project.class);

        try {
            lp = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lp;
    }

    /**
     * Récupère l'Id du projet grâce à son nom
     * @param projectName le nom du projet
     * @return l'Id du projet
     */
    public int getIdProjectByName(String projectName) {
        int p = 0;
        Query query = em.createNamedQuery("Project.findIdByName", Project.class).setParameter("projectName", projectName);

        try {
            p = (int) query.getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return p;
    }

    /**
     * Création d'un projet
     * @param projectName le nom du projet
     * @param projectPath le chemin du projet
     * @param projectDescription la description du projet
     * @return le projet
     */
    public Project createProject(String projectName, String projectPath, String projectDescription) {
        Project p = getProjectByName(projectName);
        if (p == null) {
            p = new Project(projectName, projectPath, projectDescription);
            em.persist(p);
            return p;
        }
        else {
            return p;
        }
    }

    /**
     * supprime un projet
     * @param projectName le projet à supprimer
     */
    public void removeProject(String projectName) {
        Project p = getProjectByName(projectName);
        if(p != null) {
            em.remove(p);
        }
    }

    /**
     * met à jour le nom d'un projet
     * @param project le projet à mettre à jour
     * @param newProjectName le nouveau nom du projet
     */
    public Project updateProjectName(Project project, String newProjectName){
        project.setProjectName(newProjectName);
        return project;
    }
}
