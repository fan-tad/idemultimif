package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name="ForumPost.findById", query="SELECT fP FROM ForumPost fP WHERE fP.forumPostId = :forumPostId"),
        @NamedQuery(name="ForumPost.findAllByForumThread", query="SELECT fP FROM ForumPost fP WHERE fP.forumThread = :forumThread"),
})

public class ForumPost {

    /**
     * Identifiant du message
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forumPostId")
    private int forumPostId;

    /**
     * Date de création du message
     */
    @Column(name = "forumPostCreateDate")
    private Date forumPostCreateDate;

    /**
     * Date de modification du message
     */
    @Column(name = "forumPostModifyDate")
    private Date forumPostModifyDate;

    /**
     * Contenu du message
     */
    @Column(name = "forumPostContent")
    private String forumPostContent;

    /**
     * Sujet du message
     */
    @ManyToOne
    @JoinColumn(name = "forumThreadId")
    private ForumThread forumThread;

    /**
     * Utilisateur du message
     */
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    /**
     * Constructeur par défaut du message
     */
    public ForumPost() {
        this.forumPostCreateDate = null;
        this.forumPostModifyDate = null;
        this.forumPostContent = "";
        this.forumThread = null;
        this.user = null;
    }

    /**
     * Constructeur paramétrique du message
     * @param forumPostCreateDate Date de création du message
     * @param forumPostModifyDate Date de modification du message
     * @param forumPostContent Contenu du message
     * @param forumThread Sujet du message
     * @param user Utilisateur du message
     */
    public ForumPost(Date forumPostCreateDate, Date forumPostModifyDate, String forumPostContent, ForumThread forumThread, User user) {
        this.forumPostCreateDate = forumPostCreateDate;
        this.forumPostModifyDate = forumPostModifyDate;
        this.forumPostContent = forumPostContent;
        this.forumThread = forumThread;
        this.user = user;
    }

    /**
     * Retourne l'identifiant du message
     *
     * @return Identifiant du message
     */
    public int getForumPostId() {
        return forumPostId;
    }

    /**
     * Spécifie l'identifiant du message
     * @param forumPostId Identifiant du message
     */
    public void setForumPostId(int forumPostId) {
        this.forumPostId = forumPostId;
    }

    /**
     * Retourne la date de création du message
     * @return Date de création du message
     */
    public Date getForumPostCreateDate() {
        return forumPostCreateDate;
    }

    /**
     * Spécifie la date de création du message
     * @param forumPostCreateDate Date de création du message
     */
    public void setForumPostCreateDate(Date forumPostCreateDate) {
        this.forumPostCreateDate = forumPostCreateDate;
    }

    /**
     * Retourne la date de modification du message
     * @return Date de modification du message
     */
    public Date getForumPostModifyDate() {
        return forumPostModifyDate;
    }

    /**
     * Spécifie la date de modification du message
     * @param forumPostModifyDate Date de modification du message
     */
    public void setForumPostModifyDate(Date forumPostModifyDate) {
        this.forumPostModifyDate = forumPostModifyDate;
    }

    /**
     * Retourne le contenu du message
     * @return Contenu du message
     */
    public String getForumPostContent() {
        return forumPostContent;
    }

    /**
     * Spécifie le contenu du message
     * @param forumPostContent Contenu du message
     */
    public void setForumPostContent(String forumPostContent) {
        this.forumPostContent = forumPostContent;
    }

    /**
     * Retourne le sujet du message
     * @return Sujet du message
     */
    public ForumThread getForumThread() {
        return forumThread;
    }

    /**
     * Spécifie le sujet du message
     * @param forumThread Sujet du message
     */
    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    /**
     * Retourne l'utilisateur du message
     * @return Utilisateur du message
     */
    public User getUser() {
        return user;
    }

    /**
     * Spécifie l'utilisateur du message
     * @param user Utilisateur du message
     */
    public void setUser(User user) {
        this.user = user;
    }
}
