package fr.univ_lyon1.info.m1.TestDao;

import fr.univ_lyon1.info.m1.DAO.WikiPageDao;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.WikiPage;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class TestWikiPage extends TestCase {

    private static final String PERSISTENCE_UNIT_TEST = "pu-sharkode-test";

    private static final String WIKI_PAGE_NAME_1 = "nom_page_wiki_test_1";
    private static final String WIKI_PAGE_CONTENT_1 = "contenu_page_wiki_test_1";
    private static final String WIKI_PAGE_PROJECT_NAME_1 = "nom_projet_page_wiki_test_1";

    private static final String WIKI_PAGE_NAME_2 = "nom_page_wiki_2";
    private static final String WIKI_PAGE_CONTENT_2 = "contenu_page_wiki_test_2";
    private static final String WIKI_PAGE_PROJECT_NAME_2 = "nom_projet_page_wiki_test_2";

    private EntityManager entityManager;
    private WikiPageDao wikiPageDao;

    private Project project_1;
    private Project project_2;

    private WikiPage wikiPage_1;
    private WikiPage wikiPage_2;

    public TestWikiPage(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST).createEntityManager();
        wikiPageDao = new WikiPageDao(entityManager);

        project_1 = new Project();
        project_1.setProjectName(WIKI_PAGE_PROJECT_NAME_1);
        project_2 = new Project();
        project_2.setProjectName(WIKI_PAGE_PROJECT_NAME_2);

        wikiPage_1 = new WikiPage(WIKI_PAGE_NAME_1, WIKI_PAGE_CONTENT_1, project_1);
        wikiPage_2 = new WikiPage(WIKI_PAGE_NAME_2, WIKI_PAGE_CONTENT_2, project_2);

        entityManager.getTransaction().begin();

        entityManager.persist(wikiPage_1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        entityManager.getTransaction().rollback();

        entityManager.clear();
        entityManager.close();
        entityManager = null;

        wikiPage_2 = null;
        wikiPage_1 = null;
        project_2 = null;
        project_1 = null;
        wikiPageDao = null;
    }

    @Test
    public void testGetWikiPageById() {
        assertSame(wikiPage_1, wikiPageDao.getWikiPageById(wikiPageDao.getWikiPageByProject(project_1).getWikiPageId()));
        assertNotSame(wikiPage_2, wikiPageDao.getWikiPageById(wikiPageDao.getWikiPageByProject(project_1).getWikiPageId()));
    }

    @Test
    public void testGetWikiPageByProject() {
        assertSame(wikiPage_1, wikiPageDao.getWikiPageByProject(project_1));
        assertNotSame(wikiPage_2, wikiPageDao.getWikiPageByProject(project_1));
    }

    @Test
    public void testGetWikiPageByProjectName() {
        assertSame(wikiPage_1, wikiPageDao.getWikiPageByProject(project_1));
        assertNotSame(wikiPage_2, wikiPageDao.getWikiPageByProject(project_1));
    }

    @Test
    public void testSetWikiPageContent() {
        wikiPageDao.setWikiPageContent(WIKI_PAGE_CONTENT_1,  wikiPageDao.getWikiPageByProject(project_1));
        assertEquals(WIKI_PAGE_CONTENT_1, wikiPageDao.getWikiPageByProject(project_1).getWikiPageContent());
    }

    @Test
    public void testCreateWikiPage() {
        wikiPageDao.createWikiPage(WIKI_PAGE_NAME_2, WIKI_PAGE_CONTENT_2, project_2);
        assertEquals(WIKI_PAGE_NAME_2, wikiPageDao.getWikiPageByProject(project_2).getWikiPageName());
        assertEquals(WIKI_PAGE_CONTENT_2, wikiPageDao.getWikiPageByProject(project_2).getWikiPageContent());
        assertSame(project_2, wikiPageDao.getWikiPageByProject(project_2).getProject());
    }

    @Test
    public void testRemoveWikiPage() {
        assertNotNull(wikiPageDao.getWikiPageByProject(project_1));
        wikiPageDao.removeWikiPage(wikiPageDao.getWikiPageByProject(project_1).getWikiPageId());
        assertNull(wikiPageDao.getWikiPageByProject(project_1));
    }
}

