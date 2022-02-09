------------------------------------------------------
Version 1.7.2
------------------------------------------------------
Updated to MC 1.18.1

- Added Spanish translation (thanks Nanrech !)

------------------------------------------------------
Version 1.7.1
------------------------------------------------------
- Fixed crash at launch with Carpet and KubeJS

------------------------------------------------------
Version 1.7.0
------------------------------------------------------
Updated to MC 1.18

------------------------------------------------------
Version 1.6.0
------------------------------------------------------
Updated to MC 1.17

- Added German localization (thanks soradotwav !)
- Updated French localization (thanks Koockies !)

------------------------------------------------------
Version 1.5.0
------------------------------------------------------
- Naturally spawned creepers now get replaced with creeperlings 20% of the time (was previously 100%)
- Added the `creeper-spores:creeperReplaceChance` gamerule to configure the chance that a creeper gets replaced
- Renamed `cspores_creeperGrief` gamerule to `creeper-spores:creeperGrief`, for consistency
  - Old values will be migrated upon loading your world
- Updated zh_cn localization (thanks EnterFor !)

------------------------------------------------------
Version 1.4.1
------------------------------------------------------
Updated to 1.16.2

------------------------------------------------------
Version 1.4.0
------------------------------------------------------
Updated to 1.16.1
Requires latest Fabric API for gamerules support

------------------------------------------------------
Version 1.3.7
------------------------------------------------------
Another patch brought to you by Joaoh1
- Fixed edge case health issues with creepers grown from creeperlings

------------------------------------------------------
Version 1.3.6
------------------------------------------------------
Bugfixes:
- Fixed modded creeperlings always growing into vanilla creepers

API:
- Added a new creeper registration method to the mod's API

------------------------------------------------------
Version 1.3.5
------------------------------------------------------
An update brought to you by yuwanjun564
- Added Chinese localization

------------------------------------------------------
Version 1.3.4
------------------------------------------------------
An update brought to you by Joaoh1
- Fixed creepers grown from creeperlings having less health than regular ones.

------------------------------------------------------
Version 1.3.3
------------------------------------------------------
Oops, I had forgotten to update my mixins

- Fixed a crash on world load

------------------------------------------------------
Version 1.3.2
------------------------------------------------------
- Reupload of 1.3.1 with a fixed refmap

------------------------------------------------------
Version 1.3.1
------------------------------------------------------
Updated to 1.15.2

Bug Fixes
- Gamerules were never registered on dedicated servers
- CreeperEntity's data tracker was all messed up, causing clientside log spam and uncommon crashes.

------------------------------------------------------
Version 1.3.0
------------------------------------------------------
Additions
- Support for modded creeper types (currently supporting mobZ)
    - Modded creepers get their own spores effect and creeperlings that grow into the appropriate entity
- Rudimentary API for modders who want to register modded creeper types

Changes
- Creepers with the `NoAI` property (like those used in datapacks) do not spread spores anymore
    - Spore spreading can be forcefully enabled or disabled per creeper through the `cspores:giveSpores` NBT property
- Updated French localization

------------------------------------------------------
Version 1.2.1
------------------------------------------------------
- Added Japanese localization by FlashfireEX
    
------------------------------------------------------
Version 1.2.0
------------------------------------------------------
Additions
- Added a new `cspores_creeperGrief` gamerule with 3 values:
    - CHARGED: Only charged creepers can destroy terrain. This is the default.
    - NEVER: Creepers can never destroy terrain.
    - VANILLA: All creepers can destroy terrain, just like in vanilla

------------------------------------------------------
Version 1.1.0
------------------------------------------------------
Additions
- Creeperlings are now tempted by bonemeal
- Creeperlings that have been given bonemeal will stop fleeing from players

------------------------------------------------------
Version 1.0.1
------------------------------------------------------
Additions
- French (fr_fr) translation by Lykrast

Bug Fixes
- Crash on dedicated servers


------------------------------------------------------
Version 1.0.0
------------------------------------------------------
Initial release of Creeper Spores