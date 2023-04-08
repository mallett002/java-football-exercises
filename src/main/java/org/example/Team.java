package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
    private Integer fieldGoals;
    private Integer games;
    private String id;
    private String name;
    private String nickname;
    private Integer PATs;
    private Integer points;
    private Double pointsPerGame;
    private Integer safeties;
    private Integer touchdowns;
    private Integer twoPointConversions;
}
