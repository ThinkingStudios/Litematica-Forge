<center><div align="center">

<img height="100" src="icon/400x400.png" width="100"/>

# Forgematica

Litematica unofficial forge port.

**Require [MaLiLib-Forge](https://github.com/ThinkingStudios/MaLiLib-Forge).**

</div></center>

Forgematica (or Litematica-Forge) is a client-side schematic mod for Minecraft, with also lots of extra functionality especially for creative mode (such as schematic pasting, area cloning, moving, filling, deletion).

Litematica (Original mod) was started as an alternative for [Schematica](https://www.curseforge.com/minecraft/mc-mods/schematica), for players who don't want to have Forge installed on their client, and that's why it was developed for Liteloader.

For compiled builds (= downloads), see [Releases](https://github.com/ThinkingStudios/Litematica-Forge/releases)

## Development

This mod uses jitpack maven, and will use modrinth maven in the future after modrinth is released (snapshots still use jitpack).

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation "com.github.ThinkingStudios:Litematica-Forge:${forgematica_version}"
}
```

> Note: "${forgematica_version}" can be found in Releases

## Compiling
- Clone the repository
- Open a command prompt/terminal to the repository directory
- run 'gradlew build'
- The built jar file will be in build/libs/
