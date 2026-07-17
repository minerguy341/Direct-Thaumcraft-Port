# Thaumcraft → Minecraft 1.21.1 (NeoForge) — Porting Plan

## Context

Thaumcraft's implementation was never open-sourced. What exists publicly is:

- **The Thaumcraft 6 API, beta 26** (published by Azanor for addon developers) — 118 Java
  files defining the data model and interfaces of every major system. Cloned in this
  workspace from `minerguy341/thaumcraft-api` (master).
- **The community localization files** (`en_us.lang` etc.) already in this repo — a complete
  inventory of every item, block, research entry, and tooltip in TC6.
- **The TC4 API** (`Azanor/thaumcraft-api`, branch `1.7.10`) as a design reference —
  *action item: push this branch into the `minerguy341/thaumcraft-api` fork so it can be
  fetched here.*

This project is therefore a **clean-room reimplementation**, not a decompile-and-recompile
port: we port the published API forward to 1.21.1 NeoForge and write the implementation
fresh, guided by documentation, the wiki, and the lang files.

## Locked design decisions (agreed with project owner)

| Area | Decision |
|---|---|
| Base architecture | TC6 API (beta 26) ported to 1.21.1 NeoForge |
| Casting | **Wands** (TC4-style rods + caps), not casting gauntlets |
| Foci | **TC6 focus engine** (`casters/FocusEngine` node graph: medium + effect + modifiers) socketed into wands |
| Vis | **Hybrid**: wands store six primal aspects (TC4 costs & cap/rod discounts), trickle-recharge from regional aura, fast-charge at nodes |
| Aura | **Aura nodes feed the regional (per-chunk) aura** — TC4 nodes as sources, TC6 aura as the ambient pool |
| Node jarring | **Yes** — nodes can be jarred and relocated, TC4-style |
| Research | **Blend**: TC6 theorycraft card minigame at the Research Table, but theorycrafting **produces a research paper item** (TC4-style research notes) rather than abstract theory points |
| Golems | **TC6 seals & materials** system (fully specced in `golems/` + `golems/seals/`) |
| Mana beans | **Cut** |
| Aspects | Keep compound aspects (the `Aspect` class already supports `components`/`mixList`) — needed for TC4-flavored scanning and research |

Open questions to resolve during implementation (ask owner when reached):
- Exact research-paper flow (does the paper require completion steps, or is it consumed to unlock?)
- Eldritch/Outer Lands content scope (late phase)
- Mod id / display name (trademark caution around "Thaumcraft" if published publicly)

## Target stack

- **Minecraft 1.21.1**, **NeoForge 21.1.x** (latest stable)
- **Java 21**, Mojang official mappings, **ModDevGradle**
- Mod id `thaumcraft` (revisit before public release)
- Repo layout:
  ```
  src/main/java/thaumcraft/api/      ← ported API (keep package names for future addon compat)
  src/main/java/thaumcraft/common/   ← implementation (blocks, items, tiles, systems)
  src/main/java/thaumcraft/client/   ← screens, renderers, particles
  src/main/resources/                ← models, lang (converted), textures
  src/generated/                     ← datagen output (recipes, tags, worldgen JSON)
  ```

## API migration map (1.12.2 Forge → 1.21.1 NeoForge)

Derived from an import inventory of all 118 API files. The API is a mechanical port; counts
show how often each type appears.

| 1.12.2 construct (uses) | 1.21.1 replacement |
|---|---|
| `ItemStack` NBT (46) | `ItemStack` + **Data Components** (`DataComponentType`) for vis charge, focus package, seal config |
| `EntityPlayer` (41) / `EntityPlayerMP` | `Player` / `ServerPlayer` |
| `World` (23) | `Level` |
| `NBTTagCompound` (20) / `NBTTagList` | `CompoundTag`/`ListTag` — prefer **Codecs** for anything serialized |
| `ResourceLocation` ctor (16) | `ResourceLocation.fromNamespaceAndPath` (ctor is private now) |
| `EnumFacing` (15) | `Direction` |
| `EntityLivingBase` (12) | `LivingEntity` |
| `TextComponentTranslation` / `I18n` (17) | `Component.translatable` |
| `RayTraceResult` (7) | `HitResult`/`BlockHitResult` |
| `OreDictionary` (5+) / `ShapedOreRecipe` | **Tags** (`TagKey<Item>`) + vanilla-style JSON recipes |
| `TileEntity` (5) | `BlockEntity` + `BlockEntityType` |
| `IBlockState` (5) | `BlockState` |
| `@SideOnly` (4) | `Dist` checks / client classes split into `thaumcraft.client` |
| `IItemHandler` wrappers (5) | Still exist in NeoForge — `Capabilities.ItemHandler.BLOCK` |
| Capabilities `IPlayerKnowledge`, `IPlayerWarp` | **NeoForge Data Attachments** on the player + sync payloads |
| `GameData`/`GameRegistry` | `DeferredRegister` / `Registries` |
| `Potion` (2) | `MobEffect` via `DeferredRegister` |
| `DamageSource` subclasses (2) | Data-driven **damage types** (JSON) + `DamageSources` helpers |
| Custom packets (impl side) | `PayloadRegistrar` + `CustomPacketPayload` + `StreamCodec` |
| Item→aspect registration in code | **NeoForge Data Maps** (JSON, datapack-reloadable) with `AspectRegistryEvent` kept for programmatic addon entries |
| Code-registered worldgen | Datapack worldgen: configured/placed features + biome modifiers (JSON via datagen) |
| `.lang` files | `en_us.json` etc. — script the conversion of the 10 lang files already in this repo |

