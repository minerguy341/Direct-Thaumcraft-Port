#!/usr/bin/env python3
"""Convert 1.12.2-era .lang files to modern Minecraft JSON lang files.

Reads reference/lang/*.lang and writes
src/main/resources/assets/thaumcraft/lang/<locale>.json.

Key mapping:
  item.<x>.name   -> item.thaumcraft.<x with dots -> underscores>
  tile.<x>.name   -> block.thaumcraft.<x with dots -> underscores>
  entity.<x>.name -> entity.thaumcraft.<x with dots -> underscores>
  anything else   -> kept verbatim (custom keys referenced via Component.translatable)

Special cases map legacy variant keys onto the ids actually registered by the mod.
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "reference" / "lang"
DST = ROOT / "src" / "main" / "resources" / "assets" / "thaumcraft" / "lang"

# legacy key -> modern key overrides (extend as items get registered)
SPECIAL = {
    "item.thaumonomicon.normal.name": "item.thaumcraft.thaumonomicon",
}

# extra keys injected into every locale (value per locale falls back to en_us)
EXTRA = {
    "itemGroup.thaumcraft": {"en_us": "Thaumcraft", "*": "Thaumcraft"},
}

PREFIX_MAP = [
    (re.compile(r"^item\.(.+)\.name$"), "item.thaumcraft.{}"),
    (re.compile(r"^tile\.(.+)\.name$"), "block.thaumcraft.{}"),
    (re.compile(r"^entity\.(.+)\.name$"), "entity.thaumcraft.{}"),
]


def convert_key(key: str) -> str:
    if key in SPECIAL:
        return SPECIAL[key]
    for pattern, template in PREFIX_MAP:
        m = pattern.match(key)
        if m:
            return template.format(m.group(1).replace(".", "_"))
    return key


def convert_file(path: Path) -> dict:
    out = {}
    for raw in path.read_text(encoding="utf-8-sig", errors="replace").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if "=" not in line:
            continue
        key, _, value = line.partition("=")
        key, value = key.strip(), value.strip()
        if not key:
            continue
        out[convert_key(key)] = value
    return out


def main() -> None:
    DST.mkdir(parents=True, exist_ok=True)
    for lang_file in sorted(SRC.glob("*.lang")):
        locale = lang_file.stem.lower()  # nl_NL -> nl_nl
        data = convert_file(lang_file)
        for key, values in EXTRA.items():
            data.setdefault(key, values.get(locale, values["*"]))
        dest = DST / f"{locale}.json"
        dest.write_text(
            json.dumps(data, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
            encoding="utf-8",
        )
        print(f"{lang_file.name} -> {dest.relative_to(ROOT)} ({len(data)} keys)")


if __name__ == "__main__":
    main()
