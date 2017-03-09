package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.WikiPage;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class WikiPageDao {

    private EntityManager em;

    public WikiPageDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère la page Wiki en fonction de son Id
     * @param wikiPageId l'Id du wiki
     * @return le wiki
     */
    public WikiPage getWikiPageById(int wikiPageId) {
        WikiPage wp = null;
        Query query = em.createNamedQuery("WikiPage.findById", WikiPage.class).setParameter("wikiPageId", wikiPageId);

        try {
            wp = (WikiPage) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao (e);
        }

        return wp;
    }

    /**
     * Récupère un Wiki en fonction du projet auquel il est rattaché
     * @param project le projet
     * @return le wiki
     */
    public WikiPage getWikiPageByProject(Project project) {
        WikiPage wp = null;
        Query query = em.createNamedQuery("WikiPage.findByProject", WikiPage.class).setParameter("project", project);

        try {
            wp = (WikiPage) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return wp;
    }

    /**
     * Récupère une page wiki en fonction du nom d'un projet
     * @param projectName le nom du projet
     * @return le wiki
     */
    public WikiPage getWikiPageByProjectName(String projectName) {
        WikiPage wp = null;
        Query query = em.createNamedQuery("WikiPage.findByProjectName", WikiPage.class).setParameter("projectName", projectName);
        try {
            wp = (WikiPage) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return wp;
    }

    /**
     * Modifie le contenu d'un wiki
     * @param wikiContent le contenu à modifier
     * @param wikiPage le wiki
     */
    public void setWikiPageContent(String wikiContent, WikiPage wikiPage) {
        try {
            wikiPage.setWikiPageContent(wikiContent);
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
    }

    /**
     * Crée une page wiki
     * @param wikiPageName le nom de la page Wiki
     * @param wikiPageContent le contenu de la page Wiki
     * @param project le projet auquel la page Wiki est rattaché
     * @return la page Wiki crée
     */
    public WikiPage createWikiPage(String wikiPageName, String wikiPageContent, Project project) {
            WikiPage wp = new WikiPage(wikiPageName, wikiPageContent, project);
            em.persist(wp);
            return wp;
    }

    /**
     * Supprime une page Wiki en fonction de son Id
     * @param wikiPageId l'Id de la page Wiki
     */
    public void removeWikiPage(int wikiPageId) {
        WikiPage wp = getWikiPageById(wikiPageId);
        if(wp != null) {
            em.remove(wp);
        }
    }
}
