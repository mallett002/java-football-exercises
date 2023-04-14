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

        TreeMap<Integer, List<String>> teamsByPoints = teams.stream()
                .map(t -> {
                    int fg = Optional.ofNullable(t.getFieldGoals()).orElse(0) * 3;
                    int pats = Optional.ofNullable(t.getPATs()).orElse(0);
                    int total = fg + pats;

                    return new Pair<String, Integer>(t.getName(), total);
                })
                .reduce(
                        new TreeMap<>(),
                        (memo, pair) -> {
                            if (!memo.containsKey(pair.getRight())) {
                                List<String> teamsWithPoints = new ArrayList<>();
                                teamsWithPoints.add(pair.getLeft());
                                memo.put(pair.getRight(), teamsWithPoints);
                            } else {
                                List<String> teamsWithPoints = memo.get(pair.getRight());
                                teamsWithPoints.add(pair.getLeft());
                            }

                            return memo;
                        },
                        (treeOne, treeTwo) -> {
                            treeOne.putAll(treeTwo);
                            return treeOne;
                        }
                );

//        return teamsByPoints.get(teamsByPoints.lastKey());
          return teamsByPoints.lastEntry().getValue();
//        map get number of points
//                order / sort
//
//                        take first few
//
//        private final class PointsForTeam {
//            public String teamName;
//            public int points;
//            public PointsForTeam(String teamName, int points) {
//                this.teamName = teamName;
//                this.points = points;
//            }
//        }
//
//        Comparator<PointsForTeam> neatComparator = (obj1, obj2) -> {
//            return obj1.points - obj2.points;
//        };
//
//        List<PointsForTeam> hi = teams.stream().map((team) -> {
//            int fieldGoalPoints = Optional.ofNullable(team.getFieldGoals()).orElse(0) * 3;
//            int extraPoints = Optional.ofNullable(team.getPATs()).orElse(0);
//            int points = fieldGoalPoints + extraPoints;
//            return new PointsForTeam(team.getName(), points);
//        }).sorted(neatComparator).collect(Collectors.toList());

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
    }


    public List<String> getTeamsPlayingFewerThanSevenGames() throws IOException {
        InputStream teamsStream = Team.class.getResourceAsStream("/teams.json");
        List<Team> teams = mapper.readValue(teamsStream, new TypeReference<List<Team>>() {});

        return teams.stream()
                .filter(team -> {
                    int gamesPlayed = Optional.of(team.getGames()).orElse(0);

                    return gamesPlayed < 7;
                })
                .map(Team::getName)
                .collect(Collectors.toList());
    }

    public List<String> getConferenceWithFewestGamesPlayed() throws IOException {
        InputStream conferencesStream = null;
        InputStream teamsStream = null;

        try {
            conferencesStream = Conference.class.getResourceAsStream("/conferences.json");
            teamsStream = Conference.class.getResourceAsStream("/teams.json");
        } catch (Exception ex) {
            System.out.printf("Error reading file: %s", ex);
        }

        List<Conference> conferences = mapper.readValue(conferencesStream, new TypeReference<List<Conference>>() {});
        List<Team> teams = mapper.readValue(teamsStream, new TypeReference<List<Team>>() {});

        TreeMap<Integer, List<String>> conferencesByGamesPlayed = conferences.stream()
                // map conference -> Pair<gamesPlayed, conferenceName>
                .map(conference -> {
                    // sum total games played for the teams in the conference
                    Integer totalGamesForConference = conference.getTeams().stream()
                            .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findFirst())
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(Team::getGames)
                            .reduce(0, Integer::sum);

                    return new Pair<Integer, String>(totalGamesForConference, conference.getName());
                })
                // reduce Pair -> TreeMap<gamesPlayed, List<conferenceNames>>
                .reduce(
                        new TreeMap<>(),
                        (memo, pair) -> {
                            if (!memo.containsKey(pair.getLeft())) {
                                memo.put(pair.getLeft(), new ArrayList<>());
                            }

                            memo.get(pair.getLeft()).add(pair.getRight());

                            return memo;
                        },
                        (treeOne, treeTwo) -> {
                            treeOne.putAll(treeTwo);
                            return treeOne;
                        }
                );

        if (conferencesStream != null) {
            conferencesStream.close();
        }
        if (teamsStream != null) {
            teamsStream.close();
        }

        // Return the one with the fewest game (first key in treeMap is lowest)
        return conferencesByGamesPlayed.firstEntry().getValue();
    }
}
