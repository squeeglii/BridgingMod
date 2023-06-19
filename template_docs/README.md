[![](./media/banner.png)](https://github.com/CloudG360/BridgingMod)

[![Minecraft Version](https://img.shields.io/badge/Minecraft-v${minecraft_version}-blue?style=flat-square)](https://www.minecraft.net/en-us)
[![Fabric Loader Version](https://img.shields.io/badge/Fabric_Loader-v${loader_version}-AA8554?style=flat-square)](https://fabricmc.net/use/installer/)
[![Attribution-NonCommercial-ShareAlike 3.0 Unported](https://img.shields.io/badge/License-GNU_GPL_3.0-mint?style=flat-square)](https://creativecommons.org/licenses/by-nc-sa/3.0/)

[![Modrinth](https://img.shields.io/modrinth/dt/lO3s8hjs?logo=modrinth&style=flat-square)](https://modrinth.com/mod/bridging-mod)
[![Curseforge](https://cf.way2muchnoise.eu/short_bridging-mod.svg?badge_style=flat)](https://www.curseforge.com/minecraft/mc-mods/bridging-mod)

---

# Project Overview

This mod is a fabric/quilt port of [Quark's](https://github.com/VazkiiMods/Quark) "Reach-Around Placement" feature with
some tweaks to refine the experience. It's similar to Minecraft: Bedrock Edition's bridging features where blocks can be
placed in horizontally adjacent block gaps or vertically underneath blocks, even if there isn't a surface to place the
blocks on.

This mod's official project pages can be found below:

- [Modrinth](https://modrinth.com/mod/bridging-mod) (Preferred)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bridging-mod)

## ⚙️ Differences To Quark

- Customizable crosshair icons to notify the player that bridging assistance is available.
  - Located at `assets/bridgingmod/textures/gui/placement_icons.png`
- More forgiving placement logic
  - Can place when under 2 block ceilings
  - Target areas are larger


## 📜 Credits

- **Quark Mod**
  - https://github.com/VazkiiMods/Quark
  - Licensed under [Attribution-NonCommercial-ShareAlike 3.0 Unported](https://creativecommons.org/licenses/by-nc-sa/3.0/)


- **Reach-Around Placement Contributors (From Quark)**
  - [Vazkii](https://github.com/Vazkii)
  - [yrsegal](https://github.com/yrsegal)
  - [williewillus](https://github.com/williewillus)
  - [heipiao233](https://github.com/heipiao233)
  - [Alwinfy](https://github.com/Alwinfy)

--- 


# Contributing

Pull Requests and Issues are always welcome! Try to stick to templates where available but deviate if some components
don't apply. Detail is important when debugging an issue or trying to implement a new system however so prioritise
that!

Thanks for any help in advance! :)  -- I keep an eye out for Issues and PRs fairly regularly.


## 📦 Building/Running the project

The project can be built with:

- `gradle clean build` / `gradlew clean build` depending on your install.
- This should build to `/build/libs/`

If running the mod in a dev environment, runs should be created automatically. If not, consult the 
[Fabric Loom Wiki](https://fabricmc.net/wiki/documentation:fabric_loom) on how to generate these through gradle.

Once the runs are generated, running them should place the environments for each in the following isolated folders:

- Client `/run/client/`
- Server `/run/server/`



## 📈 Updating Versions / Adding Dependencies

A lot of this project is streamlined to make version updates quicker by reducing the amount of redundant version
strings. All mod dependencies should have their versions listed in the `gradle.properties` file, using variables
to drop them into files such as `fabric.mod.json` & this README when needed. Minecraft & Fabric versions are handled in
the exact same way for the same reasons.


### For Minecraft Version Updates:

- Check [the Fabric Develop utility](https://fabricmc.net/develop/) to get the version strings for a version
  - do NOT use `yarn_mappings` -- this project uses Mojmaps
- Copy the versions found into the appropriate entries found in `gradle.properties`
  - `minecraft_long_version` is the same as `minecraft_version` for __release__ versions and __snapshot__ versions
  - For __pre-releases__ and __release candidates__, they should have an extra dot (`1.20-pre1` -> `1.20-pre.1`)
  - This is because the loaded Fabric dependency and the mappings are named with slightly different schemes. :(
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For Updating Dependencies:

- Change the dependency version found in `gradle.properties`
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For New Dependencies:

- Add a new entry to `gradle.properties` with the dependency's version.
- Add the dependency inside `build.gradle`, using a project placeholder referencing the `gradle.properties` property
- Add the dependency to the `fabric.mod.json`, using a placeholder referencing the `gradle.properties` property
- Add a new badge to `/template_docs/README.md`, using a placeholder referencing the `gradle.properties` property
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions

---