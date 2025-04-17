package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandContext;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.MessageSender;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class CreateCommand {
    private static Command command;
    private static final List<String> ALL_DIRECTION = List.of("North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth");
    private final MessageSender sender;
    private final String direction;
    private final Path filepath;
    private final int size;
    //private final BlockPos pos;
    private final int nbThreads;

    private CreateCommand(@NotNull CommandContext ctx) {
        this.sender = ctx.getSender();
        var args = ctx.getArguments();
        direction = args.getArg("direction", String.class);
        filepath = args.getArg("filename", Path.class);
        size = args.getArg("size", Integer.class);
        //pos = args.getArg("pos", BlockPos.class);
        nbThreads = args.getArg("nbThreads", Integer.class, 4);
    }

    synchronized public static Command get() {
        if (command == null) {
            var argsFactory = CommandFactory.argsFactory();
            command = CommandFactory.builder("create")
                    .argument(argsFactory.wordArgument("direction")
                        .suggests(ALL_DIRECTION)
                        .argument(argsFactory.fileArgument("filename", PixelArtisan.PATH_IMAGES,
                                List.of(Files::exists, Files::isRegularFile, Files::isReadable))
                            .argument(argsFactory.integerArgument("size", 1)
                                .argument(argsFactory.wordArgument("pos")
                                    .argument(argsFactory.integerArgument("nbThreads", 1)
                                        .execute(ctx -> new CreateCommand(ctx).exec())
                                    )
                                    .execute(ctx -> new CreateCommand(ctx).exec())
                                )
                            )
                        )
                    );
        }
        return command;
    }

    private void exec() {
        sender.send("§ecalculation of direction, face and position");
        byte[] dirH = getDirectionH(direction);
        byte[] dirW = getDirectionW(direction);
        if (dirH==null || dirW==null) return;
        byte face = getFace(direction);
        BufferedImage img = resizeImg(filepath, size);
        if (img==null) return;
        //if (pos==null) return;
        sender.send("§ecreate pixel art..");
        sender.send("paint size : "+img.getWidth()+" "+img.getHeight());
        //TODO PixelArtisan.getInstance().getExecutorService().submit(new CreateCommandInstance(sender, pos, dirH, dirW, face, img, nbThreads));
    }

    private BufferedImage resizeImg(Path originalImgPath, int size){
        sender.send("§eimage recovery and resizing..");
        try {
            BufferedImage originalImg = ImageIO.read(originalImgPath.toFile());
            BufferedImage img = getBufferedImage(size, originalImg);
            Graphics2D g2D = img.createGraphics();
            g2D.drawImage(originalImg,0,0,img.getWidth(),img.getHeight(),null);
            g2D.dispose();
            return img;
        } catch (IOException e) {
            sender.send("§ccheck that the provided file is an image and that it is not corrupted");
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
        for (int i=0; i<CreateCommand.ALL_DIRECTION.size(); i++){
            if (Objects.equals(CreateCommand.ALL_DIRECTION.get(i), direction)){
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
        for (int i=0; i<CreateCommand.ALL_DIRECTION.size(); i++){
            if (Objects.equals(CreateCommand.ALL_DIRECTION.get(i), direction)){
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
