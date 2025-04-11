package fr.metouais.pixelartisan.data;

import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DataGenerator {
    private static final String[][] FACES_SUFFIXES = new String[][]{{"top"},{"front", "north"},{"east"},{"back", "south"},{"west"},{"bottom"},{"side"}};
    private static final String[] OTHER_SUFFIXES = {
        "down", "up", "base", "stage", "overlay", "on", "off", "inside", "outside", "moist", "inverted",
        "inner", "empty", "occupied", "powered", "awake", "dormant", "triggered", "tip", "emissive", "lit"
    };
    private static final String[] ALL_SUFFIXES = Stream.concat(Arrays.stream(FACES_SUFFIXES).flatMap(Arrays::stream), Arrays.stream(OTHER_SUFFIXES)).toArray(String[]::new);
    private static final Pattern SuffixPattern = Pattern.compile("_(" +String.join("|", ALL_SUFFIXES)+ ")([0-9]|s)?((_.*$)|$)");

    public static void generateFromTexturesBlock(CommandSender sender, Path srcDir, String name) throws IOException {
        generateFromTexturesBlock(sender, srcDir, name, new DataManager(sender));
    }

    public static void generateFromTexturesBlock(CommandSender sender, Path srcDir, String name, @NotNull DataManager dataManager) throws IOException {
        if(FileUtils.isFolderEmpty(srcDir)) {
            ChatUtils.sendMessage(sender, "§csource folder is empty or invalid ! (fill the folder and retry)");
            if (sender instanceof Player) ChatUtils.sendMessage(sender, "§6For more information: " + PixelArtisan.GIT_LINK);
            return;
        }
        ChatUtils.sendMessage(sender,"§echecking texture and delete unnecessary files...");
        int nbDelete = checkAndDelUselessFile(srcDir);
        ChatUtils.sendMessage(sender,"§e"+nbDelete+" files have been deleted");
        ChatUtils.sendMessage(sender,"§edata processing...");
        var treeList = dataProcessing(srcDir);
        ChatUtils.sendMessage(sender,"§ecompare and save...");
        dataManager.compareWithDefaultAndSave(treeList, name);
        ChatUtils.sendMessage(sender,"§ecleanup of source folder");
        FileUtils.tryDeleteContentOfFolder(srcDir); ChatUtils.sendMessage(sender,"§acleanup finish");
        ChatUtils.sendMessage(sender,"§2custom textures have been supported.");
    }

    private static int getAverageColor(BufferedImage img){
        int r=0;
        int g=0;
        int b=0;
        int a=0;
        int nb=0;
        for (int i=0; i<img.getWidth(); i++){
            for (int j=0; j<img.getHeight(); j++){
                Color color = new Color(img.getRGB(i,j),true);
                r+=color.getRed();
                g+=color.getGreen();
                b+=color.getBlue();
                a+=color.getAlpha();
                nb++;
            }
        }
        if (nb==0) nb=1;
        return new Color(r/nb,g/nb,b/nb,a/nb).getRGB();
    }

    private static String getMaterialName(@NotNull String textureName){
        String name = SuffixPattern.matcher(textureName).replaceAll("");
        if (name.contains("_pot")){
            if (!name.equals("flower_pot")) name = "potted_" + name.split("_pot")[0];
        }
        for (String s : new String[]{
                "turtle_egg","structure_block","small_dripleaf","jigsaw","grindstone","frosted_ice","campfire",
                "beehive","calibrated_sculk_sensor","test_block","sniffer_egg","suspicious_gravel","suspicious_sand",
                "wildflowers","pink_petals"
        }){
            if (name.contains(s)) {name = s; break;}
        }
        if (name.contains("redstone_dust")) name = "redstone_wire";
        if (name.contains("rail_corner")) name = "rail";
        if (name.contains("sticky")) name = "sticky_piston";
        else if (name.contains("piston")) name = "piston";
        if (name.equals("big_dripleaf_tip")) name = "big_dripleaf";
        if (name.equals("mushroom_block")) name = "brown_"+name;
        if (name.equals("mangrove_propagule_hanging")) name = "mangrove_propagule";
        name = name.toUpperCase(Locale.ROOT);
        Material m = Material.matchMaterial(name);
        if (m==null) {
            ChatUtils.sendConsoleMessage(textureName+" alias "+name+" not found correspondance !");
            return null;
        }
        return m.name();
    }

    private static byte getFace(@NotNull String name, @NotNull String mName){
        String[] suffixs = name.split(mName.toLowerCase(Locale.ROOT));
        if (suffixs.length<=1) return 0;
        String suffix = suffixs[1];
        if (suffix.length()<=2) return 0;
        for (int i=0; i<FACES_SUFFIXES.length; i++){
            for (var faceSuffix : FACES_SUFFIXES[i]) if (suffix.contains("_"+faceSuffix)) return (byte) (i+1);
        }
        if (suffix.contains("_inner")){
            if (mName.contains("CAULDRON")) return 1;
            else return 7;
        }
        if (mName.contains("GRINDSTONE")) return 0;
        if (suffix.contains("_end")) return 6;
        if (suffix.contains("_tip")) return 7;
        return 0;
    }

    private static int checkAndDelUselessFile(Path srcDir){
        int nbDelete=0;
        try (DirectoryStream<Path> list = Files.newDirectoryStream(srcDir)) {
            for (Path file : list){
                if (!Files.isRegularFile(file) && file.toFile().delete()) nbDelete++;
                String[] nameSplit = file.getFileName().toString().split("\\.");
                if (nameSplit[nameSplit.length-1].equals("mcmeta")){
                    if (file.toFile().delete()) nbDelete++;
                    if (Path.of(srcDir+"/"+nameSplit[0]+".png").toFile().delete()) nbDelete++;
                } else if (!nameSplit[nameSplit.length-1].equals("png") && file.toFile().delete()) nbDelete++;
                for (String s : new String[]{"destroy","_plant","grass","end_portal","composter","debug","chorus","bamboo","farmland","campfire","shulker_box","coral"}){
                    if (nameSplit[0].contains(s) && file.toFile().delete()) nbDelete++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nbDelete;
    }

    private static ArrayList<TreeMap<Integer,Short>> dataProcessing(Path srcDir){
        ArrayList<TreeMap<Integer,Short>> treeList = new ArrayList<>(6);
        for (int i=0; i<6; i++) treeList.add(new TreeMap<>());
        int nbError=0;
        try (DirectoryStream<Path> list = Files.newDirectoryStream(srcDir)) {
            for (Path file : list){
                String name = file.getFileName().toString().split("\\.")[0];
                String mName = getMaterialName(name);
                if (mName==null) {nbError++; continue;}
                byte face = getFace(name,mName);
                if (face==-1) {nbError++; continue;}
                int color = 0;
                try {
                    color = getAverageColor(ImageIO.read(file.toFile()));
                } catch (IOException e) {
                    PixelArtisan.LOGGER.error("Failed get average color of texture {}", mName, e);
                }
                Material material = Material.matchMaterial(mName);
                if (material==null || !material.isBlock()) continue;
                short mID = (short) material.ordinal();

                int[] faceGoods;
                if (face==0) faceGoods = new int[]{0,1,2,3,4,5};
                else if (face==7) faceGoods = new int[]{1,2,3,4};
                else faceGoods = new int[]{face-1};
                for (int i : faceGoods) treeList.get(i).putIfAbsent(color, mID);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ChatUtils.sendConsoleMessage(nbError+" error(s) during processing");
        return treeList;
    }
}
