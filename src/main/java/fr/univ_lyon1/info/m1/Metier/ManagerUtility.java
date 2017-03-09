package fr.univ_lyon1.info.m1.Metier;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Created by fanuel on 24/11/16.
 */
public class ManagerUtility {

    public EntityTransaction getTransaction(EntityManager em){
        int retry = 0;
        EntityTransaction transaction = em.getTransaction();
        while (transaction.isActive()){
            if(retry > 5) break;
            try {
                Thread.sleep(100+(int)Math.random()*100);
            } catch (Exception e){
                System.out.println(e);
            }
            retry++;
        }
        return transaction;
    }
}
