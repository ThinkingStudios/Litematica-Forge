<center><div align="center">

<img height="100" src="icon/400x400.png" width="100"/>

# Forgematica

**This mod requires [BadPackets](https://modrinth.com/mod/badpackets) in 1.20.4 and above.**

Litematica unofficial (Neo)Forge port.

**Require [MaLiLib-Forge](https://github.com/ThinkingStudios/MaLiLib-Forge).**

<img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg">

<a href="https://modrinth.com/mod/forgematica">
<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
</a>
<a href="https://www.curseforge.com/minecraft/mc-mods/forgematica">
<img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">
</a>

</div></center>

Forgematica (or Litematica-Forge) is a client-side schematic mod for Minecraft, with also lots of extra functionality especially for creative mode (such as schematic pasting, area cloning, moving, filling, deletion).

Litematica (Original mod) was started as an alternative for [Schematica](https://www.curseforge.com/minecraft/mc-mods/schematica), for players who don't want to have Forge installed on their client, and that's why it was developed for Liteloader.

## Development

```gradle
repositories {
    maven { url 'https://api.modrinth.com/maven' }
}

dependencies {
    modImplementation "maven.modrinth:forgematica:${forgematica_version}"
}
```

> Note: "${forgematica_version}" can be found in [Modrinth](https://modrinth.com/mod/forgematica).

## Compiling
- Clone the repository
- Open a command prompt/terminal to the repository directory
- run 'gradlew build'
- The built jar file will be in build/libs/