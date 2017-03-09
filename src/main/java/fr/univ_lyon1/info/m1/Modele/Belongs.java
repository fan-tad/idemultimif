package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name="Belongs.findAll", query="SELECT b FROM Belongs b"),
        @NamedQuery(name="Belongs.findByUserAndProject", query="SELECT b FROM Belongs b WHERE b.userId.userId = :userId AND b.projectId.projectId = :projectId"),
        @NamedQuery(name="Belongs.findByProject", query="SELECT b FROM Belongs b WHERE b.projectId.projectId = :projectId"),
})

@IdClass(BelongsId.class)
public class Belongs {

    /**
     * Identifiant de l'utilisateur
     */
    @Id
    @ManyToOne
    @JoinColumn(name="userId")
    private User userId;

    /**
     * Identifiant du projet
     */
    @Id
    @ManyToOne
    @JoinColumn(name="projectId")
    private Project projectId;

    /**
     * Niveau de droit attribué à l'utilisateur dans le projet
     */
    @Column(name = "belongsRight")
    private int belongsRight;

    /**
     * Constructeur par défaut de "Belongs"
     */
    public Belongs() {
        this.userId = null;
        this.projectId = null;
        this.belongsRight = 0;
    }

    /**
     * Constructeur paramétrique de "Belongs"
     * @param user Utilisateur du projet
     * @param project Projet de l'utilisateur
     * @param belongsRight Niveau du droit attribué à l'utilisateur dans le projet
     */
    public Belongs(User user, Project project, int belongsRight) {
        this.userId = user;
        this.projectId = project;
        this.belongsRight = belongsRight;
    }

    /**
     * Retourne l'utilisateur
     *
     * @return Utilisateur du projet
     */
    public User getUser() {
        return userId;
    }

    /**
     * Spécifie l'utilisateur du projet
     * @param user Utilisateur du projet
     */
    public void setUser(User user) {
        this.userId = user;
    }

    /**
     * Retourne le projet de l'utilisateur
     * @return Projet de l'utilisateur
     */
    public Project getProject() {
        return projectId;
    }

    /**
     * Spécifie le projet de l'utilisateur
     * @param project Projet de l'utilisateur
     */
    public void setProject(Project project) {
        this.projectId = project;
    }

    /**
     * Retourne le niveau de droit attribué à l'utilisateur dans le projet
     * @return Niveau de droit attribué à l'utilisateur dans le projet
     */
    public int getBelongsRight() {
        return belongsRight;
    }

    /**
     * Spécifie le niveau de droit attribué à l'utilisateur dans le projet
     * @param belongsRight Niveau de droit attribué à l'utilisateur dans le projet
     */
    public void setBelongsRight(int belongsRight) {
        this.belongsRight = belongsRight;
    }
}
