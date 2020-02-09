package persistance;

import models.Day;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import ui.MainPageController;
import ui.mainController;

import java.io.File;
import java.io.IOException;

public class PersistanceWithJackson {

    public static void toJsonDay(Day day){
        ObjectMapper mapper = new ObjectMapper();

        try {
            File json = new File("day.json");
            mapper.writeValue(json, day);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Day toJavaDate() {
        ObjectMapper mapper = new ObjectMapper();
        Day day = new Day();
        try {
            File json = new File("day.json");
            day = mapper.readValue(json, Day.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static void toJsonController(MainPageController controller){
        ObjectMapper mapper = new ObjectMapper();

        try {
            File json = new File("controller.json");
            mapper.writeValue(json, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MainPageController toJavaController() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File json = new File("controller.json");
            return mapper.readValue(json, MainPageController.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
