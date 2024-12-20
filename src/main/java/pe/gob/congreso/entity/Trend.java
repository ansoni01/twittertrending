package pe.gob.congreso.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trends")
@Getter
@Setter
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_info_id", nullable = false)
    private TableInfo tableInfo;

    @Column(name = "trend_id", nullable = false)
    private String trendId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "raw_name", nullable = false)
    private String rawName;

}
