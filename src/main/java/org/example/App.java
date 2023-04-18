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

    public String getTeamWithHighestTouchdownToFieldGoalRatio() throws IOException {
        InputStream teamsStream = null;

        try {
            teamsStream = Team.class.getResourceAsStream("/teams.json");
        } catch (Exception exception) {
            System.out.println("Error parsing json: " + exception);
        }

        List<Team> teams = mapper.readValue(teamsStream, new TypeReference<List<Team>>() {});

        Optional<Pair<String, Double>> teamsToRatio = teams.stream()
                .map(team -> {
                    int touchdowns = Optional.of(team.getTouchdowns()).orElse(0);
                    int fieldGoals = Optional.of(team.getFieldGoals()).orElse(0);
                    double ratio = (double) touchdowns / fieldGoals;

                    return new Pair<>(team.getName(), ratio);
                }).min((a, b) -> b.getRight().compareTo(a.getRight()));

        if (teamsStream != null) {
            teamsStream.close();
        }

        return teamsToRatio.map(Pair::getLeft).orElse(null);
    }

    public String getConferenceWithHighestTouchdownToFieldGoalRatio() {
        InputStream conferenceStream = null;
        InputStream teamStream = null;

        conferenceStream = Conference.class.getResourceAsStream("/conferences.json");
        teamStream = Team.class.getResourceAsStream("/teams.json");

        try {
            List<Conference> conferences = mapper.readValue(conferenceStream, new TypeReference<List<Conference>>() {});
            List<Team> teams = mapper.readValue(teamStream, new TypeReference<List<Team>>() {});
//
//            List<Pair<Double, String>> conferenceRatiosToNames = conferences.stream()
//                    .map(conference -> {
//                        Pair<Integer, Integer> tdsToFgs = conference.getTeams().stream()
//                                .map(teamId ->  teams.stream().filter(t -> t.getId().equals(teamId)).findAny().orElse(null)) // list Team
//                                .filter(Objects::nonNull)                                                                          // filter out nulls
//                                .map(team -> {                                                                                     // map to stream of {TD, FG}
//                                    int touchdowns = Optional.of(team.getTouchdowns()).orElse(0);
//                                    int fgs = Optional.of(team.getFieldGoals()).orElse(0);
//
//                                    return new Pair<>(touchdowns, fgs);
//                                })
//                                .reduce(                                                                                            // reduce to {confTotalTD, confTotalFG}
//                                        new Pair<>(0, 0),
//                                        (memo, currPair) -> {
//                                            // Pair<Td, Fg>
//                                            Integer td = currPair.getLeft();
//                                            Integer fg = currPair.getRight();
//
//                                            Integer totalTd = memo.getLeft() + td;
//                                            Integer totalFg = memo.getRight() + fg;
//
//                                            memo.setLeft(totalTd);
//                                            memo.setRight(totalFg);
//
//                                            return memo;
//                                        },
//                                        (a, b) -> {
//                                            Pair<Integer, Integer> result = new Pair<>();
//                                            result.setLeft(a.getLeft() + b.getLeft());
//                                            result.setRight(a.getRight() + b.getRight());
//
//                                            return result;
//                                        }
//                                );
//
//                        double ratio = (double) tdsToFgs.getLeft() / tdsToFgs.getRight();
//
//                        return new Pair<>(ratio, conference.getName());
//                    })
//                    .sorted((a, b) -> b.getLeft().compareTo(a.getLeft()))
//                    .collect(Collectors.toList());
//
//            System.out.println(conferenceRatiosToNames);
//
//            Objects.requireNonNull(conferenceStream).close();
//            Objects.requireNonNull(teamStream).close();
//
//            return conferenceRatiosToNames.size() > 0 ? conferenceRatiosToNames.get(0).getRight() : null;

            // Alternative way:
            TreeMap<Double, String> conferencesByRatio = conferences.stream().reduce(                                                         // reduce conferences to TreeMap<ratio, name>
                    new TreeMap<>(),
                    (memo, conference) -> {
                        // build up Pair<totalTd, totalFg>
                        Pair<Integer, Integer> totals = conference.getTeams().stream()                                                          // stream on teamIds for conference
                                .map(teamId -> teams.stream().filter(t -> t.getId().equals(teamId)).findAny().orElse(null))               // turn into stream of Teams
                                .filter(Objects::nonNull)                                                                                       // get rid of nulls
                                .map(team -> {                                                                                                  // turn into stream of Pair<tds, fgs>
                                    int tds = Optional.ofNullable(team.getTouchdowns()).orElse(0);
                                    int fgs = Optional.ofNullable(team.getFieldGoals()).orElse(0);

                                    return new Pair<>(tds, fgs);
                                })
                                .reduce(new Pair<>(), (subTotalPair, currPair) -> {                                                             // sum up tds & fgs into Pair<totalTds, totalFgs>
                                    subTotalPair.setLeft(Optional.ofNullable(subTotalPair.getLeft()).orElse(0) + currPair.getLeft());
                                    subTotalPair.setRight(Optional.ofNullable(subTotalPair.getRight()).orElse(0) + currPair.getRight());

                                    return subTotalPair;
                                });

                        double ratio = (double) totals.getLeft() / totals.getRight();                                                           // generate the ratio for conference TreeMap
                        memo.put(ratio, conference.getName());
                        return memo;
                    },
                    (treeOne, treeTwo) -> {
                        treeOne.putAll(treeTwo);
                        return treeOne;
                    }
            );

            return conferencesByRatio.lastEntry().getValue();                                                                                   // return the last value (the highest one)

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getConferenceWithFewestTeams() {
        InputStream conferenceStream = Conference.class.getResourceAsStream("/conferences.json");

        List<Conference> conferences;

        try {
            conferences = mapper.readValue(conferenceStream, new TypeReference<List<Conference>>() {});

            Optional<Conference> maybeConference = conferences.stream()
                    .min(Comparator.comparingInt(conf -> conf.getTeams().size()));

            return maybeConference.map(Conference::getName).orElse(null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }





















}
