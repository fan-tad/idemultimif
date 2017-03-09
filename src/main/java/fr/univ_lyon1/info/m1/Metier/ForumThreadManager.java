package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.ForumThread;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

@Transactional
public class ForumThreadManager {
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;
    private ForumThreadDao forumThreadDao;
    private ManagerUtility managerUtility;

    public ForumThreadManager(EntityManager em) {
        //this.em = em;
        managerUtility = new ManagerUtility();
        this.forumThreadDao = new ForumThreadDao(em);
    }

    public ForumThread newForumThread(String forumThreadName, Project project, User user) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        ForumThread forumThread = forumThreadDao.createForumThread(forumThreadName, project, user);
        project.getForumThreadsCollection().add(forumThread);
        user.getForumThreadsCollection().add(forumThread);

        transaction.commit();
        return forumThread;
    }
}
