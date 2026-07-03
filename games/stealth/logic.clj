;; kami-genre-base-systems / stealth -- gameplay, in the kami-clj subset.
;; Guards drift on fixed patrol positions; alert rises within detection
;; range, decays otherwise. Reach the goal with low alert to win.

(def detect-range (f32 120.0))
(def goal-range   (f32 30.0))
(def alert-max    100)

(defatom alert 0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 -400.0) (f32 0.0) (f32 0.0)))
  (let [g1 (spawn-entity "guard")] (set-position! g1 (f32 0.0)   (f32 100.0)  (f32 0.0)))
  (let [g2 (spawn-entity "guard")] (set-position! g2 (f32 0.0)   (f32 -100.0) (f32 0.0)))
  (let [goal (spawn-entity "goal")] (set-position! goal (f32 400.0) (f32 0.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; alert: rises while a guard is within detection range, decays otherwise.
(defsystem watch [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [g (nearest-tagged "guard" (get-x p) (get-y p) detect-range)]
        (if (not= g -1)
          (when (< alert alert-max)
            (set-atom! alert (+ alert 2)))
          (when (< 0 alert)
            (set-atom! alert (- alert 1))))))))

;; win condition: reach the goal while alert stays below the threshold.
;; goes silent (no reset primitive beyond re-init exists) once reached --
;; the goal despawns so the round has an observable end.
(defsystem reach [dt]
  (let [p (player)]
    (when (not= p -1)
      (let [g (nearest-tagged "goal" (get-x p) (get-y p) goal-range)]
        (when (and (not= g -1) (< alert alert-max))
          (despawn-entity g))))))
