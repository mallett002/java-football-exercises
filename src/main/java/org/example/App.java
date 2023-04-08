package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class App {
    ObjectMapper mapper;

    public App() {
        mapper = new ObjectMapper();
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public List<String> sortConferencesByAveragePointsPerGame() throws IOException {
        InputStream conferenceStream = Conference.class.getResourceAsStream("/conferences.json");
        InputStream teamStream = Conference.class.getResourceAsStream("/teams.json");

        List<Conference> conferences = mapper.readValue(conferenceStream, new TypeReference<List<Conference>>(){});
        List<Team> teams = mapper.readValue(teamStream, new TypeReference<List<Team>>(){});

        // Create new comparator to sort based on ppg:
        Comparator<Conference> ppgComparator = (c1, c2) -> {
            Double c1PPG = c1.getTeams().stream()
                .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().get())
                .mapToDouble(t -> t.getPointsPerGame())
                .sum();

            Double c2PPG = c2.getTeams().stream()
                    .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().get())
                    .mapToDouble(t -> t.getPointsPerGame())
                    .sum();

            return c2PPG.compareTo(c1PPG);
        };

        // sort the list of conferences by the summed total ppg of the teams in that conference
        return conferences.stream()
                .sorted(ppgComparator)
                .map(Conference::getName)
                .collect(Collectors.toList());
    }
}
