package com.example.businesscentral.Entity;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class TeamCreationDTO {
    private String nameT;
    private Integer membreInteger;
    private Set<Long> userIds;
}