## Subsystem plans

### 1. Aspects & scanning (`aspects/`, `research/Scan*`)
Port `Aspect`, `AspectList`, `AspectHelper`, container/source interfaces nearly as-is.
Item/block/entity aspect assignments move to a **data map** (`data/thaumcraft/data_maps/item/aspects.json`)
with inheritance rules (recipe-derived aspects computed at recipe-manager reload, as TC did).
Thaumometer scanning ports from `ScanningManager` + `Scan*` classes; scan results write to the
player knowledge attachment.

### 2. Research & Thaumonomicon (`research/`, `research/theorycraft/`)
TC6 b26 already loads research entries from JSON — keep that format (categories, entries,
stages, addenda) as datapack assets. Port `TheorycraftManager` and all 10 card types.
**Blend spec:** completing a theorycraft session mints a **Research Paper item** (data
component: research key + progress) instead of directly granting theory; reading/completing
the paper unlocks the entry in the Thaumonomicon. Thaumonomicon GUI is a from-scratch
`Screen` (node graph per category, zoom/pan) — largest single client task.

### 3. Aura & nodes (`aura/AuraHelper`) — the hybrid centerpiece
- Per-chunk aura (vis + flux, base cap) stored via **chunk data attachments**, ticked server-side.
- **Aura node** block entity (invisible, wrenchable/jarrable) generated during worldgen with
  type (normal/bright/pale/hungry/tainted…), modifier, and primal aspect mix.
- Nodes **regenerate the aura** of surrounding chunks toward cap (radius by node size) —
  this replaces TC6's silverwood-only regen and gives nodes their TC4 gameplay weight.
- **Node jarring**: break with a jar per TC4 rules (node weakens one grade), place elsewhere;
  jarred node feeds aura in its new location. Hungry-node hazards preserved.
- Flux: byproduct of over-drain and infusion instability; feeds taint events later.

### 4. Wands & vis
- Wand = item with **components**: rod id, cap id, per-primal vis stored (centivis), socketed focus.
- Costs pay in primal vis with cap/rod **discounts** (TC4 tables); `IVisDiscountGear` armor works too.
- Recharge: slow trickle from chunk aura (drains aura), fast-charge held near a node,
  recharge pedestal later. `IRechargable`/`RechargeHelper` port to component-based vis.

### 5. Focus engine on wands (`casters/`)
Port `FocusEngine`, `FocusPackage`, node/medium/effect/mod classes and `Trajectory` intact —
it's self-contained. Replace `ICaster` implementor: the **wand** (not gauntlet) is the caster.
Focal Manipulator (focus-building station + GUI) is the implementation task; effects
(fire, shock, break, exchange…) each need a `FocusEffect` impl + particles.

### 6. Crafting stations (`crafting/`)
All become real `Recipe<RecipeInput>` types with serializers/codecs + JSON + datagen + JEI/EMI later:
- **Arcane Workbench**: shaped/shapeless + primal crystal slots + vis cost (discounts apply).
- **Crucible**: aspect-sum matching from thrown items; port `CrucibleRecipe` logic.
- **Infusion Altar**: center item + surrounding pedestals, instability events, `IInfusionStabiliser(Ext)` for candles/skulls.
- **Dust triggers** (`IDustTrigger`): salis mundus block-conversion (bookshelf→Thaumonomicon, cauldron→crucible, workbench→arcane workbench).

### 7. Essentia logistics (`aspects/IEssentiaTransport`)
Warded jars, tubes (with valves/filters/buffers), alembics, essentia smelter, centrifuge.
`IEssentiaTransport` ports as a **block capability**. Tube network = per-tick suction
propagation as in TC; BERs for jar labels/contents.

