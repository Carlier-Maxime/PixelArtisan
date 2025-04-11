# ChangeLog

All notable changes to the project will be documented in this file.  
Please note that this changelog may not include every change made to the project but highlights the most significant updates.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.5.0] - Work in progress

wait for the version release for the changelog

## [0.4.2] - 2025-04-11

### Added

- add dependence to commandapi
- use commandapi for all command
- auto generate data of vanilla texture on first pixel art created
- add name argument on generate texture : **'/pa texture generate <name>'**
- add debug command for show list materials : **'/pa debug listMaterial'**
- support new block introduced between 1.18.1 and 1.21.5
- limit chunk of pixel art in one tick for avoid server crash due to tick is too long (4*nbThread)
- add optional argument nbThread on pixel art creation (default: 4)

### Changed

- optimize pixel art creation (this is much faster)
- not update neighbor block for improve performance and avoid fail block placed
- tweak project structure
- improve argument verification
- change **'/pa customTexture'** to **'/pa texture'**
- change custom texture enable/disable to **'/pa texture use [name]'**
- downgrade spigot-api for work with 1.18.0

### Fixed

- fix generate data of custom texture
- fix fail not delete because as deleted before
- fix some files bug on first init plugin

### Removed

- remove default data on jar

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
