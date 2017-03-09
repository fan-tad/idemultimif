package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.ProjectDao;
import fr.univ_lyon1.info.m1.Modele.Project;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class TestProject extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String PROJECT_NAME_1 = "nom_projet_test_1";
    private static final String PROJECT_PATH_1 = "chemin_projet_test_1";
    private static final String PROJECT_DESCRIPTION_1 = "description_projet_test_1";

    private static final String PROJECT_NAME_2 = "nom_projet_test_2";
    private static final String PROJECT_PATH_2 = "chemin_projet_test_2";
    private static final String PROJECT_DESCRIPTION_2 = "description_projet_test_2";

    private EntityManager entityManager;
    private ProjectDao projectDao;

    private Project project_1;
    private Project project_2;

    public TestProject(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        projectDao = new ProjectDao(entityManager);

        project_1 = new Project(PROJECT_NAME_1, PROJECT_PATH_1, PROJECT_DESCRIPTION_1);
        project_2 = new Project(PROJECT_NAME_2, PROJECT_PATH_2, PROJECT_DESCRIPTION_2);

        entityManager.getTransaction().begin();

        entityManager.persist(project_1);
        entityManager.persist(project_2);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        project_2 = null;
        project_1 = null;
        projectDao = null;
    }

    @Test
    public void testGetProjectByName() {
        assertSame(project_1, projectDao.getProjectByName(PROJECT_NAME_1));
        assertNotSame(project_2, projectDao.getProjectByName(PROJECT_NAME_1));
    }

    @Test
    public void testGetAllProjects() {
        List<Project> projectsList = entityManager.createNamedQuery("Project.findAll", Project.class).getResultList();
        assertEquals(projectsList, projectDao.getAllProjects());
    }

    @Test
    public void testGetIdProjectByName() {
        assertEquals(projectDao.getProjectByName(PROJECT_NAME_1).getProjectId(), projectDao.getProjectByName(PROJECT_NAME_1).getProjectId());
        //assertNotSame(projectDao.getProjectByName(PROJECT_NAME_2).getProjectId(), projectDao.getProjectByName(PROJECT_NAME_1).getProjectId());
    }

    @Test
    public void testCreateProject() {
        projectDao.createProject(PROJECT_NAME_2, PROJECT_PATH_2, PROJECT_DESCRIPTION_2);
        assertEquals(PROJECT_NAME_2, projectDao.getProjectByName(PROJECT_NAME_2).getProjectName());
        assertEquals(PROJECT_PATH_2, projectDao.getProjectByName(PROJECT_NAME_2).getProjectPath());
        assertEquals(PROJECT_DESCRIPTION_2, projectDao.getProjectByName(PROJECT_NAME_2).getProjectDescription());
    }

    @Test
    public void testRemoveProject() {
        assertNotNull(projectDao.getProjectByName(PROJECT_NAME_1));
        projectDao.removeProject(PROJECT_NAME_1);
        assertNull(projectDao.getProjectByName(PROJECT_NAME_1));
    }

    @Test
    public void testUpdateProjectName() {
        assertEquals(PROJECT_NAME_1, projectDao.getProjectByName(PROJECT_NAME_1).getProjectName());
        projectDao.updateProjectName(project_1, PROJECT_NAME_1 + "bis");
    }
}
