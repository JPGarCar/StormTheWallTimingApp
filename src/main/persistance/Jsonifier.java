package persistance;

import models.*;

import org.json.*;

public class Jsonifier {

    public static JSONObject finalTimeToJson(FinalTime finalTime) {
        JSONObject finalTimeJson = new JSONObject();
        try {
            finalTimeJson.put("endTime", finalTime.getStopTime());
            finalTimeJson.put("startTime", finalTime.getStartTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalTimeJson;
    }

    public static JSONObject teamHeatToJson(TeamHeat teamHeat) {
        JSONObject jsonTeamHeat = new JSONObject();
        try {
            jsonTeamHeat.put("finalTime", teamHeat.getFinalTime());
            //jsonTeamHeat.put("heat", teamHeat.getHeatFromTeam());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonTeamHeat;
    }

    public static JSONObject dayToJson(Day day) {
        JSONObject jsonDay = new JSONObject();
        try {
            jsonDay.put("day", day.getDayToRun());
            jsonDay.put("dayNumber", day.getDayNumber());
            jsonDay.put("heats", day.getHeats());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDay;
    }

    public static JSONObject teamToJson(Team team) {
        JSONObject jsonTeam = new JSONObject();
        try {
            jsonTeam.put("teamType",team.getTeamType());
            jsonTeam.put("sitrep", team.getSitRep());
            jsonTeam.put("leagueType", team.getTeamLeague());
            jsonTeam.put("notes", team.getNotes());
            jsonTeam.put("teamNumber", team.getTeamNumber());
            jsonTeam.put("teamName", team.getTeamName());
            jsonTeam.put("heats", team.getHeats());
            jsonTeam.put("doneHeats", team.getDoneHeats());
            jsonTeam.put("remainingHeats", team.getRemainingHeats());
            jsonTeam.put("currentHeat", team.getCurrentHeatID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonTeam;
    }

    public static JSONObject heatToJson(Heat heat) {
        JSONObject jsonHeat = new JSONObject();
        try {
            jsonHeat.put("timeToStart",heat.getTimeToStart());
            jsonHeat.put("leagueType", heat.getLeagueType());
            jsonHeat.put("teamType", heat.getTeamType());
            jsonHeat.put("heatNumber", heat.getHeatNumber());
            jsonHeat.put("hasStarted", heat.isHasStarted());
            jsonHeat.put("startTime", heat.getStartTime());
            jsonHeat.put("teams", heat.getTeams());
            jsonHeat.put("day", heat.getDayToRace());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonHeat;
    }

}
