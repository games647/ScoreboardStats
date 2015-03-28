# ScoreboardStats

ScoreboardStats is a [Bukkit](https://github.com/Bukkit/Bukkit) plugin
    that uses the sidebar from scoreboard feature which was introduced into
    [Minecraft](http://minecraft.net) since version
    [1.5](http://mcupdate.tumblr.com/post/45267771887/minecraft-1-5).
This plugin simplifies, adds many new features and possible ways to improve
    the use of it.

Project page:
    http://dev.bukkit.org/bukkit-mods/scoreboardstats/

    http://www.curse.com/bukkit-plugins/minecraft/scoreboardstats

Build status:
    [![Build Status](http://travis-ci.org/games647/ScoreboardStats.svg)](http://travis-ci.org/games647/ScoreboardStats/)

### For Plugin Developers

https://github.com/games647/ScoreboardStats/wiki

Feel free to contribute there too

### Compatibility

This plugin is compatible with all Minecraft versions above 1.5, where the
    scoreboard feature was introduced by [Mojang](https://mojang.com/).

Based on vanilla servers (like Craftbukkit) doesn't allow individual and global
objectives at the same time. By operating over packets, we can workaround this.
For this method, you'll need ProtocolLib. It's used to prevent conflicts with
other plugins which also access the sidebar objective. Otherwise the client
could crash.

These plugins are (tested) compatible (just activate comaptibilityMode):
* HealthBar (http://dev.bukkit.org/bukkit-plugins/health-bar/)
* ColoredTags (http://dev.bukkit.org/bukkit-plugins/colored-tags/)
* GhostPlayer (http://dev.bukkit.org/bukkit-plugins/ghost-player/)
* McCombatLevel (http://dev.bukkit.org/bukkit-plugins/mccombatlevel/)

### Building

ScoreboardStats uses Maven 3 to manage building configurations,
    general project informations and dependencies.
You can compile this project yourself by using Maven.


* Just import the project from [Github](http://github.com/).
    Your IDE would detect the Maven project.
* If not: Download it from [here](http://maven.apache.org/download.cgi)
    You will find executable in the bin folder.
* Run (using IDE, console or something else)