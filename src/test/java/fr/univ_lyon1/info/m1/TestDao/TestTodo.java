package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.TodoDao;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.Todo;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestTodo extends TestCase {
    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String TODO_NAME_1 = "nom_todo_test_1";
    private static final String TODO_CONTENT_1 = "contenu_page_wiki_test_1";
    private static final String TODO_PROJECT_NAME_1 = "nom_projet_todo_test_1";

    private static final String TODO_NAME_2 = "nom_page_wiki_2";
    private static final String TODO_CONTENT_2 = "contenu_page_wiki_test_2";
    private static final String TODO_PROJECT_NAME_2 = "nom_projet_todo_test_2";

    private EntityManager entityManager;
    private TodoDao todoDao;

    private Project project_1;
    private Project project_2;

    private Todo todo_1;
    private Todo todo_2;

    public TestTodo(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        todoDao = new TodoDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName(TODO_PROJECT_NAME_1);
        project_2 = new Project();
        project_2.setProjectName(TODO_PROJECT_NAME_2);

        todo_1 = new Todo(TODO_NAME_1, TODO_CONTENT_1, project_1);
        todo_2 = new Todo(TODO_NAME_2, TODO_CONTENT_2, project_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);
        entityManager.persist(todo_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        todo_2 = null;
        todo_1 = null;
        project_2 = null;
        project_1 = null;
        todoDao = null;
    }



    @Test
    public void testGetTodosByProject() {
        List<Todo> todosList = entityManager.createNamedQuery("Todo.findAllByProject", Todo.class).setParameter("project", project_1).getResultList();
        assertEquals(todosList, todoDao.getTodosByProject(project_1));
    }

    @Test
    public void testGetTodoById() {
        assertSame(todo_1, todoDao.getTodoById(todoDao.getTodosByProject(project_1).get(0).getTodoId()));
        assertNotSame(todo_2, todoDao.getTodoById(todoDao.getTodosByProject(project_1).get(0).getTodoId()));
    }

    @Test
    public void testCreateTodo() {
        todoDao.createTodo(TODO_NAME_2, TODO_CONTENT_2, project_2);
        assertEquals(TODO_NAME_2, todoDao.getTodosByProject(project_2).get(0).getTodoName());
        assertEquals(TODO_CONTENT_2, todoDao.getTodosByProject(project_2).get(0).getTodoContent());
    }

    @Test
    public void testRemoveTodo() {
        Throwable e = null;

        assertNotNull(todoDao.getTodosByProject(project_1).get(0));
        todoDao.removeTodo(todoDao.getTodosByProject(project_1).get(0).getTodoId());

        try {
            assertNull(todoDao.getTodosByProject(project_1).get(0));
        } catch (Throwable ex) {
            e = ex;
        }

        assertTrue(e instanceof IndexOutOfBoundsException);
    }
}
