;; kami-genre-base-systems / rhythm -- gameplay, in the kami-clj subset.
;; Reuses the exact beat? tick-gated pattern proven in
;; gftdcojp/isekai-network games/01-netsurvivors/logic.clj (iteration 7).

(def beat-period   (f32 60.0))
(def beat-window   (f32 4.0))
(def target-period 20)
(def hit-range     (f32 40.0))
(def max-targets   20)

(defn beat? []
  (< (mod (tick-n) beat-period) beat-window))

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 0.0) (f32 0.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; targets spawn on a ring, capped, tick-gated -- same shape as survivors' spawn.
(defsystem spawn [dt]
  (when (< (count-tagged "target") max-targets)
    (when (zero? (mod (tick-n) target-period))
      (let [r (rand-int 4)
            t (spawn-entity "target")]
        (cond
          (= r 0) (set-position! t (f32 400.0)  (f32 0.0)   (f32 0.0))
          (= r 1) (set-position! t (f32 -400.0) (f32 0.0)   (f32 0.0))
          (= r 2) (set-position! t (f32 0.0)    (f32 400.0) (f32 0.0))
          :else   (set-position! t (f32 0.0)   (f32 -400.0) (f32 0.0)))))))

;; hit: touching a target always succeeds -- an on-beat hit additionally
;; spawns a cosmetic beat-spark. Never required, only rewarded.
(defsystem hit [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [t (nearest-tagged "target" (get-x p) (get-y p) hit-range)]
        (when (not= t -1)
          (despawn-entity t)
          (when (beat?)
            (let [spark (spawn-entity "beat-spark")]
              (set-position! spark (get-x t) (get-y t) (f32 0.0)))))))))