### 8. Golems & seals (`golems/`, `golems/seals/`)
Port as specced: golem = entity assembled from material/head/arms/addons with `EnumGolemTrait`s;
**seals** placed on blocks define tasks (harvest, gather, guard, fill, empty…) via `ISeal*`
interfaces + `TaskController`. Golem AI on vanilla goal system; seal GUIs from `ISealGui`/config parts.

### 9. World content & worldgen
Blocks/items enumerated by `BlocksTC`/`ItemsTC` + the lang files: greatwood/silverwood trees
(features + biome modifiers), amber + cinnabar + quartz ores, crystal clusters (six primals),
plants (shimmerleaf, cinderpearl, vishroom). Ore tags for compat. Loot tables via datagen.

### 10. Warp, flux & taint (`capabilities/IPlayerWarp`, `potions/`, `entities/`)
Warp attachment (permanent/normal/temporary) + warp events; flux effects (`PotionFluxTaint`,
flux rifts); taint blocks/mobs (`ITaintedMob`) as a later content phase. Eldritch content
(`IEldritchMob`, crimson cult, Outer Lands?) — final phase, scope TBD with owner.

### 11. Client
Thaumonomicon screen; research table + theorycraft card UI; focal manipulator UI; seal config UI;
HUD gauges (vis in wand, local aura via goggles `IRevealer`/`IGoggles`); custom particle engine
(FXDispatcher equivalent) for essentia trails, infusion sparks, node auras; BERs for nodes
(shader-ish layered quads), jars, pedestals, altar.

### 12. Assets — flagged risk
Original textures/models/sounds are **copyrighted and not in any repo we have**. Plan:
programmer-art placeholders generated per item/block at first (flat colors + aspect glyph
style), fresh art pass later. The 32×32 aspect glyphs also need redrawing. Lang files we
legitimately have; convert `.lang` → `.json` with a small script.

## Phased roadmap

Each phase ends runnable (`gradle runClient`) with its acceptance test.

| Phase | Deliverable | Done when |
|---|---|---|
| 0 | NeoForge 21.1 scaffold on this repo, CI build, lang conversion | Mod loads, creative tab exists |
| 1 | API package ported & compiling (mechanical migration per map above) | `./gradlew build` green |
| 2 | Content skeleton: core blocks/items registered w/ placeholder art | Items visible in creative |
| 3 | Aspects + data maps + Thaumometer scanning + knowledge attachment | Scan a block, see aspects |
| 4 | Salis mundus dust triggers + Arcane Workbench + Crucible + JSON recipes | Craft thaumium in crucible |
| 5 | Aura chunks + nodes + worldgen placement + node jarring | F3-style aura debug + jar a node |
| 6 | Wands (vis store/costs/discounts, hybrid recharge) | Cast/craft with vis costs |
| 7 | Focus engine + Focal Manipulator + 4–5 core foci effects | Build & cast a custom focus |
| 8 | Thaumonomicon GUI + research JSON + theorycraft table → research paper | Full research loop playable |
| 9 | Essentia: jars, tubes, alembics, smelter + Infusion Altar | Infuse an item end-to-end |
| 10 | Golems + seals | Golem harvests a farm |
| 11 | Warp/flux/taint, mobs, remaining content parity | Content checklist vs lang files |
| 12 | Polish: particles, sounds, JEI/EMI, addon API stabilization | Release candidate |

Order rationale: crafting (4) before aura/wands (5–6) so there's something to spend vis on;
research GUI (8) after the systems it gates exist; essentia/infusion (9) needs aspects (3)
and benefits from wand-era tools.

## Risks

1. **Assets** — no legal art source; placeholder strategy above. Biggest practical burden.
2. **Thaumonomicon + theorycraft GUIs** — largest bespoke client code; budget generously.
3. **Focus engine breadth** — port compiles easily, but each effect/mod needs gameplay + VFX.
4. **Hybrid aura/node balance** — new design, will need playtesting iterations.
5. **Legal** — clean-room implementation only; no decompiled TC4/TC6 code may be committed.
   API usage follows Azanor's published-for-addons precedent. Revisit mod naming pre-release.

## Verification

- `./gradlew build` per phase; `runClient` smoke test against each phase's acceptance row.
- **GameTests** for headless-testable systems: aura tick/regen math, recipe matching
  (arcane/crucible/infusion), vis discount math, essentia transport steps.
- Datagen (`runData`) must be clean-diff before each commit touching resources.

## Immediate next steps

1. Owner: push `1.7.10` branch to `minerguy341/thaumcraft-api` (TC4 reference for wand/node numbers).
2. Phase 0: scaffold ModDevGradle project on `claude/thaumcraft-1-21-1-port-0thlcf`.
3. Phase 1: vendor the TC6 API into `src/main/java/thaumcraft/api/` and migrate it per the map.
