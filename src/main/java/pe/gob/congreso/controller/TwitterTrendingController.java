package pe.gob.congreso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.congreso.service.TwitterTrendingService;

@RestController
@RequestMapping("/api/twitter-trending")
public class TwitterTrendingController {

    private final TwitterTrendingService twitterTrendingService;

    public TwitterTrendingController(TwitterTrendingService twitterTrendingService) {
        this.twitterTrendingService = twitterTrendingService;
    }

    @GetMapping("/cookies")
    public ResponseEntity<String> getCookies() {
        String cookies = twitterTrendingService.fetchCookies();
        return ResponseEntity.ok(cookies);
    }

    @PostMapping("/trends")
    public ResponseEntity<Void> processTrends(@RequestParam String country) {
        twitterTrendingService.processAndSaveTrends(country);
        return ResponseEntity.ok().build();
    }

}
