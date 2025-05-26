package de.zappler2k.bedWars.hibernate.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "statsPlayers")
@Getter
@Setter
public class StatsPlayer {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private int kills;

    @Column(nullable = false)
    private int deaths;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    @Column(nullable = false)
    private long playTime;

    @Column(nullable = false)
    private int bedDestroyed;

    @Column(nullable = false)
    private int finalKills;
}
