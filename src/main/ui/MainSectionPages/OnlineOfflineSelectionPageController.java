package ui.MainSectionPages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.security.auth.login.Configuration;
import java.io.IOException;

public class OnlineOfflineSelectionPageController {

    /**
     * Path where data can be saved in this computer. As a string.
     */
    String pathToUse;

    public OnlineOfflineSelectionPageController(String pathToUse) {
        this.pathToUse = pathToUse;
    }

    @FXML
    private Button goOnlineButton;


    /**
     * There is no change to the code so proceed as expected and use the system as it was intended offline. Pass on
     * the path where data can be saved in this computer.
     */
    @FXML
    void GoOfflineActionButton() {
        FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/RaceChooserPage.fxml"));
        root.setControllerFactory(c -> new RaceChooserPageController(pathToUse));

        try {
            goOnlineButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Button will need to connect with the database, import all the data from the cloud and start RunPolling.
     */
    @FXML
    void GoOnlineActionButton() {

        // Connect to database
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(ssr).getMetadataBuilder().build();

        // SessionFactory will remain the same one throughout the entire program
        SessionFactory factory = metadata.getSessionFactoryBuilder().build();
        // Session can be opened and closed as needed
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        // DO WHAT NEEDS TO BE DONE

        transaction.commit();
        session.close();

        factory.close();

        // Import Data

        // Start RunningPoll


    }

//    FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/RaceChooserPage.fxml"));
//            root.setControllerFactory(c -> new RaceChooserPageController(pathToUse));
//
//            try {
//        confirmAndMoveOnButton.getScene().setRoot(root.load());
//    } catch (
//    IOException e) {
//        e.printStackTrace();
//    }


}
