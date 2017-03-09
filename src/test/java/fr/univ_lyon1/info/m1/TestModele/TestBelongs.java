package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestBelongs extends TestCase {

    private static final int BELONGS_RIGHT = 0;

    private Belongs belongs;
    private User user;
    private Project project;

    public TestBelongs(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        user = new User();
        project = new Project();
        belongs = new Belongs(user, project, BELONGS_RIGHT);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        belongs = null;
    }

    public void testBelongsRight() {
        assertEquals(BELONGS_RIGHT, belongs.getBelongsRight());
    }

    public void testUser() {
        assertSame(user, belongs.getUser());
    }

    public void testProject() {
        assertSame(project, belongs.getProject());
    }
}
