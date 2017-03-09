package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name="Ticket.findById", query="SELECT t FROM Ticket t WHERE t.ticketId = :ticketId"),
        @NamedQuery(name="Ticket.findAllByProject", query="SELECT t FROM Ticket t WHERE t.project = :project ORDER BY t.ticketId DESC"),
})

public class Ticket {
    /**
     * Identifiant du ticket
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketId")
    private int ticketId;

    /**
     * Nom du ticket
     */
    @Column(name = "ticketName")
    private String ticketName;

    /**
     * Badge du ticket
     */
    @Column(name = "ticketBadge")
    private String ticketBadge;

    /**
     * Contenu du ticket
     */
    @Column(name = "ticketContent")
    private String ticketContent;

    /**
     * Projet du ticket
     */
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    /**
     * Utilisateur propriétaire du ticket
     */
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userOwner;

    /**
     * Utilisateur assigné au ticket
     */
    @ManyToOne
    @JoinColumn(name = "userId_User")
    private User userAssigned;

    /**
     * Constructeur par défaut du ticket
     */
    public Ticket() {
        this.ticketName = "";
        this.ticketBadge = "";
        this.ticketContent = "";
        this.project = null;
        this.userOwner = null;
        this.userAssigned = null;
    }

    /**
     * Constructeur paramétrique du ticket (avec utilisateur assigné)
     * @param ticketName Nom du ticket
     * @param ticketBadge Badge du ticket
     * @param ticketContent Contenu du ticket
     * @param project Projet du ticket
     * @param userOwner Utilisateur propriétaire du ticket
     * @param userAssigned Utilisateur assigné au ticket
     */
    public Ticket(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner, User userAssigned) {
        this.ticketName = ticketName;
        this.ticketBadge = ticketBadge;
        this.ticketContent = ticketContent;
        this.project = project;
        this.userOwner = userOwner;
        this.userAssigned = userAssigned;
    }

    /**
     * Constructeur paramétrique du ticket (sans utilisateur assigné)
     * @param ticketName Nom du ticket
     * @param ticketBadge Badge du ticket
     * @param ticketContent Contenu du ticket
     * @param project Projet du ticket
     * @param userOwner Utilisateur propriétaire du ticket
     */
    public Ticket(String ticketName, String ticketBadge, String ticketContent, Project project, User userOwner) {
        this.ticketName = ticketName;
        this.ticketBadge = ticketBadge;
        this.ticketContent = ticketContent;
        this.project = project;
        this.userOwner = userOwner;
        this.userAssigned = null;
    }

    /**
     * Retourne l'identifiant du ticket
     * @return Identifiant du ticket
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * Spécifie l'identifiant du ticket
     * @param ticketId Identifiant du ticket
     */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * Retourne le nom du ticket
     * @return Nom du ticket
     */
    public String getTicketName() {
        return ticketName;
    }

    /**
     * Spécifie le nom du ticket
     * @param ticketName Nom du ticket
     */
    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    /**
     * Retourne le badge du ticket
     * @return Badge du ticket
     */
    public String getTicketBadge() {
        return ticketBadge;
    }

    /**
     * Spécifie le badge du ticket
     * @param ticketBadge Badge du ticket
     */
    public void setTicketBadge(String ticketBadge) {
        this.ticketBadge = ticketBadge;
    }

    /**
     * Retourne le contenu du ticket
     * @return Contenu du ticket
     */
    public String getTicketContent() {
        return ticketContent;
    }

    /**
     * Spécifie le contenu du ticket
     * @param ticketContent Contenu du ticket
     */
    public void setTicketContent(String ticketContent) {
        this.ticketContent = ticketContent;
    }

    /**
     * Retourne le projet du ticket
     * @return Projet du ticket
     */
    public Project getProject() {
        return project;
    }

    /**
     * Spécifie le projet du ticket
     * @param project Projet du ticket
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Retourne l'utilisateur propriétaire du ticket
     * @return Utilisateur propriétaire du ticket
     */
    public User getUserOwner() {
        return userOwner;
    }

    /**
     * Spécifie l'utilisateur propriétaire du ticket
     * @param userOwner Utilisateur propriétaire du ticket
     */
    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }

    /**
     * Retourne l'utilsateur assigné au ticket
     *
     * @return Utilisateur assigné au ticket
     */
    public User getUserAssigned() {
        return userAssigned;
    }

    /**
     * Spécifie l'utilisateur assigné au ticket
     * @param userAssigned Utilisateur assigné au ticket
     */
    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }
}
