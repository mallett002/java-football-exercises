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
        long start = System.currentTimeMillis();
        List<String> result = app.getTeamOrTeamsWithMostPointsThroughUprights();

        // Time to beat: 38ms
        System.out.println("Total time: " + (System.currentTimeMillis() - start));

        Assert.assertEquals(result, Collections.singletonList("LSU"));
    }

    @Test
    public void testGetTeamsPlayingFewerThanSevenGames() {

    }

    @Test
    public void testGetTeamWithHighestTouchdownToFieldGoalRatio() {

    }

    @Test
    public void testGetConferenceWithFewestTeams() {

    }

    @Test
    public void testGetNicknamesOfTeamsWithAtLeastOneSafetyOrTwoPointConversion() {

    }
}
