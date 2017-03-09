package fr.univ_lyon1.info.m1.Itf;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Created by fanuel on 08/12/16.
 */
public class CreateEntity {
    public static EntityManager em = Persistence.createEntityManagerFactory("pu-sharkode").createEntityManager();


    public static EntityManager getEm() {
        return em;
    }

    public static void setEm(EntityManager em) {
        CreateEntity.em = em;
    }
}
