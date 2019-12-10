# Mandelbrot Fractal Zoomer

This project creates a GIF or MP4 of zooming into a single point on the Mandelbrot Set.  Can also generate a single PNG.

## Requirements

In order to use this project, must have Java SDK installed.

For creating MP4, FFMPEG must be installed

## Setup

1. Clone this repository
2. In terminal in project folder, execute `javac src/Main.java && java src/Main` to compile and run

## Usage

In the startup window, you can specify the following fields:

| Name  | Description | Usage |
| ------------- | ------------- | ------------- |
| X-coordinate  | X-coordinate of the center of the image  | GIF, MP4, PNG |
| Y-coordinate  | Y-coordinate of the center of the image  | GIF, MP4, PNG |
| Zoom Factor | The factor by which the zoom is increased every image | GIF, MP4 |
| Number of Images | The number of frames to create | GIF, MP4 |
| Iterations | The number of iterations per pixel (higher = more precise) | GIF, MP4, PNG |
| Initial Zoom | The zoom of the starting image | GIF, MP4, PNG |
| Frames per Second | The frames per second of the animation | GIF, MP4 |
| Dimensions | The dimensions of the output | GIF, MP4, PNG |
| Output folder | The folder which the animation is built and dumped to | GIF, MP4, PNG |

Additionally there are the following buttons:

| Name  | Description | Usage |
| ------------- | ------------- | ------------- |
| Pause/Resume | Stops/resumes initializing threads to create new images  | GIF, MP4 |
| Cancel | Cancels the initialization of new image threads, and finishes already in progress images | GIF, MP4 |
| Force Cancel | Immediately stops creating all images and creates animation with created images | GIF, MP4, PNG |
| Create GIF | Create the GIF | GIF |
| Create MP4 | Create the MP4 | MP4 |
| Create PNG | Create the PNG | PNG |

## Suggestions

This program can become quite slow, so here are a few suggestions to keep runtime down a bit
 - Decrease iterations (Create final image first, then decrease iterations until image isn't precise enough)
 - Decrease dimensions (Less pixels = less runtime)
