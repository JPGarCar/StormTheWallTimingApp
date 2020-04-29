package persistance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.beans.property.StringProperty;
import models.Program;
import models.RunNumber;
import ui.UIAppLogic;

import java.io.File;
import java.io.IOException;

public class PersistenceWithJackson {

    private String SAVEFILEPATH;

    public void setSAVEFILEPATH(String SAVEFILEPATH) {
        this.SAVEFILEPATH = SAVEFILEPATH;
    }

    public static void toJsonProgram(Program program, String path){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        try {
            File json = new File(path + "/program.json");
            mapper.writeValue(json, program);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Program toJavaProgram(String path) {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(RunNumber.class, new RunNumberDes());
        mapper.registerModule(simpleModule);

        Program program = new Program();
        try {
            File json = new File(path + "/program.json");
            program = mapper.readValue(json, Program.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return program;
    }

    public static void toJsonController(UIAppLogic controller, String path){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        try {
            File json = new File(path + "/controller.json");
            mapper.writeValue(json, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UIAppLogic toJavaController(String path) {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(RunNumber.class, new RunNumberDes());
        mapper.registerModule(simpleModule);


        try {
            File json = new File(path + "/controller.json");
            return mapper.readValue(json, UIAppLogic.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
