# PixelArtisan

## Description

PixelArtisan is plugin minecraft for spigot server. <br>
It allows to create a pixel art on minecraft from an image. <br>
**Plugin Version** : 0.4 <br>
**Minecraft Version** : 1.18.* <br>
**Librairie Version** : *[1.18.1-R0.1-SNAPSHOT](https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/1.18.1-R0.1-SNAPSHOT/spigot-api-1.18.1-R0.1-20220218.224135-74.jar)*

## Commands

**prefix command** : /pa ...

### 1. /pa create [direction] [filename] [size] (x) (y) (z) (speed)

**this command creates a pixel art from the provided image.** <br>
**filename** : name of the image present in the images folder of the plugin (*server*/plugins/PixelArtisan/images) <br>
**direction** : the direction in which the image will be drawn
- North
- East
- West
- South
- FlatNorthEast
- FlatEastSouth
- FlatSouthWest
- FlatWestNorth

**size** : number of blocks for the larger side of the image, the other side is automatically calculated <br>
**x**,**y**,**z** : coordinates of the lower left corner of the image, by default: this is the position of the player executing the command. (args is optional) <br>
**speed** : the speed of pixel art creation, the normal speed prevented from being disconnected from the server during the construction of large pixel art, but it is much longer. while fast speed allows to be much faster, but you risk getting disconnected while building great pixel art. (default: normal)

### 2. /pa customTexture

**takes custom textures instead of default textures** <br>
she retrieves the custom textures present in the custom_texture folder of the plugin (server/plugins/PixelArtisan/custom_texture) <br>
is performs different processing to obtain the necessary data and saves them in the data folder of the plugin (server/plugins/PixelArtisan/data). <br>
then deletes the texture present in the texture folder of the plugin and loads the custom data. <br>
**WARNING** : drop only vanilla block textures 1.18 ! (otherwise errors may occur.)

## Installation

1. Download PixelArtisan.jar
2. Place it in plugins folder
3. reload plugin (/bukkit:reload or restart server)
4. ENJOY :smile:
