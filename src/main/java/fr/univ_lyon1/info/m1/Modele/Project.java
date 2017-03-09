package fr.univ_lyon1.info.m1.Modele;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name="Project.findAll", query="SELECT p FROM Project p"),
        @NamedQuery(name="Project.findByName", query="SELECT p FROM Project p WHERE p.projectName = :projectName"),
        @NamedQuery(name="Project.findById", query="SELECT p FROM Project p WHERE p.projectId = :projectId"),
        @NamedQuery(name="Project.findIdByName", query="SELECT p.projectId FROM Project p WHERE p.projectName = :projectName"),
        @NamedQuery(name="Project.updateNameByName", query="UPDATE Project p SET p.projectName = :newProjectName WHERE p.projectName = :oldProjectName"),
})

public class Project {
    /**
     * Identifiant du projet
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectId")
    private int projectId;

    /**
     * Nom du projet
     */
    @Column(name = "projectName", unique = true)
    private String projectName;

    /**
     * Chemin du projet
     */
    @Column(name = "projectPath")
    private String projectPath;

    /**
     * Description du projet
     */
    @Column(name = "projectDescription")
    private String projectDescription;

    /**
     *
     */
    @OneToMany(mappedBy = "projectId", fetch=FetchType.LAZY)
    private List<Belongs> belongs = new ArrayList<>();

    /**
     * Todos associés au projet
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Todo> todosCollection = new ArrayList<>();

    /**
     * Tickets associés au projet
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Ticket> ticketsCollection = new ArrayList<>();

    /**
     * Sujets associés au projet
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ForumThread> forumThreadsCollection = new ArrayList<>();

    /**
     * Constructeur par défaut du projet
     */
    public Project() {
        this.projectName = "";
        this.projectPath = "";
        this.projectDescription = "";
    }

    /**
     * Constructeur paramétrique du projet
     * @param projectName Nom du projet
     * @param projectPath Chemin du projet
     * @param projectDescription Description du projet
     */
    public Project(String projectName, String projectPath, String projectDescription) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.projectDescription = projectDescription;
    }

    /**
     * Retourne l'identifiant du projet
     *
     * @return Identifiant du projet
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Spécifie l'identifiant du projet
     * @param projectId Identifiant du projet
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Retourne le nom du projet
     * @return Nom du projet
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Spécifie le nom du projet
     * @param projectName Nom du projet
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Retourne le chemin du projet
     * @return Chemin du projet
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * Spécifie le chemin du projet
     * @param projectPath Chemin du projet
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * Retourne la description du projet
     * @return Description du projet
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Spécifie la description du projet
     * @param projectDescription Description du projet
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * Retourne les "belongs" associés au projet
     * @return "Belongs" associés au projet
     */
    public List<Belongs> getBelongs() {
        return belongs;
    }

    /**
     * Spécifie les "belongs" associés au projet
     * @param belongs "Belongs" associés au projet
     */
    public void setBelongs(List<Belongs> belongs) {
        this.belongs = belongs;
    }

    /**
     * Retourne les todos du projets
     * @return Todos du projet
     */
    public List<Todo> getTodosCollection() {
        return todosCollection;
    }

    /**
     * Spécifie les todos du projet
     * @param todosCollection Fichiers du projet
     */
    public void setTodosCollection(List<Todo> todosCollection) {
        todosCollection = todosCollection;
    }

    /**
     * Retourne les tickets du projet
     * @return Tickets du projet
     */
    public List<Ticket> getTicketsCollection() {
        return ticketsCollection;
    }

    /**
     * Spécifie les tickets du projet
     * @param ticketsCollection Tickets du projet
     */
    public void setTicketsCollection(List<Ticket> ticketsCollection) {
        ticketsCollection = ticketsCollection;
    }

    /**
     * Retourne les sujets du projet
     * @return Sujets du projets
     */
    public List<ForumThread> getForumThreadsCollection() {
        return forumThreadsCollection;
    }

    /**
     * Spécifie les sujets du projet
     * @param forumThreadsCollection Sujets du projet
     */
    public void setForumThreadsCollection(List<ForumThread> forumThreadsCollection) {
        this.forumThreadsCollection = forumThreadsCollection;
    }

    /**
     * Ajoute un "belongs"
     * @param belongs "Belongs"
     */
    public void addBelongs(Belongs belongs) {
        this.belongs.add(belongs);
    }
}
