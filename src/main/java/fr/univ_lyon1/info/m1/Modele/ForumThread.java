package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name="ForumThread.findById", query="SELECT fT FROM ForumThread fT WHERE fT.forumThreadId = :forumThreadId"),
        @NamedQuery(name="ForumThread.findAllByProject", query="SELECT fT FROM ForumThread fT WHERE fT.project = :project"),
})

public class ForumThread {

    /**
     * Identifiant du sujet
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forumThreadId")
    private int forumThreadId;

    /**
     * Nom du sujet
     */
    @Column(name = "forumThreadName")
    private String forumThreadName;

    /**
     * Projet du sujet
     */
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    /**
     * Utilisateur du sujet
     */
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    /**
     * Messages du sujet
     */
    @OneToMany(mappedBy = "forumThread", fetch = FetchType.LAZY)
    private List<ForumPost> forumPostsCollection = new ArrayList<>();

    /**
     * Constructeur par défaut du sujet
     */
    public ForumThread() {
        this.forumThreadName = "";
        this.project = null;
        this.user = null;
    }

    /**
     * Constructeur paramétrique du sujet
     * @param forumThreadName Nom du sujet
     * @param project Projet du sujet
     * @param user Utilisateur propriétaire du sujet
     */
    public ForumThread(String forumThreadName, Project project, User user) {
        this.forumThreadName = forumThreadName;
        this.project = project;
        this.user = user;
    }

    /**
     * Retourne l'identifiant du sujet
     *
     * @return Identifiant du sujet
     */
    public int getForumThreadId() {
        return forumThreadId;
    }

    /**
     * Spécifie l'identifiant du projet
     * @param forumThreadId Identifiant du projet
     */
    public void setForumThreadId(int forumThreadId) {
        this.forumThreadId = forumThreadId;
    }

    /**
     * Retourne le nom du sujet
     * @return Nom du sujet
     */
    public String getForumThreadName() {
        return forumThreadName;
    }

    /**
     * Spécifie le nom du sujet
     * @param forumThreadName Nom du sujet
     */
    public void setForumThreadName(String forumThreadName) {
        this.forumThreadName = forumThreadName;
    }

    /**
     * Retourne le projet du sujet
     * @return Projet du sujet
     */
    public Project getProject() {
        return project;
    }

    /**
     * Spécifie le sujet du projet
     * @param project Sujet du projet
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Retourne l'utilisateur du sujet
     * @return Utilisateur du sujet
     */
    public User getUser() {
        return user;
    }

    /**
     * Spécifie l'utilisateur du projet
     * @param user Utilisateur du projet
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retourne les messages du sujet
     * @return Messages du sujet
     */
    public List<ForumPost> getForumPostsCollection() {
        return forumPostsCollection;
    }

    /**
     * Spécifie les messages du sujet
     * @param forumPostsCollection Messages du sujet
     */
    public void setForumPostsCollection(List<ForumPost> forumPostsCollection) {
        this.forumPostsCollection = forumPostsCollection;
    }


}
