package fr.univ_lyon1.info.m1.TestModele;

import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import fr.univ_lyon1.info.m1.Modele.WikiPage;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TestWikiPage extends TestCase {

    private static final int WIKI_PAGE_ID = 0;
    private static final String WIKI_PAGE_NAME = "nom_page_wiki_test";
    private static final String WIKI_PAGE_CONTENT = "contenu_page_wiki_test";

    private WikiPage wikiPage;
    private Project project;

    public TestWikiPage(String name) {
        super(name);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        wikiPage = new WikiPage(WIKI_PAGE_NAME, WIKI_PAGE_CONTENT, project);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        wikiPage = null;
    }

    public void testWikiPageId() {
        assertEquals(WIKI_PAGE_ID, wikiPage.getWikiPageId());
    }

    public void testWikiPageName() {
        assertEquals(WIKI_PAGE_NAME, wikiPage.getWikiPageName());
    }

    public void testWikiPageContent() {
        assertEquals(WIKI_PAGE_CONTENT, wikiPage.getWikiPageContent());
    }

    public void testProject() {
        assertSame(project, wikiPage.getProject());
    }
}
