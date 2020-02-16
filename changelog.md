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