package ui.widgets;

import javafx.scene.layout.HBox;
import models.Run;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.UIAppLogic;

public abstract class CustomHBox extends HBox {

// VARIABLES //

    Run run;

// CONSTRUCTOR //

    CustomHBox(double spacing, Run run) {
        super(spacing);
        this.run = run;
    }

// FUNCTIONS //

    // EFFECTS: update the status of a specific team private does all the work, three public for different situations
    protected void updateStatus(String sitrep, UIAppLogic UIAppLogic) {
        run.setSitrep(Sitrep.valueOf(sitrep));
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    protected void endTeam(UIAppLogic controller) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {
        controller.endRun(run.getRunNumber());
    }


}
