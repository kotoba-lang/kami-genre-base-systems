;; kami-genre-base-systems / sports -- gameplay, in the kami-clj subset.
;; Abstract ball-and-goals: proximity pushes the ball toward the far goal;
;; crossing a fixed x-threshold scores and respawns the ball at center.

(def push-range   (f32 40.0))
(def push-speed   (f32 220.0))
(def goal-x       (f32 480.0))

(defatom score-right 0)
(defatom score-left  0)

(defn player []
  (nearest-tagged "player" (f32 0.0) (f32 0.0) (f32 1000000.0)))
(defn ball []
  (nearest-tagged "ball" (f32 0.0) (f32 0.0) (f32 1000000.0)))

(defn init []
  (let [p (spawn-entity "player")]
    (set-position! p (f32 -200.0) (f32 0.0) (f32 0.0)))
  (let [b (spawn-entity "ball")]
    (set-position! b (f32 0.0) (f32 0.0) (f32 0.0))))

(defsystem control [dt]
  (let [p (player)]
    (when (not= p -1)
      (set-velocity! p (axis "MoveX") (axis "MoveY") (f32 0.0)))))

;; push: player proximity to the ball sends it toward the +x goal.
(defsystem push [dt]
  (let [p (player)
        b (ball)]
    (when (and (not= p -1) (not= b -1))
      (let [near (nearest-tagged "ball" (get-x p) (get-y p) push-range)]
        (when (not= near -1)
          (set-velocity! b push-speed (f32 0.0) (f32 0.0)))))))

;; score: ball crossing the goal threshold scores and respawns at center.
(defsystem score [dt]
  (let [b (ball)]
    (when (not= b -1)
      (when (< goal-x (get-x b))
        (set-atom! score-right (+ score-right 1))
        (set-position! b (f32 0.0) (f32 0.0) (f32 0.0))
        (set-velocity! b (f32 0.0) (f32 0.0) (f32 0.0))))))
