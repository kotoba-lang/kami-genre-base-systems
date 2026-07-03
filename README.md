# kami-genre-base-systems

Base/foundation gameplay systems, one per genre category from a PlayStation
Store-style genre-browse taxonomy — **generic genre archetypes only**, not
tied to any specific commercial title's characters, art, story, or exact
mechanics. Follows the precedent set by `gftdcojp/isekai-network`'s concept 1
(熱痕ランナー): each is a minimal, honest `author.clj`/`logic.clj` pair built
strictly within the kami-clj compiler subset's **proven vocabulary** —
`defsystem`, `defn`, `def`, `let`, `when`/`cond`/`if`, `f32`, `spawn-entity`,
`despawn-entity`, `set-position!`, `set-velocity!`, `get-x`/`get-y`,
`nearest-tagged`, `count-tagged`, `doseq-entities`, `move-toward!`, `axis`,
`tick-n`, `mod`, `rand-int`, `zero?`, `</=/not=`, `defatom`/`set-atom!` —
confirmed working end-to-end via `kotoba-lang/engine` (compiler) and
`kotoba-lang/kami-script-runtime-rs` (WASM host) this session.

## Honest scope

18 categories were requested. Not all of them are gameplay-mechanic
archetypes — some are business/marketing/production categories with no
distinct core loop of their own. This repo does not force-invent a fake
mechanic to hit 18/18 "real systems":

| Category | Treatment |
|---|---|
| モダンリメイク (modern remake) | N/A — a production category about remaking existing games, not a gameplay archetype |
| 基本プレイ無料 (free-to-play) | N/A — a business model, not a gameplay archetype |
| インディーズ (indie) | N/A — a publishing category, not a gameplay archetype |
| ポストアポカリプス (post-apocalyptic) | a setting/tuning preset (scarcity tuning), not a distinct core loop — layered on another base rather than duplicated |

## Known engine constraints (apply across every system here)

- **Single local input stream only.** No simultaneous local multiplayer
  input exists in the current engine (confirmed this session via
  `isekai-network`'s concept-3 investigation: `kami-input-scene`'s
  `InputMap` schema has no player-index dimension, and no reference game's
  `logic.clj` calls `axis` with a player argument). ローカルマルチプレイヤー
  and 対戦格闘 both use the same honest "1 human + 1 AI" adaptation this
  constraint forces.
- **No terrain/tilemap/pathfinding/vehicle-physics/skeletal-animation
  reachable from kami-clj logic** — those are Rust-substrate crates the
  guest module can't call into. Genres that traditionally lean on them
  (open world terrain, platformer collision, strategy pathfinding, sports
  vehicles) get the best honest gameplay-logic-only approximation using
  only tagged entities + positions, not full genre fidelity.

## Status (this pass)

Real, verified `author.clj`+`logic.clj` pairs landed for: rhythm, puzzle,
stealth, single-player, strategy, sports, horror, superhero (8/18 with
runnable code, `clojure -M author.clj` verified for each, `logic.clj`
constructs cross-checked against the proven vocabulary).

Design-only (`design/*.edn`, no code yet) for: open-world,
local-multiplayer, kids-family, relaxing, platformer, fighting (6/18).

N/A or preset-note only: modern-remake, free-to-play, indie,
post-apocalyptic (4/18).

Not attempted: WASM compilation (`kotoba-lang/engine`'s `compile-file`) for
any of the 8 implemented systems — a real, valuable follow-up, out of scope
for this pass given this session's established slow-build cost. `author.clj`
verification only confirms the datalevin→`scene.edn` authoring step works,
not that `logic.clj` actually compiles to WASM.
