#!/usr/bin/env python3
"""Generate placeholder models, blockstates, loot tables, and programmer-art textures
for registered content. Deterministic per id so re-runs are stable. Real art replaces
the PNGs later without touching the JSON.

Kinds: item, block, ore, log, leaves.
"""
import hashlib
import json
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "thaumcraft"
DATA = ROOT / "src" / "main" / "resources" / "data" / "thaumcraft"

ITEMS = [
    "salis_mundus", "amber", "quicksilver", "tallow", "fabric", "filter",
    "alumentum", "void_seed", "crystal_essence", "phial_empty",
    "ingot_thaumium", "ingot_void", "ingot_brass",
    "nugget_thaumium", "nugget_void", "nugget_quicksilver",
    "thaumometer", "scribing_tools",
]

BLOCKS = {
    "stone_arcane": "block",
    "stone_arcane_brick": "block",
    "ore_cinnabar": "ore",
    "ore_amber": "ore",
    "amber_block": "block",
    "log_greatwood": "log",
    "log_silverwood": "log",
    "plank_greatwood": "block",
    "plank_silverwood": "block",
    "leaves_greatwood": "leaves",
    "leaves_silverwood": "leaves",
    "crucible": "block",
    "arcane_workbench": "block",
    "research_table": "block",
    "pedestal_arcane": "block",
}

# hand-tuned colors where the guess matters; everything else hashes its hue
COLOR_OVERRIDES = {
    "salis_mundus": (200, 60, 140), "amber": (224, 148, 32), "quicksilver": (200, 208, 216),
    "ingot_thaumium": (92, 70, 152), "ingot_void": (56, 40, 72), "ingot_brass": (196, 148, 60),
    "nugget_thaumium": (92, 70, 152), "nugget_void": (56, 40, 72), "nugget_quicksilver": (200, 208, 216),
    "stone_arcane": (120, 124, 136), "stone_arcane_brick": (108, 112, 124),
    "ore_cinnabar": (168, 40, 32), "ore_amber": (224, 148, 32), "amber_block": (224, 148, 32),
    "log_greatwood": (86, 62, 40), "log_silverwood": (216, 214, 202),
    "plank_greatwood": (110, 82, 54), "plank_silverwood": (226, 224, 212),
    "leaves_greatwood": (44, 92, 36), "leaves_silverwood": (150, 196, 214),
    "crucible": (110, 114, 120), "arcane_workbench": (110, 82, 54),
    "research_table": (96, 72, 48), "pedestal_arcane": (120, 124, 136),
}

STONE = (126, 126, 126)


def color_for(name):
    if name in COLOR_OVERRIDES:
        return COLOR_OVERRIDES[name]
    h = hashlib.md5(name.encode()).digest()
    return 64 + h[0] % 160, 64 + h[1] % 160, 64 + h[2] % 160


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c)


def png(pixels):
    raw = b""
    for row in pixels:
        raw += b"\x00" + b"".join(struct.pack("4B", *p) for p in row)

    def chunk(t, d):
        c = struct.pack(">I", len(d)) + t + d
        return c + struct.pack(">I", zlib.crc32(t + d) & 0xFFFFFFFF)

    out = b"\x89PNG\r\n\x1a\n"
    out += chunk(b"IHDR", struct.pack(">IIBBBBB", 16, 16, 8, 6, 0, 0, 0))
    out += chunk(b"IDAT", zlib.compress(raw, 9))
    out += chunk(b"IEND", b"")
    return out


def rng_bytes(name):
    return hashlib.md5(("tex" + name).encode()).digest() * 32


def item_texture(name):
    base = color_for(name)
    dark = shade(base, 0.6)
    light = shade(base, 1.3)
    r = rng_bytes(name)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for y in range(3, 13):
        for x in range(3, 13):
            dx, dy = x - 7.5, y - 7.5
            if dx * dx + dy * dy <= 22:
                c = base
                if r[(y * 16 + x) % 128] % 5 == 0:
                    c = light
                px[y][x] = (*c, 255)
    for y in range(2, 14):
        for x in range(2, 14):
            if px[y][x][3] == 0:
                continue
            edge = any(px[y + oy][x + ox][3] == 0 for ox, oy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
            if edge:
                px[y][x] = (*dark, 255)
    return png(px)


def block_texture(name, kind):
    base = STONE if kind == "ore" else color_for(name)
    speck = color_for(name) if kind == "ore" else shade(color_for(name), 1.25)
    dark = shade(base, 0.75)
    r = rng_bytes(name)
    px = []
    for y in range(16):
        row = []
        for x in range(16):
            c = base
            v = r[(y * 16 + x) % 128]
            if kind == "log" and (y % 4 == 0):
                c = dark
            elif kind == "ore" and v % 7 == 0 and 2 <= x <= 13 and 2 <= y <= 13:
                c = speck
            elif v % 11 == 0:
                c = dark
            elif v % 13 == 0 and kind != "ore":
                c = speck
            row.append((*c, 255))
        px.append(row)
    return png(px)


def write(path, content):
    path.parent.mkdir(parents=True, exist_ok=True)
    if isinstance(content, bytes):
        path.write_bytes(content)
    else:
        path.write_text(json.dumps(content, indent=2) + "\n", encoding="utf-8")


def main():
    for name in ITEMS:
        write(ASSETS / "models" / "item" / f"{name}.json",
              {"parent": "minecraft:item/generated",
               "textures": {"layer0": f"thaumcraft:item/{name}"}})
        write(ASSETS / "textures" / "item" / f"{name}.png", item_texture(name))

    for name, kind in BLOCKS.items():
        model = f"thaumcraft:block/{name}"
        if kind == "log":
            variants = {
                "axis=y": {"model": model},
                "axis=x": {"model": model, "x": 90, "y": 90},
                "axis=z": {"model": model, "x": 90},
            }
        elif name == "crucible":
            variants = {"full=false": {"model": model}, "full=true": {"model": model}}
        else:
            variants = {"": {"model": model}}
        write(ASSETS / "blockstates" / f"{name}.json", {"variants": variants})
        write(ASSETS / "models" / "block" / f"{name}.json",
              {"parent": "minecraft:block/cube_all",
               "textures": {"all": f"thaumcraft:block/{name}"}})
        write(ASSETS / "models" / "item" / f"{name}.json", {"parent": model})
        write(ASSETS / "textures" / "block" / f"{name}.png", block_texture(name, kind))
        write(DATA / "loot_table" / "blocks" / f"{name}.json",
              {"type": "minecraft:block",
               "pools": [{"rolls": 1,
                          "entries": [{"type": "minecraft:item", "name": f"thaumcraft:{name}"}],
                          "conditions": [{"condition": "minecraft:survives_explosion"}]}],
               "random_sequence": f"thaumcraft:blocks/{name}"})

    print(f"generated assets for {len(ITEMS)} items and {len(BLOCKS)} blocks")


if __name__ == "__main__":
    main()
