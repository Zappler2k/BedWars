package de.zappler2k.bedWars.hibernate.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gameMaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapEntity {
    @Id
    private String name;
    
    @Column(columnDefinition = "LONGTEXT")
    private String config;
    
    private String variant;
}
