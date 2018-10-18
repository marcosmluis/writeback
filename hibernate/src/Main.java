import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Main {

    public static void main(String[] args) {


        Configuration con= new Configuration();
        con.configure();
        Properties prop=con.getProperties();
        ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(con.getProperties()).build();

    }
}
