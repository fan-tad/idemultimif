package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Todo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class TodoDao {

    private EntityManager em;

    public TodoDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère un todo en fonction de son Id
     * @param todoId l'Id du todo
     * @return le todo
     */
    public Todo getTodoById(int todoId) {
        Todo t = null;
        Query query = em.createNamedQuery("Todo.findById", Todo.class).setParameter("todoId", todoId);

        try {
            t = (Todo) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return t;
    }

    /**
     * Récupère une liste de todos en fonction de leur nom (le nom du fichier) et de l'Id du projet auquel ils sont rattachés
     * @param todoName le nom du todo
     * @param projectId l'Id du projet
     * @return la liste de todos correspondante
     */
    public List<Todo> getTodosByNameAndProjectId(String todoName, int projectId) {
        List<Todo> lt = null;
        Query query = em.createNamedQuery("Todo.findAllByNameAndProjectId", Todo.class).setParameter("todoName", todoName).setParameter("projectId", projectId);
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
     * Récupère tous les todos d'un projet
     * @param project le projet pour lequel on souhaite récupérer les todos
     * @return la liste de todos correspondante
     */
    public List<Todo> getTodosByProject(Project project) {
        List<Todo> lt = null;
        Query query = em.createNamedQuery("Todo.findAllByProject", Todo.class).setParameter("project", project);

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
     * Crée un todo
     * @param todoName le nom du todo (qui correspond au nom du fichier)
     * @param todoContent le contenu du todo
     * @param project le projet auquel il est rattaché
     * @return le todo crée
     */
    public Todo createTodo(String todoName, String todoContent, Project project) {
        Todo t = new Todo(todoName, todoContent, project);
        em.persist(t);
        return t;
    }

    /**
     * Supprime un todo en fonction de son Id
     * @param todoId l'Id du todo
     */
    public void removeTodo(int todoId) {
        Todo t = getTodoById(todoId);
        if(t != null) {
            em.remove(t);
        }
    }
}
