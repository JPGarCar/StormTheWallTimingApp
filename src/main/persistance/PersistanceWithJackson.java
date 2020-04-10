package persistance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.Program;
import models.RunNumber;
import ui.TimingController;

import java.io.File;
import java.io.IOException;

public class PersistanceWithJackson {

    public static void toJsonProgram(Program program){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        try {
            File json = new File("program.json");
            mapper.writeValue(json, program);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Program toJavaProgram() {
        ObjectMapper mapper = new ObjectMapper();
        Program program = new Program();
        try {
            File json = new File("program.json");
            program = mapper.readValue(json, Program.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return program;
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

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(RunNumber.class, new RunNumberDes());
        mapper.registerModule(simpleModule);


        try {
            File json = new File("controller.json");
            return mapper.readValue(json, TimingController.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
