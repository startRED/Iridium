<p align="center">
  <img src="src/main/resources/assets/iridium/icon.png" alt="Iridium" width="160" height="160">
</p>

<h1 align="center">Iridium</h1>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-PolyForm--Shield--1.0.0-blue"></a>
  <img alt="Minecraft" src="https://img.shields.io/badge/Minecraft-1.21.1-green">
  <img alt="Loader" src="https://img.shields.io/badge/loader-Fabric-orange">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-red">
</p>

A Fabric optimization mod for Minecraft 1.21.1 that **complements Sodium and Lithium** by filling the gaps they don't cover: entity, particle and block-entity culling, animation culling, dynamic FPS, nametag caching, hopper throttling, explosion ray caching, random-tick throttling, and more.

> **Iridium is not a Sodium replacement.** It is designed to run *alongside* Sodium and Lithium. Rendering mixins that would conflict with Sodium auto-disable at load time, so features stay additive.

---

## Real-world performance

Measured on Minecraft 1.21.1, identical scene, with **Sodium + Lithium** as the baseline (the realistic starting point for any optimization-focused player):

| Setup                          | Avg FPS   | 1% low    | 0.2% low |
|--------------------------------|----------:|----------:|---------:|
| Sodium + Lithium               |     454.7 |     133.6 |     80.4 |
| **Sodium + Lithium + Iridium** | **499.0** | **139.7** | **89.8** |

Iridium on top of Sodium + Lithium: **+9.7% avg FPS, +4.6% 1% low, +11.7% 0.2% low**.

The gain is largest in the **0.2% low percentile** — the worst frames — which means Iridium's main contribution is **smoothing stutter** in scenes with many entities, particles and block entities, not raising the average ceiling.

---

## Features

### Client-side (rendering)

- **Entity Culling** — skips entities outside the camera frustum.
- **Particle Culling + Cap** — frustum-culls particles and caps active counts (separate cap for fireworks).
- **Block Entity Culling** — chests, furnaces, beacons, shulkers, etc. outside the frustum are skipped.
- **Animation Culling** — skips client-side animation steps for entities outside the frustum.
- **Beacon Beam Culling** — skips the beam when its 1×1024×1 column is outside the frustum.
- **Held Item Culling** — skips the item-in-hand render for mobs outside the frustum.
- **Nametag Cache** — caches rendered nametag text between frames.
- **Map Texture Cache** — skips map texture re-upload when the map data hasn't changed.
- **Dynamic FPS** — drastically reduces frame rate when the Minecraft window is minimized or unfocused (saves ~90% CPU/GPU on Alt-Tab).

### Server-side (tick)

- **Hopper Throttle** — idle hoppers tick less frequently (configurable).
- **Explosion Ray Cache** — caches raycasts for simultaneous nearby explosions.
- **Random Tick Throttle** — skips random block ticks in chunks with no nearby player.
- **Item Entity Merge Throttle** — runs merge scans every N ticks instead of every tick.
- **Villager AI Throttle** — reduces sensor/brain frequency when no player is within range.
- **Fire Spread Throttle** — slows fire propagation in chunks with no nearby player.
- **Projectile Tick Throttle** — far projectiles tick 1 in 3 ticks, with a short-lookahead raycast guard so hit detection stays intact.

### Quality of life

- In-game config screen (ModMenu integration).
- Defaults tuned for maximum performance out of the box; every feature individually toggleable.
- Debug overlay showing culled entities/particles/hoppers and estimated savings (bind hotkey in Controls).
- Optional toggle hotkeys for Entity Culling, Particle Culling and Dynamic FPS.
- `/iridium reload` command reloads `config/iridium.json` without restart.

---

## Design principle: invisible optimization

Iridium never reduces visual quality to gain FPS. **No distance-based culling, no adaptive-LOD, no "drop quality when FPS is low" tricks.** Only geometry outside the camera frustum — which by definition you can't see — is skipped. This rule is enforced throughout the codebase.

---

## Roadmap

Iridium 0.1.0 targets Minecraft 1.21.1 only. Beyond that, here's the direction for future versions — directions, not dated promises:

- **Broader Minecraft version support.** Once 0.1.0 stabilizes, Iridium will expand to the most popular long-lived Minecraft releases using [Stonecutter](https://stonecutter.kikugie.dev/) so a single source tree can produce builds for multiple MC versions. Planned targets: **1.21.4** (Winter Drop) first as a pilot, then **1.20.1** (the version still dominant in large modpack ecosystems like Create and Cobblemon). Short-lived minors (1.21.2 / 1.21.3) will likely be skipped.
- **New optimization gaps.** Features are added based on measured wins, not hype. The design philosophy stays the same: fill specific gaps left by Sodium and Lithium, never reduce visual quality to gain FPS. Community suggestions via [GitHub issues](https://github.com/startRED/Iridium/issues) are welcome.
- **What will not change.** No network calls, no telemetry, no shading, no distance-based culling, no adaptive quality tricks. These are enforced by design, not toggles you have to turn off.

Versions older than 1.20.1 are not planned — maintenance cost beyond three supported releases grows faster than the expected benefit.

---

## Requirements

| Component     | Version                |
|---------------|------------------------|
| Minecraft     | 1.21.1                 |
| Fabric Loader | 0.15.11 or newer       |
| Fabric API    | 0.102.0+1.21.1 or newer |
| Java          | 21                     |

Works on both client and dedicated server. Client features no-op on dedicated servers; server features no-op in the integrated server of a client-only install.

---

## Compatibility

| Mod              | Status                                                                    |
|------------------|---------------------------------------------------------------------------|
| **Sodium** 0.6.x | ✅ Conflicting rendering mixins auto-disable; all other features stay on. |
| **Lithium**      | ✅ No conflicts. Iridium covers gaps Lithium doesn't (hopper, villager AI, fire, projectiles). |
| **Starlight**    | ✅ No conflicts.                                                          |
| **FerriteCore**  | ✅ No conflicts.                                                          |
| **ModernFix**    | ✅ No conflicts.                                                          |
| **ModMenu**      | ✅ Optional. Used for the in-game config screen.                          |

No network calls, no telemetry, no shading.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) 0.15.11+ for Minecraft 1.21.1.
2. Drop [Fabric API](https://modrinth.com/mod/fabric-api) into your `mods/` folder.
3. Drop the Iridium `.jar` into `mods/`.
4. *(Recommended)* Add [Sodium](https://modrinth.com/mod/sodium) and [Lithium](https://modrinth.com/mod/lithium) for best results.
5. *(Optional)* Add [ModMenu](https://modrinth.com/mod/modmenu) to get the in-game config screen.

Launch the game. The config file is generated at `config/iridium.json` on first run.

---

## Building from source

```bash
./gradlew build
```

The built JAR lands in `build/libs/`.

---

## License

Released under the [PolyForm Shield License 1.0.0](LICENSE). This is a source-available license that permits use, modification, distribution, and contribution for any purpose except commercial exploitation as a competing product. Same license used by Sodium.
