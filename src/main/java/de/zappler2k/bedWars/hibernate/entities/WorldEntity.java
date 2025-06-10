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
@Table(name = "gameWorlds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorldEntity {

    @Id
    private String mapName;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] worldData;
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    @Column(name = "last_modified")
    private java.time.LocalDateTime lastModified;
}
