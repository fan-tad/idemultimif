package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.*;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestTodo extends TestCase {

    private static final int TODO_ID = 0;
    private static final String TODO_NAME= "nom_todo_test";
    private static final String TODO_CONTENT = "contenu_todo_test";

    private Todo todo;
    private Project project;

    public TestTodo(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        todo = new Todo(TODO_NAME, TODO_CONTENT, project);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        todo = null;
    }

    public void testTodoId() {
        assertEquals(TODO_ID, todo.getTodoId());
    }

    public void testTodoName() {
        assertEquals(TODO_NAME, todo.getTodoName());
    }

    public void testTodoContent() {
        assertEquals(TODO_CONTENT, todo.getTodoContent());
    }

    public void testProject() {
        assertSame(project, todo.getProject());
    }
}
