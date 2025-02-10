## Changelog for 2.6.1 (All Versions)

- Corrected config description for minimum distance sliders.


## Changelog for 2.6.0 (All Versions)

A pretty big release of small things.

__**Making Bridging More Comfortable:**__
BridgingMod tends to snap to blocks more than other mods when bridging. This can be really useful, but when it's annoying, it can be *really* annoying. With that, I've added a few config options which allow finer control over the snapping.

**Features->Minimum Bridging Distance (Horizontal & Vertical):** How far does a block need to be from you, before you can bridge at it? Before, it wasn't visible and there was a minimum distance of 1, in all directions. Now it's split into horizontal & vertical distances. Horizontally, a block must be 2.2 blocks away. Vertically, a block must be 1 block away.
**Fixes->Bridging Snap Strength:** (experimental!) Self-explanatory. A magic number that affects the size of the bridging targets.
**Fixes->Bridging Adjacency:** (experimental!) Hard to explain, but it affects blocks that catch the edge of your sightline, and changes if they're bridgeable on or not. `strict` will have the biggest difference.


__**Mod Compatibility**__
- Added a compatibility API. Should speed up updates in the future.
  - Restructured the mod internals for this. Breaking changes.
- Compatibility with Bank Storage
- Compatibility with Dank Storage
- Changed behaviour when the Freelook mod is used.
  - See Fixes -> Perspective Lock in config. You can either bridge from your head or the camera.
- Thanks @crendgrim for making dynamiccrosshair compatibility easier

__**Translations:**__
Once again, a big thank you for the translation contributions from:

- Ukranian: @Melishy, with tweaks from @xicscon
- Russian: @Melishy
- Chinese: @Xinyang-Gao

__**Other Changes**__
- Config values are now formatted with "block(s)" and "tick(s)" where appropriate.
- Changed the toggle bridging keybind to "toggle reacharound placement".