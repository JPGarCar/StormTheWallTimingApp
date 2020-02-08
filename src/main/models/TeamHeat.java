package models;

import java.util.Calendar;

public class TeamHeat {

    private Heat heat;
    private FinalTime finalTime;
    private Calendar endTime;

    public TeamHeat(Heat heat) {
        this.heat = heat;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
        finalTime = new FinalTime(heat.getStartTime(), endTime);
    }

    public FinalTime getFinalTime() {
        return finalTime;
    }

    // EFFECTS: return the heat´s number associated with the TeamHeat
    public int getHeatNumber() {
        return heat.getHeatNumber();
    }
}
