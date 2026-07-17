# Direct Thaumcraft Port

A faithful, clean-room recreation of **Thaumcraft** for **Minecraft 1.21.1 on NeoForge** —
TC6's architecture with TC4's soul: wands, aura nodes feeding a regional aura, node jarring,
and the full research/essentia/golem suite.

> Thaumcraft's implementation was never open-sourced. This project ports the
> [Thaumcraft 6 API](https://github.com/Azanor/thaumcraft-api) (published by Azanor for
> addon developers) forward to 1.21.1 and reimplements the mod fresh, guided by
> documentation and the community localization files. No decompiled Thaumcraft code, no
> original Thaumcraft assets.

## Design in one table

| Area | Decision |
|---|---|
| Casting | TC4-style **wands** (rod + caps) with the **TC6 focus engine** socketed in |
| Vis | Six primal aspects stored per wand, TC4 costs/discounts, aura trickle-recharge, node fast-charge |
| Aura | TC4 **nodes** are the sources that feed the TC6 **per-chunk aura** |
| Node jarring | Yes — jar, relocate, accept the penalty |
| Research | TC6 theorycraft minigame that mints TC4-style **research papers** |
| Golems | TC6 seals & materials |
| Mana beans | Cut |

The full plan — API migration map, subsystem breakdowns, 13-phase roadmap, risks —
lives in [PORTING_PLAN.md](PORTING_PLAN.md).

## Status

**Phase 0–1: project scaffold + API port.** Nothing playable yet.

## Building

Requires JDK 21.

```
./gradlew build
```

## Repository notes

- `reference/lang/` — the community translation files from Azanor's `thaumcraft-beta`
  repo (the one part of Thaumcraft's content that was published); converted to modern
  JSON under `src/main/resources/assets/thaumcraft/lang/`.
- This is an independent fan project, not affiliated with Azanor. It is a separate
  project from other Thaumcraft-inspired mods by this account.
