package org.example;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class AppTest {
    private App app;

    @Before
    public void setup() {
        this.app = new App();
    }

    @Test
    public void testSortConferencesByAveragePointsPerGame() throws IOException {
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
    public void testGetTeamsPlayingFewerThanSevenGames() throws IOException {
        List<String> teamNames = app.getTeamsPlayingFewerThanSevenGames();
        String colorado = teamNames.stream().filter(t -> t.equals("Colorado")).findFirst().orElse(null);

        Assert.assertEquals(40, teamNames.size());
        Assert.assertEquals("Colorado", colorado);
    }

    @Test
    public void testGetConferenceWithFewestGamesPlayed() throws IOException {
        List<String> teamNames = app.getConferenceWithFewestGamesPlayed();

        Assert.assertEquals(Collections.singletonList("Big 12"), teamNames);
    }

    @Test
    public void testGetTeamWithHighestTouchdownToFieldGoalRatio() throws IOException {
        String teamName = app.getTeamWithHighestTouchdownToFieldGoalRatio();

        Assert.assertEquals("Georgia Tech", teamName);
    }

    @Test
    public void testGetConferenceWithFewestTeams() {

    }

    @Test
    public void testGetNicknamesOfTeamsWithAtLeastOneSafetyOrTwoPointConversion() {

    }
}
