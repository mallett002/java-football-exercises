package org.example;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
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
}
