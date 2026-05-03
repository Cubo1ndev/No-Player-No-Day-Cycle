# No Player No Day Cycle

A lightweight server-side Minecraft mod that freezes the day/night cycle when no players are online. The world keeps ticking normally — mobs, redstone, and scheduled events all continue — only the time of day stops.

## Features

- Freezes day/night cycle when the server is empty
- Resumes exactly where it left off when a player joins
- No client mod required — uses the `doDaylightCycle` gamerule, which vanilla clients already respect (no sun jitter)
- Per-dimension support: each dimension is frozen and unfrozen independently
- Safe: only restores `doDaylightCycle` for dimensions the mod itself froze

## Compatibility

| Platform  | Version |
|-----------|---------|
| Fabric    | 1.21.1  |
| NeoForge  | 1.21.1  |

Server-side only. Vanilla clients connect without installing the mod.

## Commands

Requires operator permission level 2.

| Command | Description |
|---|---|
| `/npdcinfo` | Shows current `gameTime`, `dayTime`, day number, player count, and simulate state |
| `/npdcinfo simulate` | Toggles empty-server simulation — freezes/unfreezes time as if the server were empty, useful for testing |
