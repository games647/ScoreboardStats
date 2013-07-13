package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.avaje.ebean.validation.Range;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PlayerStats")
@ToString(includeFieldNames = true)
public class PlayerStats {

    @Id
    @NotEmpty
    @NotNull
    @Length(max = 16) //A minecraft name cannot be longer than 16
    @Getter @Setter
    private String playername;

    @Range(min = 0)
    @Getter @Setter
    private int kills;

    @Range(min = 0)
    @Getter @Setter
    private int deaths;

    @Range(min = 0)
    @Getter @Setter
    private int mobkills;

    @Range(min = 0)
    @Getter @Setter
    private int killstreak;
}
