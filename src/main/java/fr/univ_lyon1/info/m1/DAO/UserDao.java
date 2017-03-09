package fr.univ_lyon1.info.m1.DAO;

import fr.univ_lyon1.info.m1.Modele.User;

import javax.persistence.*;
import java.util.List;

public class UserDao {

    private EntityManager em;

    public UserDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Récupère un utilisateur par son nom
     * @param name le nom de l'utilisateur
     * @return l'utilisateur
     */
    public User getUserByName(String name) {
        User u = null;
        Query query = em.createNamedQuery("User.findByName", User.class).setParameter("userName", name);
        try {
            u = (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return u;
    }

    /**
     * Récupère un utilisateur en fonction de son Id
     * @param id l'Id de l'utilisateur
     * @return l'utilisateur
     */
    public User getUserById(int id) {
        User u = null;
        Query query = em.createNamedQuery("User.findById", User.class).setParameter("userId", id);
        try {
            u = (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return u;
    }

    /**
     * récupère l'utilisateur grâce à son nom et son password
     * @param name le nom de l'utilisateur
     * @param password le mot de passe de l'utilisateur
     * @return l'utilisateur
     */
    public User getUserByNamePassword(String name, String password) {
        User u = null;
        Query query = em.createNamedQuery("User.findByNameAndPassword", User.class).setParameter("userName", name).setParameter("userPassword", password);
        try {
            u = (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }

        return u;
    }

    /**
     * Récupère tous les utilisateurs
     * @return la liste d'utilisateurs
     */
    public List<User> getAllUsers() {
        List<User> lu = null;
        Query query = em.createNamedQuery("User.findAll", User.class);

        try {
            lu = query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new ExceptionDao(e);
        }
        return lu;
    }

    /**
     * Crée un utilisateur
     * @param userName le nom de l'utilisateur
     * @param userPassword son password
     * @return l'utilisateur crée
     */
    public User createUser(String userName, String userPassword) {
        User u = getUserByName(userName);
        if (u == null) {
            u = new User(userName, userPassword);
            em.persist(u);
            return u;
        }
        else {
            return u;
        }
    }

    /**
     * Supprime un utilisateur en fonction de son nom
     * @param userName le nom de l'utilisateur à supprimer
     */
    public void removeUser(String userName) {
        User u = getUserByName(userName);
        if(u != null) {
            em.remove(u);
        }
    }
}
