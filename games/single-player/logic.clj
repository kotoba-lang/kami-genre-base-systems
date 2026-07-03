;; kami-genre-base-systems / single-player -- gameplay, in the kami-clj subset.
;; The canonical survivors-shaped loop plus a defatom level counter that
;; escalates spawn tightness over time, so progression is felt.

(def max-alive     150)
(def base-spawn-period 20)
(def level-up-period   400)
(def fire-period   16)
(def enemy-speed   (f32 95.0))
(def weapon-range  (f32 260.0))
(def contact-range (f32 20.0))
(def spawn-radius  (f32 520.0))

(defatom level 0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 0.0) (f32 0.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; level: increases every level-up-period ticks, tightening spawn cadence.
(defsystem progress [dt]
  (when (zero? (mod (tick-n) level-up-period))
    (set-atom! level (+ level 1))))

(defn spawn-period []
  (let [tightened (- base-spawn-period level)]
    (if (< tightened 4) 4 tightened)))

(defsystem spawn [dt]
  (when (< (count-tagged "enemy") max-alive)
    (when (zero? (mod (tick-n) (spawn-period)))
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

(defsystem weapon [dt]
  (when (zero? (mod (tick-n) fire-period))
    (let [p (player)]
      (when (not= p -1)
        (let [hit (nearest-tagged "enemy" (get-x p) (get-y p) weapon-range)]
          (when (not= hit -1)
            (despawn-entity hit)))))))

(defsystem contact [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [touch (nearest-tagged "enemy" (get-x p) (get-y p) contact-range)]
        (when (not= touch -1)
          (despawn-entity touch))))))
