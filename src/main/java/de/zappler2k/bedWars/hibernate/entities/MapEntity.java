package de.zappler2k.bedWars.hibernate.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "gameMaps")
public class MapEntity {

    @Id
    private String name;
    @Column(nullable = false)
    private String config;
    @Column(nullable = false)
    private String variant;

}
