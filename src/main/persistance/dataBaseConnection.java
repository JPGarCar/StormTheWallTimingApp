package persistance;

import models.*;
import models.exceptions.AddHeatException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Calendar;

public class dataBaseConnection {

    public static void main( String[] args ) throws AddHeatException {
        dataBaseConnection dataBaseConnection = new dataBaseConnection();
    }


    public dataBaseConnection() throws AddHeatException {
        Day day = new Day();

        Heat heat = new Heat(Calendar.getInstance(), "Just For Fun", 4562, day, 1);

        Configuration configuration = new Configuration().configure().addAnnotatedClass(Day.class);
        configuration.addAnnotatedClass(Heat.class);
        configuration.addAnnotatedClass(Team.class);
        configuration.addAnnotatedClass(Run.class);
        configuration.addAnnotatedClass(Program.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        session.saveOrUpdate(heat);

        transaction.commit();



    }

}
