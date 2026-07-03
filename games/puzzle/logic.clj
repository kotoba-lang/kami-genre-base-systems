;; kami-genre-base-systems / puzzle -- gameplay, in the kami-clj subset.
;; Touch 4 fixed tiles in the correct order (0,1,2,3). Wrong order resets
;; progress to zero. No drag/swap primitive exists, so this is a
;; touch-in-sequence puzzle, not a grid-swap puzzle (see design/puzzle.edn).

(def touch-range (f32 40.0))
(def num-tiles   4)

(defatom progress 0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 0.0) (f32 0.0) (f32 0.0)))
  ;; 4 fixed tiles at the corners of a small square, tagged by index order
  ;; via distinct tile spawns -- position doubles as identity since no
  ;; per-entity metadata/id-lookup beyond tag+position exists.
  (let [t0 (spawn-entity "tile")] (set-position! t0 (f32 150.0)  (f32 150.0)  (f32 0.0)))
  (let [t1 (spawn-entity "tile")] (set-position! t1 (f32 -150.0) (f32 150.0)  (f32 0.0)))
  (let [t2 (spawn-entity "tile")] (set-position! t2 (f32 -150.0) (f32 -150.0) (f32 0.0)))
  (let [t3 (spawn-entity "tile")] (set-position! t3 (f32 150.0)  (f32 -150.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; expected-position lookup for the current progress step, by index.
(defn expected-x [step]
  (cond (= step 0) (f32 150.0) (= step 1) (f32 -150.0)
        (= step 2) (f32 -150.0) :else (f32 150.0)))
(defn expected-y [step]
  (cond (= step 0) (f32 150.0) (= step 1) (f32 150.0)
        (= step 2) (f32 -150.0) :else (f32 -150.0)))

(defsystem touch [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [tile (nearest-tagged "tile" (get-x p) (get-y p) touch-range)]
        (when (not= tile -1)
          (if (and (= (get-x tile) (expected-x progress))
                   (= (get-y tile) (expected-y progress)))
            (set-atom! progress (mod (+ progress 1) num-tiles))
            (set-atom! progress 0)))))))
