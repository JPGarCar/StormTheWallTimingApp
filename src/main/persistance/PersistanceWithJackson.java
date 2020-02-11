package persistance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jdk.nashorn.internal.runtime.Timing;
import models.Day;
import ui.TimingController;

import java.io.File;
import java.io.IOException;

public class PersistanceWithJackson {

    public static void toJsonDay(Day day){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

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

    public static void toJsonController(TimingController controller){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        try {
            File json = new File("controller.json");
            mapper.writeValue(json, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TimingController toJavaController() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File json = new File("controller.json");
            return mapper.readValue(json, TimingController.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
