package org.example;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AppTest {
    private App app;

    @Before
    public void setup() {
        this.app = new App();
    }

    @Test
    public void testSortConferencesByAveragePointsPerGame() {
        List<String> sorted = app.sortConferencesByAveragePointsPerGame();

        Assert.assertEquals(sorted, Arrays.asList(
                "SEC",
                "ACC",
                "Big Ten",
                "Pac-12",
                "Big 12"
        ));
    }

    @Test
    public void testGetTeamOrTeamsWithMostPointsThroughUprights() {
        List<String> teamNames = app.getTeamOrTeamsWithMostPointsThroughUprights();

        Assert.assertEquals(Collections.singletonList("LSU"), teamNames);
    }

    @Test
    public void testGetTeamsPlayingFewerThanSevenGames() {
        List<String> teamNames = app.getTeamsPlayingFewerThanSevenGames();
        String colorado = teamNames.stream().filter(t -> t.equals("Colorado")).findFirst().orElse(null);

        Assert.assertEquals(40, teamNames.size());
        Assert.assertEquals("Colorado", colorado);
    }

    @Test
    public void testGetConferenceWithFewestGamesPlayed() {
        List<String> teamNames = app.getConferenceWithFewestGamesPlayed();

        Assert.assertEquals(Collections.singletonList("Big 12"), teamNames);
    }

    @Test
    public void testGetTeamWithHighestTouchdownToFieldGoalRatio() {
        String teamName = app.getTeamWithHighestTouchdownToFieldGoalRatio();

        Assert.assertEquals("Georgia Tech", teamName);
    }

    @Test
    public void testGetConferenceWithHighestTouchdownToFieldGoalRatio() {
        String conferenceName = app.getConferenceWithHighestTouchdownToFieldGoalRatio();

        Assert.assertEquals("Pac-12", conferenceName);
    }

    @Test
    public void testGetConferenceWithFewestTeams() {
        String conferenceName = app.getConferenceWithFewestTeams();

        Assert.assertEquals("Big 12", conferenceName);
    }

    @Test
    public void testGetNicknamesOfTeamsWithAtLeastOneSafetyOrTwoPointConversion() {
        List<String> nickNames = app.getNicknamesOfTeamsWithAtLeastOneSafetyOrTwoPointConversion();

        Assert.assertEquals(nickNames, Arrays.asList(
                "Crimson Tide",
                "Wildcats",
                "Bears",
                "Eagles",
                "Gators",
                "Seminoles",
                "Hoosiers",
                "Hawkeyes",
                "Cyclones",
                "Jayhawks",
                "Wildcats",
                "Wolverines",
                "Spartans",
                "Golden Gophers",
                "Tigers",
                "Wolfpack",
                "Cornhuskers",
                "Wildcats",
                "Ducks",
                "Nittany Lions",
                "Boilermakers",
                "Gamecocks",
                "Cardinal",
                "Volunteers",
                "Longhorns",
                "Trojans",
                "Hokies"
        ));

    }
}
