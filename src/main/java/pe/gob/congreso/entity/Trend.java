package pe.gob.congreso.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trends")
@Getter
@Setter
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, columnDefinition = "TEXT")
    private String tableName;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private LocalDateTime timestamp;

    @Column(name = "trend_id", nullable = false, columnDefinition = "TEXT")
    private String trendId;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "raw_name", nullable = false, columnDefinition = "TEXT")
    private String rawName;

}
