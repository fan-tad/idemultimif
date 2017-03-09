package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
        @NamedQuery(name="User.findByName", query="SELECT u FROM User u WHERE u.userName = :userName"),
        @NamedQuery(name="User.findById", query="SELECT u FROM User u WHERE u.userId = :userId"),
        @NamedQuery(name="User.findByNameAndPassword", query="SELECT u FROM User u WHERE u.userName = :userName AND u.userPassword = :userPassword"),
})
public class User {

    /**
     * Identifiant de l'utilisateur
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    /**
     * Nom de l'utilisateur
     */
    @Column(name = "userName", unique = true)
    private String userName;

    /**
     * Mot de passe de l'utilisateur
     */
    @Column(name = "userPassword")
    private String userPassword;

    @OneToMany(mappedBy = "userId")
    private List<Belongs> belongs = new ArrayList<>();

    /**
     * Tickets associés à l'utilisateur (en tant qu'utilisateur propriétaire du ticket)
     */
    @OneToMany(mappedBy = "userOwner", fetch = FetchType.LAZY)
    private List<Ticket> TicketOwnerCollection = new ArrayList<>();

    /**
     * Tickets associés à l'utilisateur (en tant qu'utilisateur assigné au ticket)
     */
    @OneToMany(mappedBy = "userAssigned", fetch = FetchType.LAZY)
    private List<Ticket> TicketAssignedCollection = new ArrayList<>();

    /**
     * Sujets du forum associés à l'utilisateur
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ForumThread> forumThreadsCollection = new ArrayList<>();

    /**
     * Messages du forum associés à l'utilisateur
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ForumPost> forumPostsCollection = new ArrayList<>();

    /**
     * Constructeur par défaut de l'utilisateur
     */
    public User() {
        this.userName = "";
        this.userPassword = "";
    }

    /**
     * Constructeur paramétrique de l'utilisateur
     * @param userName Nom de l'utilisateur
     * @param userPassword Mot de passe de l'utilisateur
     */
    public User(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * Retourne l'identifiant de l'utilisateur
     * @return Identifiant de l'utilisateur
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Spécifie l'identifiant de l'utilisateur
     * @param userId Identifiant de l'utilisateur
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Retourne le nom de l'utilisateur
     * @return Nom de l'utilisateur
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Spécifie le nom de l'utilisateur
     * @param userName Nom de l'utilisateur
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Retourne le mot de passe de l'utilisateur
     * @return Mot de passe de l'utilisateur
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Spécifie le mot de passe de l'utilisateur
     * @param userPassword Mot de passe de l'utilisateur
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Retourne les "belongs" associés à l'utilisateur
     * @return "Belongs" associés à l'utilisateur
     */
    public List<Belongs> getBelongs() {
        return belongs;
    }

    /**
     * Spécifie les "belongs" associés à l'utilisateur
     * @param belongs "Belongs" associés à l'utilisateur
     */
    public void setBelongs(List<Belongs> belongs) {
        this.belongs = belongs;
    }

    /**
     * Retourne les tickets associés à l'utilisateur (en tant qu'utilisateur propriétaire du ticket)
     * @return Tickets associés à l'utilisateur (en tant qu'utilisateur propriétaire du ticket)
     */
    public List<Ticket> getTicketOwnerCollection() {
        return TicketOwnerCollection;
    }

    /**
     * Spécifie les tickets associés à l'utilisateur (en tant qu'utilisateur propriétaire du ticket)
     * @param ticketOwnerCollection Tickets associés à l'utilisateur (en tant qu'utilisateur propriétaire du ticket)
     */
    public void setTicketOwnerCollection(List<Ticket> ticketOwnerCollection) {
        TicketOwnerCollection = ticketOwnerCollection;
    }

    /**
     * Retourne les tickets associés à l'utilisateur (en tant qu'utilisateur assigné au ticket)
     * @return Tickets associés à l'utilisateur (en tant qu'utilisateur assigné au ticket)
     */
    public List<Ticket> getTicketAssignedCollection() {
        return TicketAssignedCollection;
    }

    /**
     * Spécifie les tickets associés à l'utilisateur (en tant qu'utilisateur assigné au ticket)
     * @param ticketAssignedCollection Tickets associés à l'utilisateur (en tant qu'utilisateur assigné au ticket)
     */
    public void setTicketAssignedCollection(List<Ticket> ticketAssignedCollection) {
        TicketAssignedCollection = ticketAssignedCollection;
    }

    /**
     * Retourne les sujets du forum associés à l'utilisateur
     * @return Sujets du forum associés à l'utilisateur
     */
    public List<ForumThread> getForumThreadsCollection() {
        return forumThreadsCollection;
    }

    /**
     * Spécifie les sujets du forum associés à l'utilisateur
     * @param forumThreadsCollection Sujets du forum associés à l'utilisateur
     */
    public void setForumThreadsCollection(List<ForumThread> forumThreadsCollection) {
        this.forumThreadsCollection = forumThreadsCollection;
    }

    /**
     * Retourne les messages du forum associés à l'utilisateur
     * @return Messages du forum associés à l'utilisateur
     */
    public List<ForumPost> getForumPostsCollection() {
        return forumPostsCollection;
    }

    /**
     * Spécifie les messages du forum associés à l'utilisateur
     * @param forumPostsCollection Messages du forum associés à l'utilisateur
     */
    public void setForumPostsCollection(List<ForumPost> forumPostsCollection) {
        this.forumPostsCollection = forumPostsCollection;
    }

    /**
     * Ajoute un "Belongs"
     * @param belongs "Belongs"
     */
    public void addBelongs(Belongs belongs) {
        this.belongs.add(belongs);
    }
}
