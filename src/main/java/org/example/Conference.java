package org.example;

import lombok.Data;

import java.util.List;

@Data
public class Conference {
    private String id;
    private String name;
    private List<String> teams;
}
