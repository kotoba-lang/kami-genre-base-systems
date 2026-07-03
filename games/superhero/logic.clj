;; kami-genre-base-systems / superhero -- gameplay, in the kami-clj subset.
;; Same base combat loop as single-player, plus a power meter that fills on
;; defeats and unlocks a timed super mode (widened range/faster fire).

(def max-alive       120
)
(def spawn-period    14)
(def fire-period-norm 16)
(def fire-period-boost 6)
(def weapon-range-norm  (f32 260.0))
(def weapon-range-boost (f32 420.0))
(def enemy-speed      (f32 95.0))
(def contact-range    (f32 20.0))
(def spawn-radius     (f32 520.0))
(def power-max        10)
(def super-duration   200)

(defatom power 0)
(defatom super-timer 0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 0.0) (f32 0.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

(defsystem spawn [dt]
  (when (< (count-tagged "enemy") max-alive)
    (when (zero? (mod (tick-n) spawn-period))
      (let [r (rand-int 4)
            e (spawn-entity "enemy")]
        (cond
          (= r 0) (set-position! e spawn-radius (f32 0.0)    (f32 0.0))
          (= r 1) (set-position! e (f32 -520.0) (f32 0.0)    (f32 0.0))
          (= r 2) (set-position! e (f32 0.0)    spawn-radius (f32 0.0))
          :else   (set-position! e (f32 0.0)    (f32 -520.0) (f32 0.0)))))))

(defsystem ai [dt]
  (let [p (player)]
    (when (not= p -1)
      (doseq-entities [e "enemy"]
        (move-toward! e p enemy-speed)))))

;; super-timer counts down every tick while active; hitting zero ends it.
(defsystem super-tick [dt]
  (when (< 0 super-timer)
    (set-atom! super-timer (- super-timer 1))))

;; weapon: boosted range/fire-rate while super-timer is active, normal
;; otherwise -- two pre-defined constant sets, no dynamic scaling formula.
(defsystem weapon [dt]
  (let [period (if (< 0 super-timer) fire-period-boost fire-period-norm)
        range  (if (< 0 super-timer) weapon-range-boost weapon-range-norm)]
    (when (zero? (mod (tick-n) period))
      (let [p (player)]
        (when (not= p -1)
          (let [hit (nearest-tagged "enemy" (get-x p) (get-y p) range)]
            (when (not= hit -1)
              (despawn-entity hit)
              (when (< power power-max)
                (set-atom! power (+ power 1))
                (when (= power power-max)
                  (set-atom! super-timer super-duration)
                  (set-atom! power 0))))))))))

(defsystem contact [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [touch (nearest-tagged "enemy" (get-x p) (get-y p) contact-range)]
        (when (not= touch -1)
          (despawn-entity touch))))))
