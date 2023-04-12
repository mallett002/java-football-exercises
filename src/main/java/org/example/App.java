package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

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

        @Data
        @AllArgsConstructor
        class Pair<L, R> {
            L left;
            R right;
        }

        Map<Integer, List<String>> it = teams.stream()
                .map(t -> {
                    int fg = Optional.ofNullable(t.getFieldGoals()).orElse(0) * 3;
                    int pats = Optional.ofNullable(t.getPATs()).orElse(0);
                    int total = fg + pats;

                    return new Pair<String, Integer>(t.getName(), total);
                })
                .reduce(
                        new HashMap<Integer, List<String>>(),
                        (memo, pair) -> {

                            if (!memo.containsKey(pair.getRight())) {

                                List<String> teamsWithPoints = new ArrayList<>();

                                teamsWithPoints.add(pair.getLeft());
                                memo.put(pair.getRight(), teamsWithPoints);

                            } else {
                                List<String> teamsWithPoints = memo.get(pair.getRight());
                                teamsWithPoints.add(pair.getLeft());
                                // Do I need to do this next step?
                                memo.replace(pair.getRight(), teamsWithPoints);
                            }

                            return memo;
                        },
                        (one, two) -> {
                            one.putAll(two);
                            return one;
                        }
                );
        System.out.println(it);

//        map get number of points
//                order / sort
//
//                        take first few
//
//                private final class PointsForTeam {
//                    public String teamName;
//                    public int points;
//                    public PointsForTeam(String teamName, int points) {
//                        this.teamName = teamName;
//                        this.points = points;
//                    }
//                }
//
//                Comparator<PointsForTeam> neatComparator = (obj1, obj2) -> {
//                    return obj1.points - obj2.points;
//                };
//
//                List<PointsForTeam> hi = teams.stream().map((team) -> {
//                    int fieldGoalPoints = Optional.ofNullable(team.getFieldGoals()).orElse(0) * 3;
//                    int extraPoints = Optional.ofNullable(team.getPATs()).orElse(0);
//                    int points = fieldGoalPoints + extraPoints;
//                    return new PointsForTeam(team.getName(), points);
//                }).sorted(neatComparator).collect(Collectors.toList());


        /* Other way "more readable"*/
//        Map<Integer, List<String>> teamsByTotal = new HashMap<>();
//
//        teams.forEach(t -> {
//            int fieldGoalPoints = Optional.ofNullable(t.getFieldGoals()).orElse(0) * 3;
//            int pats = Optional.ofNullable(t.getPATs()).orElse(0);
//            int total = fieldGoalPoints + pats;
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
        return Arrays.asList("hi");
    }



}
