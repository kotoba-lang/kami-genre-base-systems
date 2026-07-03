;; kami-genre-base-systems / strategy -- gameplay, in the kami-clj subset.
;; Resource accrues on a fixed tick; spending near the base spawns a unit
;; that walks in a straight line (no pathfinding) toward the nearest enemy.

(def econ-period  30)
(def spend-cost   10)
(def build-range  (f32 60.0))
(def unit-speed   (f32 80.0))
(def max-units    30)

(defatom resource 0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 -300.0) (f32 0.0) (f32 0.0)))
  (let [b (spawn-entity "base")]
    (set-position! b (f32 -300.0) (f32 0.0) (f32 0.0)))
  (let [e1 (spawn-entity "enemy")] (set-position! e1 (f32 300.0) (f32 100.0)  (f32 0.0)))
  (let [e2 (spawn-entity "enemy")] (set-position! e2 (f32 300.0) (f32 -100.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; economy: resource accrues automatically every econ-period ticks.
(defsystem economy [dt]
  (when (zero? (mod (tick-n) econ-period))
    (set-atom! resource (+ resource 1))))

;; build: proximity to the base spends resource and spawns a unit.
(defsystem build [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [b (nearest-tagged "base" (get-x p) (get-y p) build-range)]
        (when (and (not= b -1) (< spend-cost (+ resource 1)) (< (count-tagged "unit") max-units))
          (set-atom! resource (- resource spend-cost))
          (let [u (spawn-entity "unit")]
            (set-position! u (get-x b) (get-y b) (f32 0.0))))))))

;; units walk straight toward the nearest enemy -- no pathfinding exists.
(defsystem advance [dt]
  (doseq-entities [u "unit"]
    (let [e (nearest-tagged "enemy" (get-x u) (get-y u) (f32 1000000.0))]
      (when (not= e -1)
        (move-toward! u e unit-speed)))))

;; a unit that reaches an enemy despawns it (and itself, matching the
;; contact-consumes shape proven in survivors/isekai-network).
(defsystem clash [dt]
  (doseq-entities [u "unit"]
    (let [e (nearest-tagged "enemy" (get-x u) (get-y u) (f32 20.0))]
      (when (not= e -1)
        (despawn-entity e)
        (despawn-entity u)))))
