package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
                .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToDouble(Team::getPointsPerGame)
                .sum();

            Double c2PPG = c2.getTeams().stream()
                    .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .mapToDouble(Team::getPointsPerGame)
                    .sum();

            return c2PPG.compareTo(c1PPG);
        };

        // sort the list of conferences by the summed total ppg of the teams in that conference
        return conferences.stream()
                .sorted(ppgComparator)
                .map(Conference::getName)
                .collect(Collectors.toList());
    }

    List<String> getTeamOrTeamsWithMostPointsThroughUprights() {
        InputStream teamsStream = Team.class.getResourceAsStream("/teams.json");

        List<Team> teams;

        try {
            teams = mapper.readValue(teamsStream, new TypeReference<List<Team>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (teams.isEmpty()) {
            return new ArrayList<>();
        }

        int most = 0;
        List<String> leaders = new ArrayList<>();

        for (Team team : teams) {
            Integer fg = team.getFieldGoals();
            Integer pats = team.getPATs();
            int currTotal = 0;

            if (fg != null) currTotal += fg * 3;
            if (pats != null) currTotal += pats;

            if (currTotal < most) {
                continue;
            }

            if (currTotal == most) {
                leaders.add(team.getName());
            }

            if (currTotal > most) {
                most = currTotal;
                leaders = new ArrayList<>();
                leaders.add(team.getName());
            }
        }

        return leaders;

        /* Other way "more readable"*/
//        Map<Integer, List<String>> teamsByTotal = new HashMap<>();
//
//        teams.forEach(t -> {
//            Integer fg = t.getFieldGoals();
//            Integer pats = t.getPATs();
//            int total = 0;
//
//            if (fg != null) total += fg * 3;
//            if (pats != null) total += pats;
//
//            if (!teamsByTotal.containsKey(total)) {
//                ArrayList<String> newList = new ArrayList<>();
//                newList.add(t.getName());
//
//                teamsByTotal.put(total, newList);
//            } else {
//                List<String> theList = teamsByTotal.get(total);
//                theList.add(t.getName());
//                teamsByTotal.put(total, theList);
//            }
//
//        });
//
//        return teamsByTotal.get(Collections.max(teamsByTotal.keySet()));
    }

}
