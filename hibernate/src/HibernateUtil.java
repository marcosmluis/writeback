import org.hibernate.*;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.cfg.Configuration;


import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HibernateUtil {

    private static HibernateUtil instance;
    private Configuration configuration;
    private SessionFactory sessionFactory;
    private Session session;
    private StandardServiceRegistry registry;
    private  Metadata metadata;


    public synchronized static HibernateUtil getInstance() {
        if (instance == null) {
            instance = new HibernateUtil();
        }
        return instance;
    }

    private synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            //sessionFactory = new Configuration().configure().buildSessionFactory();
            //sessionFactory = getConfiguration().buildSessionFactory();
            // Create registry builder
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            // Hibernate settings equivalent to hibernate.cfg.xml's properties
            Map<String, String> settings = new HashMap<>();
            settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/custom_fields_test");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");

            // Apply settings

            registryBuilder.applySettings(settings);

            // Create registry
            registry = registryBuilder.build();

            // Create MetadataSources
            MetadataSources sources = new MetadataSources(registry);
            //sources.addFile("Contact.hbm.xml");
            // Create Metadata
            metadata = sources.getMetadataBuilder().build();


            // Create SessionFactory
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        }
        return sessionFactory;
    }

    public synchronized Session getCurrentSession() {
        if (session == null) {
            session = new Configuration().configure("resources/hibernate.cfg.xml").buildSessionFactory().openSession();

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
            registry = registryBuilder.configure("resources/hibernate.cfg.xml").build();
            MetadataSources sources = new MetadataSources(registry);
            //sources.addFile("Contact.hbm.xml");
            // Create Metadata
            metadata = sources.getMetadataBuilder().build();
            //session = getSessionFactory().openSession();
            session.setFlushMode(FlushMode.COMMIT);
            System.out.println("session opened.");
        }
        return session;
    }

    private synchronized Configuration getConfiguration() {
        if (configuration == null) {
            System.out.print("configuring Hibernate ... ");
            InputStream in = HibernateUtil.class.getResourceAsStream("resources/hibernate.cfg.xml");
            try {
                configuration = new Configuration().addInputStream(in).configure();
                configuration.addClass(Contact.class);
                System.out.println("ok");
            } catch (HibernateException e) {
                System.out.println("failure");
                e.printStackTrace();
            }
        }
        return configuration;
    }
    public void reset() {
        Session session = getCurrentSession();
        if (session != null) {
            session.flush();
            if (session.isOpen()) {
                System.out.print("closing session ... ");
                session.close();
                System.out.println("ok");
            }
        }
        SessionFactory sf = getSessionFactory();
        if (sf != null) {
            System.out.print("closing session factory ... ");
            sf.close();
            System.out.println("ok");
        }
        this.configuration = null;
        this.sessionFactory = null;
        this.session = null;
    }

    public PersistentClass getClassMapping(Class entityClass){
        //StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        //metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();

        Collection<PersistentClass> entityBindings = metadata.getEntityBindings();
        Iterator<PersistentClass> iterator = ((java.util.Collection) entityBindings).iterator();
        while (iterator.hasNext()) {

            PersistentClass persistentClass = iterator.next();
            if(persistentClass.getEntityName().equals(entityClass.getName())) return persistentClass;
            //do somthing
        }

        return null;


        //return getConfiguration().getClassMapping(entityClass.getName());
    }
}