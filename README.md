# Hit Distance HUD (Fabric 26.2)

A client-only Minecraft Fabric mod that shows the estimated distance to another player when their damage event reaches your client.

## Features

- Shows the most recent player hit for 8 seconds.
- Drag the preview in the settings screen to reposition the HUD.
- Cycle through eight text colors.
- Set text size from 0.75x to 2.0x.
- Choose 0 to 5 decimal places.
- Settings are saved in `config/hitdistance.json`.

## Controls

Press **K** in-game to open settings. The key can also be rebound in Minecraft's Controls menu.

## Build

Requires **Java 25**, Gradle, and internet access for Gradle to download Minecraft/Fabric dependencies. From this directory, run:

```sh
gradle build
```

The jar will be written to `build/libs/`.

## Important limitation

This reports an estimate from entity positions available to the client when the server's damage packet arrives. Network latency and interpolation can shift the number, so it is not definitive proof of reach cheats. Triggerbot is an input-timing behavior and cannot be identified from hit distance alone.
