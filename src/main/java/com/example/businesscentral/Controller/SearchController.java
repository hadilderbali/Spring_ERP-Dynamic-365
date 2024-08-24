package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.Team;
import com.example.businesscentral.Entity.UnifiedSearchResponse;
import com.example.businesscentral.Entity.User;
import com.example.businesscentral.Service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@AllArgsConstructor
@CrossOrigin("http://localhost:4200")

public class SearchController {
    private final SearchService searchService;
    @GetMapping("/unified")
    public ResponseEntity<UnifiedSearchResponse> unifiedSearch(@RequestParam String name) {
        UnifiedSearchResponse response = searchService.unifiedSearch(name);
        return ResponseEntity.ok(response);}

    @GetMapping("/searchUser")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam("name") String name) {
        List<User> users = searchService.findUsers(name);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/searchTeam")
    public ResponseEntity<List<Team>> searchTeamsByName(@RequestParam("name") String name) {
        List<Team> teams = searchService.SearchTeam(name);
        return ResponseEntity.ok(teams);
    }
}