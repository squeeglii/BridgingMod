[![](./media/banner.png)](https://github.com/CloudG360/BridgingMod)

[![MIT License](https://img.shields.io/badge/License-MIT-orange?style=flat-square)](https://github.com/CloudG360/BridgingMod/blob/latest/LICENSE.md)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-v1.20.1-blue?style=flat-square)](https://www.minecraft.net/en-us)

[![Fabric Loader Version](https://img.shields.io/badge/Fabric_Loader-v0.14.21-AA8554?style=flat-square)](https://fabricmc.net/use/installer/)
[![Forge Mod Loader Version](https://img.shields.io/badge/Forge-v1.20.1_47.1.37-CC1602?style=flat-square)](https://neoforged.net/)
[![Cloth Config Version](https://img.shields.io/badge/Cloth_Config-v11.0.99-pink?style=flat-square)](https://modrinth.com/mod/cloth-config)
[![Mod Menu Version](https://img.shields.io/badge/Mod_Menu-v7.1.0-indigo?style=flat-square)](https://modrinth.com/mod/modmenu)


[![Modrinth](https://img.shields.io/modrinth/dt/lO3s8hjs?logo=modrinth&style=flat-square)](https://modrinth.com/mod/bridging-mod)
[![Curseforge](https://cf.way2muchnoise.eu/short_bridging-mod.svg?badge_style=flat)](https://www.curseforge.com/minecraft/mc-mods/bridging-mod)

---

# Project Overview

This mod is an implementation of a bridging assist feature commonly referred to as "Reach-Around Placement" for **Forge,
Fabric, & Quilt!** It's very similar to the functionality found in both Minecraft: Bedrock Edition, & the Quark mod, 
with the current versions source being written from scratch.

The bridging assistance allows for placing blocks in gaps in-front, above, or below blocks even if the necessary surface
isn't visible! To mark when the feature is available, an additional crosshair is shown to indicate the direction
that a block can be placed in. When the F3 menu is visible, there's even an additional bounding box showing the exact
position it's placing in.

This mod's official project pages can be found below:

- [Modrinth](https://modrinth.com/mod/bridging-mod)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bridging-mod)


## 📜 Credits

Previous versions of the Bridging Mod (< 2.0.0) were based on the Quark implementation of "Reach-Around Placement". 
You can find Quark's source here:

- [https://github.com/VazkiiMods/Quark](https://github.com/VazkiiMods/Quark)

--- 


# Contributing

Pull Requests and Issues are always welcome! Try to stick to templates where available but deviate if some components
don't apply. Detail is important when debugging an issue or trying to implement a new system however so prioritise
that!

Thanks for any help in advance! :)  -- I keep an eye out for Issues and PRs fairly regularly.


## 📦 Building/Running the project

The project can be built with:

- `gradle cleanAll build collectJars` / `gradlew cleanAll build collectJars` depending on your install.
- This should build to `/build/for-publishing/`

If running the mod in a dev environment, runs should be created automatically. If not, consult the 
[Fabric Loom Wiki](https://fabricmc.net/wiki/documentation:fabric_loom) on how to generate these through gradle.


## 📈 Updating Versions / Adding Dependencies

A lot of this project is streamlined to make version updates quicker by reducing the amount of redundant version
strings. All mod dependencies should have their versions listed in the `gradle.properties` file, using variables
to drop them into files such as `fabric.mod.json`, `mods.toml` & this README when needed. Minecraft & Fabric versions 
are handled in the exact same way for the same reasons.


### For Minecraft Version Updates:

- Check [the Fabric Develop utility](https://fabricmc.net/develop/) to get the version strings for a version
  - do NOT use `yarn_mappings` -- this project uses Mojmaps
- Change the [Recommended Minecraft Forge version](https://files.minecraftforge.net/net/minecraftforge/forge/) to the
  corresponding loader version.
  - NeoForge support should work out of the box for now.
- Copy the versions found into the appropriate entries found in `gradle.properties`
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For Updating Dependencies:

- Change the dependency version found in `gradle.properties`
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For New Dependencies:

- Add a new entry to `gradle.properties` with the dependency's version.
- Add the dependency inside `build.gradle`, using a project placeholder referencing the `gradle.properties` property
- Add the dependency to the fabric `fabric.mod.json`, using a placeholder referencing the `gradle.properties` property
- Add the dependency to the forge `mods.toml`, using a placeholder referencing the `gradle.properties` property
- Add a new badge to `/template_docs/README.md`, using a placeholder referencing the `gradle.properties` property
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions

---