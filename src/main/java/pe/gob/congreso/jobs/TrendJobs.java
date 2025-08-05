package pe.gob.congreso.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.gob.congreso.service.TwitterTrendingService;

@Component
public class TrendJobs {

    private final TwitterTrendingService twitterTrendingService;

    public TrendJobs(TwitterTrendingService twitterTrendingService) {
        this.twitterTrendingService = twitterTrendingService;
    }

    @Scheduled(cron = "0 0 */2 * * *")
    public void tareaCadaDosHoras() {
        System.out.println("Ejecutando tarea cada 2 horas: " + new java.util.Date());
        twitterTrendingService.processAndSaveTrends("Peru");
    }

}