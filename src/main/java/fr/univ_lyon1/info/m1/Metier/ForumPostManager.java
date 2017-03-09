package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.ForumPost;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Transactional
public class ForumPostManager {
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;
    private ForumPostDao forumPostDao;
    private ManagerUtility managerUtility;

    public ForumPostManager(EntityManager em) {
        //this.em = em;
        this.managerUtility = new ManagerUtility();
        this.forumPostDao = new ForumPostDao(em);
    }



    public ForumPost newForumPost(Date forumPostCreateDate, Date forumPostModifyDate, String forumPostContent, ForumThread forumThread, User user) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        ForumPost forumPost = forumPostDao.createForumPost(forumPostCreateDate, forumPostModifyDate, forumPostContent, forumThread, user);
        forumThread.getForumPostsCollection().add(forumPost);
        user.getForumPostsCollection().add(forumPost);

        transaction.commit();
        return forumPost;
    }
}
