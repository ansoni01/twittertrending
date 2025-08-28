package pe.gob.congreso.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "world_trends_info")
@Getter
@Setter
public class WorldTrendsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "worldTrendsInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorldTrend> trends;

}

