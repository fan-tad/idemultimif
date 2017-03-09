package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.BelongsDao;
import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.List;
@Transactional
public class BelongsManager {
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;
    private BelongsDao belongsDao;
    private ManagerUtility managerUtility;

    public BelongsManager(EntityManager em) {
        //this.em = em;
        this.managerUtility = new ManagerUtility();
        this.belongsDao = new BelongsDao(em);
    }

    public Belongs getBelongs(int projectId, int userId){
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Belongs belongs = belongsDao.getBelongsByUserAndProject(projectId,userId);

        transaction.commit();
        return belongs;
    }

    public List<Belongs> getBelongsUsers(int projectId){
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        List<Belongs> users = belongsDao.getBelongsUsersByProject(projectId);

        transaction.commit();
        return users;
    }

    public Belongs addUserToProject(Project project, User user, int userRight){
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Belongs belongs = belongsDao.createBelongs(userRight,project, user);

        project.addBelongs(belongs);
        user.addBelongs(belongs);

        transaction.commit();
        return belongs;
    }

    public Belongs updateUserRight(int projectId, int userId, int userRight){
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Belongs belongs = belongsDao.getBelongsByUserAndProject(projectId, userId);
        if(belongs != null)
            belongs = belongsDao.updateUserRight(belongs, userRight);

        transaction.commit();
        return belongs;
    }
}
