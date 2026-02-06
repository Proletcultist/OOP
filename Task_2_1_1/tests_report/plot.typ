#import "@preview/simple-plot:0.2.0": plot, scatter

#plot(
  xmin: 0, xmax: 9,
  ymin: 0, ymax: 0.2,
  show-grid: true,
  scatter(
    ((0, 0.161), (1, 0.160), (2, 0.104), (3, 0.055), (4, 0.027), (5, 0.042), (6, 0.033), (7, 0.028), (8, 0.025), (9, 0.028)),
    mark: "*",
    mark-fill: blue,
  ),
)
