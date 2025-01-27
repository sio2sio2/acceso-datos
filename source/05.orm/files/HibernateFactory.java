package edu.acceso.test_hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateFactory implements AutoCloseable {

    private static HibernateFactory instance;
    private final SessionFactory sf;

    private HibernateFactory(Configuration config) {
        if(config == null) throw new IllegalArgumentException("La configuración no puede ser nula");
        sf = config.buildSessionFactory();
    }

    public static HibernateFactory getInstance(String filename) {
        if(instance == null) {
            synchronized (HibernateFactory.class) {
                if(instance == null) getInstance(new Configuration().configure(filename));
            }
        }
        return instance;
    }

    public static HibernateFactory getInstance(Configuration config) {
        if(instance == null) {
            synchronized (HibernateFactory.class) {
                if(instance == null) instance = new HibernateFactory(config);
            }
        }
        return instance;
    }

    public static HibernateFactory getInstance() {
        if(instance == null) throw new IllegalArgumentException("No hay fábrica creada, por lo que debe proporcionar una configuración");
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sf;
    }

    public Session openSession() {
        return sf.openSession();
    }

    @Override
    public void close() throws Exception {
        sf.close();
        instance = null;
    }
}
