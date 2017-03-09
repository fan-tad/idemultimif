package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Todo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class TodoManager {
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;
    private TodoDao todoDao;
    private ManagerUtility managerUtility;


    public TodoManager(EntityManager em) {
        //this.em = em;
        managerUtility = new ManagerUtility();
        this.todoDao = new TodoDao(em);
    }


    public Todo newTodo(String todoName, String todoContent, Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Todo todo = todoDao.createTodo(todoName, todoContent, project);

        project.getTodosCollection().add(todo);

        transaction.commit();
        return todo;
    }

    public List<Todo> getTodos(Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        List<Todo> lt = todoDao.getTodosByProject(project);

        transaction.commit();
        return lt;
    }

    public void deleteTodoByFileNameAndProject(String todoName, Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        int a = todoDao.getTodosByNameAndProjectId(todoName, project.getProjectId()).size();
        for(int i = 0; i < a; i++) {
            todoDao.removeTodo(todoDao.getTodosByNameAndProjectId(todoName, project.getProjectId()).get(0).getTodoId());
        }
        transaction.commit();
    }
}
