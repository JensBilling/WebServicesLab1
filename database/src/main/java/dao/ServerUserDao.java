package dao;

import serverusers.ServerUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ServerUserDao {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");

    public void addNewUser(String username, String password) {
        EntityManager em = emf.createEntityManager();

        ServerUser su = new ServerUser(username, password);

        em.getTransaction().begin();
        em.persist(su);
        em.getTransaction().commit();
        em.close();

        System.out.println(username + " added to database");


    }
}
