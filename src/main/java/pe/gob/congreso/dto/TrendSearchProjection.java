package pe.gob.congreso.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public interface  TrendSearchProjection {

    String getRawName();
    String getDisplayName();
    Long getTotalMentions();
    LocalDateTime getFirstSeen();
    LocalDateTime getLastSeen();

}
