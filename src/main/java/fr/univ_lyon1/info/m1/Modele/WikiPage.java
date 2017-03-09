package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name="WikiPage.findById", query="SELECT wp FROM WikiPage wp WHERE wp.wikiPageId = :wikiPageId"),
        @NamedQuery(name="WikiPage.findByProject", query="SELECT wp FROM WikiPage wp WHERE wp.project = :project"),
        @NamedQuery(name = "WikiPage.findByProjectName", query = "SELECT wp FROM WikiPage wp WHERE wp.project.projectName = :projectName"),
})
public class WikiPage {

    /**
     * Identifiant de la page wiki
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wikiPageId")
    private int wikiPageId;

    /**
     * Nom de la page wiki
     */
    @Column(name = "wikiPageName")
    private String wikiPageName;

    /**
     * Contenu de la page wiki
     */
    @Column(name = "wikiPageContent")
    private String wikiPageContent;

    /**
     * Projet de la page wiki
     */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="projectId")
    private Project project;

    /**
     * Constructeur par défaut de la page wiki
     */
    public WikiPage() {
        this.wikiPageName = "";
        this.wikiPageContent = "";
        this.project = null;
    }

    /**
     * Constructeur paramétrique de la page wiki
     * @param wikiPageName Nom de la page wiki
     * @param wikiPageContent Contenu de la page wiki
     * @param project Projet de la page wiki
     */
    public WikiPage(String wikiPageName, String wikiPageContent, Project project) {
        this.wikiPageName = wikiPageName;
        this.wikiPageContent = wikiPageContent;
        this.project = project;
    }

    /**
     * Retourne l'identifiant de la page wiki
     * @return Identifiant de la page wiki
     */
    public int getWikiPageId() {
        return wikiPageId;
    }

    /**
     * Spécifie l'identifiant la page wiki
     * @param wikiPageId Identifiant de la page wiki
     */
    public void setWikiPageId(int wikiPageId) {
        this.wikiPageId = wikiPageId;
    }

    /**
     * Retourne le nom de la page wiki
     * @return Nom de la page wiki
     */
    public String getWikiPageName() {
        return wikiPageName;
    }

    /**
     * Spécifie le nom de la page wiki
     * @param wikiPageName Nom de la page wiki
     */
    public void setWikiPageName(String wikiPageName) {
        this.wikiPageName = wikiPageName;
    }

    /**
     * Retourne le contenu de la page wiki
     *
     * @return Contenu de la page wiki
     */
    public String getWikiPageContent() {
        return wikiPageContent;
    }

    /**
     * Spécifie le contenu de la page wiki
     * @param wikiPageContent Contenu de la page wiki
     */
    public void setWikiPageContent(String wikiPageContent) {
        this.wikiPageContent = wikiPageContent;
    }

    /**
     * Retourne le projet de la page wiki
     * @return Projet de la page wiki
     */
    public Project getProject() {
        return project;
    }

    /**
     * Spécifie le projet de la page wiki
     * @param project Projet de la page wiki
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
