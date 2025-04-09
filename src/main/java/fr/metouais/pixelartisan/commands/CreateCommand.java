package fr.metouais.pixelartisan.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class CreateCommand {
    private static CommandAPICommand command;
    private static final String[] ALL_DIRECTION = new String[]{"North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"};
    private final CommandSender sender;
    private final String direction;
    private final Path filepath;
    private final int size;
    private final Location pos;
    private final int nbThreads;

    private CreateCommand(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        this.sender = sender;
        direction = (String) args.get("direction");
        filepath = (Path) args.get("filename");
        size = (Integer) Objects.requireNonNull(args.get("size"));
        pos = (Location) args.get("pos");
        var argNbThreads = args.get("nbThreads");
        nbThreads = argNbThreads==null ? 4 : (Integer) argNbThreads;
    }

    synchronized public static CommandAPICommand get() {
        if (command == null) {
            command = new CommandAPICommand("create")
                    .withArguments(new StringArgument("direction")
                            .replaceSuggestions(ArgumentSuggestions.strings(ALL_DIRECTION))
                    )
                    .withArguments(Arguments.FileArgument("filename", PixelArtisan.PATH_IMAGES,
                            Files::exists, Files::isRegularFile, Files::isReadable))
                    .withArguments(new IntegerArgument("size", 1))
                    .withArguments(new LocationArgument("pos", LocationType.BLOCK_POSITION))
                    .withOptionalArguments(new IntegerArgument("nbThreads", 1))
                    .executes((sender, args) -> {
                        new CreateCommand(sender, args).exec();
                    });
        }
        return command;
    }

    private void exec() {
        ChatUtils.sendMessage(sender,"§ecalculation of direction, face and position");
        byte[] dirH = getDirectionH(direction);
        byte[] dirW = getDirectionW(direction);
        if (dirH==null || dirW==null) return;
        byte face = getFace(direction);
        Location startLocation = pos;
        if (startLocation==null) return;
        BufferedImage img = resizeImg(filepath, size);
        if (img==null) return;
        ChatUtils.sendMessage(sender,"§ecreate pixel art..");
        ChatUtils.sendMessage(sender,"paint size : "+img.getWidth()+" "+img.getHeight());
        PixelArtisan.getInstance().getExecutorService().submit(new CreateCommandInstance(sender, startLocation, dirH, dirW, face, img, nbThreads));
    }

    private BufferedImage resizeImg(Path originalImgPath, int size){
        ChatUtils.sendMessage(sender,"§eimage recovery and resizing..");
        try {
            BufferedImage originalImg = ImageIO.read(originalImgPath.toFile());
            BufferedImage img = getBufferedImage(size, originalImg);
            Graphics2D g2D = img.createGraphics();
            g2D.drawImage(originalImg,0,0,img.getWidth(),img.getHeight(),null);
            g2D.dispose();
            return img;
        } catch (IOException e) {
            ChatUtils.sendMessage(sender,"§ccheck that the provided file is an image and that it is not corrupted");
            return null;
        }
    }

    private static BufferedImage getBufferedImage(int size, BufferedImage originalImg) {
        BufferedImage img;
        if (originalImg.getHeight()> originalImg.getWidth()){
            int w = (int) (originalImg.getWidth()*(size *1.0/ originalImg.getHeight()));
            if (w<=0) w=1;
            img = new BufferedImage(w, size,BufferedImage.TYPE_INT_ARGB);
        } else {
            int h = (int) (originalImg.getHeight()*(size *1.0/ originalImg.getWidth()));
            if (h<=0) h=1;
            img = new BufferedImage(size,h,BufferedImage.TYPE_INT_ARGB);
        }
        return img;
    }

    private byte[] getDirectionH(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        for (int i=0; i<CreateCommand.ALL_DIRECTION.length; i++){
            if (Objects.equals(CreateCommand.ALL_DIRECTION[i], direction)){
                return switch (i) {
                    case 0, 1, 2, 3 -> new byte[]{0, 1, 0};
                    case 4 -> new byte[]{0, 0, -1};
                    case 5 -> new byte[]{1, 0, 0};
                    case 6 -> new byte[]{0, 0, 1};
                    case 7 -> new byte[]{-1, 0, 0};
                    default -> null;
                };
            }
        }
        return null;
    }

    private byte[] getDirectionW(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        for (int i=0; i<CreateCommand.ALL_DIRECTION.length; i++){
            if (Objects.equals(CreateCommand.ALL_DIRECTION[i], direction)){
                return switch (i) {
                    case 0,4 -> new byte[]{1, 0, 0};
                    case 1,5 -> new byte[]{0, 0, 1};
                    case 2,6 -> new byte[]{-1, 0, 0};
                    case 3,7 -> new byte[]{0, 0, -1};
                    default -> null;
                };
            }
        }
        return null;
    }

    private byte getFace(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        return switch (direction){
            case "North" -> (byte) 1;
            case "East" -> (byte) 2;
            case "South" -> (byte) 3;
            case "West" -> (byte) 4;
            default -> (byte) 0;
        };
    }
}
