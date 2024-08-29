## Change
sync `sakura-ryoko/litematica` 0.19.2 ~ 0.19.3-sakura.2
### Change info with upstream:
#### `sakura-ryoko/litematica` 0.19.2
- fix: vanilla breaking "unpowered/unconnected" redstone wires in uncommon situations.
- Added and tested V6 Export features in 1.20.4. Lore and Custom Names / Entity Attributes, etc have been tested more in depth.
#### `sakura-ryoko/litematica` 0.19.3-sakura.1
- Add Iris compat support & Improve Shader compatibility by IMS212 [#83](https://github.com/sakura-ryoko/litematica/pull/83)
- Add enableSchematicFluidRendering so users can 'DISABLE' the Fluid rendering of schematics; similar to ignoreExistingFluids, but for the Schematic Rendering.
#### `sakura-ryoko/litematica` 0.19.3-sakura.2
- fix the Import function from Schematic Manager to be able to Import Sponge (.schem) files, even though the code already existed.
- make the default Export function to be for the V6 (1.20.4) Litematic downgrade, because the Export as Schematic does not work.