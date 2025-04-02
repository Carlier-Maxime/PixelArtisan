# ChangeLog

All notable changes to the project will be documented in this file.  
Please note that this changelog may not include every change made to the project but highlights the most significant updates.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.4.1] - 2025-04-02

### Added

- add time duration of creation pixel art
- use gradle for build project
- add dependence to jetbrains annotations
- add dependence to slf4j-api
- use SLF4J for logging an exception
- use java.nio for file manipulation 

### Changed

- change process of creation pixel art for more performance and avoid crash in large pixel art
- change delay of percentage msg to 5 sec
- reorganize project structure
- show percentage progression with one decimal

### Fixed

- fix some code warnings

### Removed

- remove speed parameter and multithreading (that will come back later)
- remove msg 'db is null' on first create pixel art during server session
- remove unused part of code

## [0.4.0] - 2022-03-05

### Added

- show nb block as been placed for create this pixel art
- show progress in chat after 4 chunk placed
- add parameter speed on command '**/pa create**' for choose if using multithreading
- add possibility to enable or disable using customTexture

### Changed

- change appearance of PixelArtisan message in chat

### Fixed

- fix comparison of color between block and pixel

### Removed

- remove shulker_box, coral in the block used for pixel art

## [0.3.0] - 2022-02-22

### Changed

- change version plugin in README.md
- add library version in README.md

### Fixed

- fix color comparison for best fit block with pixel

## [0.2.0] - 2022-02-22

### Changed

- if pixel in image is opaque use block opaque
- if direction used is flat use no gravity block only

## [0.1.0] - 2022-02-22

### Added

- Initial architecture of plugin spigot
- Add jar to the repo
- Add command for process texture to data necessary for create pixel art : '**/pa customTexture**'
- Base of command '**/pa create**' for create pixel art from an image
