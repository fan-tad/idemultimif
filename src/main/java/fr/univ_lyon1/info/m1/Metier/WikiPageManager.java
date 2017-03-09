package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.WikiPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

@Transactional
public class WikiPageManager {

    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;

    private ManagerUtility managerUtility;
    private WikiPageDao wikiPageDao;

    public WikiPageManager(EntityManager em) {
        //this.em = em;
        managerUtility = new ManagerUtility();
        this.wikiPageDao = new WikiPageDao(em);
    }

    public WikiPage getwiki(String projectName) {

        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        WikiPage wikiPage = wikiPageDao.getWikiPageByProjectName(projectName);

        transaction.commit();
        return wikiPage;
    }

    public WikiPage getWIkiByProject(Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        WikiPage wikiPage = wikiPageDao.getWikiPageByProject(project);

        transaction.commit();
        return wikiPage;
    }

    public void setWiki(String wikiContent, Project project) {

        WikiPage wikiPage = this.getWIkiByProject(project);
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();



        wikiPageDao.setWikiPageContent(wikiContent, wikiPage);

        transaction.commit();
    }
}
