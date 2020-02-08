package models;

import java.util.Calendar;

public class TeamHeat {

    private Heat heat;
    private FinalTime finalTime;
    private Calendar endTime;

    public TeamHeat(Heat heat) {
        this.heat = heat;
    }

    public void addEndTime(Calendar endTime) {
        this.endTime = endTime;
        finalTime = new FinalTime(heat.getStartTime(), this.endTime);
    }

    public FinalTime getFinalTime() {
        return finalTime;
    }
}
