package pe.gob.congreso.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "world_trends")
@Getter
@Setter
public class WorldTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_trends_info_id", nullable = false)
    private WorldTrendsInfo worldTrendsInfo;

    @Column(name = "trend_id", nullable = false)
    private String trendId;

    @Column(name = "name", nullable = false)
    private String name;

    // Getters and setters
}
