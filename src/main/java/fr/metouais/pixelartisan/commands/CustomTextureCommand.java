package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.Utils.DataManager;
import fr.metouais.pixelartisan.Utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class CustomTextureCommand extends MyCommand{
    private CommandSender sender;
    private DirectoryStream<Path> list;
    private ArrayList<TreeMap<Integer,Short>> treeList;
    private DataManager dataManager;

    public CustomTextureCommand() {
        sender=null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.sender = sender;
        if (args.length<1){
            ChatUtils.sendMessage(sender,"§cmissing argument !");
            ChatUtils.sendMessage(sender,"§c/pa create [generate|disable|enable]");
            return false;
        }
        dataManager = new DataManager(sender);
        return switch (args[0]){
            case "generate" -> generate();
            case "disable" -> disable();
            case "enable" -> enable();
            default -> false;
        };
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.sender = sender;
        if (args.length==1) return List.of("generate","disable","enable");
        else return null;
    }

    private boolean generate(){
        if(!FileUtils.isFolderNotEmpty(PixelArtisan.PATH_CUSTOM_TEXTURE)) {
            ChatUtils.sendMessage(sender, "§ccustom_texture folder is empty or invalid ! (fill the folder and retry)");
            if (sender instanceof Player) {
                ChatUtils.sendMessage(sender, "§6For more information: " + PixelArtisan.GIT_LINK);
            }
            return false;
        }
        checkAndDelUselessFile();
        int nbError = dataProcessing();
        ChatUtils.sendMessage(sender,"§ecompare and save...");
        dataManager.compareAndSave(treeList);
        enable();
        ChatUtils.sendMessage(sender,"§ecleanup of custom_texture folder");
        if (nbError==0) {FileUtils.tryDeleteContentOfFolder(PixelArtisan.PATH_CUSTOM_TEXTURE); ChatUtils.sendMessage(sender,"§acleanup finish");}
        else ChatUtils.sendMessage(sender,"§6cleanup of custom_texture folder canceled because processing errors occurred");
        ChatUtils.sendMessage(sender,"§2custom textures have been supported.");
        return true;
    }

    private boolean disable(){
        ChatUtils.sendMessage(sender,"§edisabling custom data..");
        dataManager.loadData(false);
        ChatUtils.sendMessage(sender,"§2disable.");
        return true;
    }

    private boolean enable(){
        ChatUtils.sendMessage(sender,"§eenabling custom data..");
        dataManager.loadData(true);
        ChatUtils.sendMessage(sender,"§2enable.");
        return true;
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

    private String getMaterialName(String textureName){
        String name = textureName.split("_top")[0];
        name = name.split("_side")[0];
        name = name.split("_bottom")[0];
        name = name.split("_front")[0];
        name = name.split("_down")[0];
        name = name.split("_up")[0];
        name = name.split("_base")[0];
        name = name.split("_lit")[0];
        name = name.split("_stage")[0];
        name = name.split("_overlay")[0];
        name = name.split("_back")[0];
        name = name.split("_on")[0];
        name = name.split("_off")[0];
        name = name.split("_inside")[0];
        name = name.split("_outside")[0];
        name = name.split("_moist")[0];
        name = name.split("_inverted")[0];
        name = name.split("_inner")[0];
        if (name.contains("_pot")){
            if (!name.equals("flower_pot")) name = "potted_" + name.split("_pot")[0];
        }
        for (String s : new String[]{"turtle_egg","structure_block","small_dripleaf","jigsaw","grindstone","frosted_ice","campfire","beehive"}){
            if (name.contains(s)) {name = s; break;}
        }
        if (name.contains("redstone_dust")) name = "redstone_wire";
        if (name.contains("rail_corner")) name = "rail";
        if (name.contains("sticky")) name = "sticky_piston";
        if (name.equals("big_dripleaf_tip")) name = "big_dripleaf";
        else if (name.contains("piston")) name = "piston";
        if (name.equals("mushroom_block")) name = "brown_"+name;
        name = name.toUpperCase(Locale.ROOT);
        Material m = Material.matchMaterial(name);
        if (m==null) {
            ChatUtils.sendMessage(sender,"§c"+textureName+" alias "+name+" not found correspondance !");
            return null;
        }
        return m.name();
    }

    private byte getFace(String name, String mName){
        String[] suffixs = name.split(mName.toLowerCase(Locale.ROOT));
        if (suffixs.length<=1) return 0;
        String suffix = suffixs[1];
        if (suffix.length()<=2) return 0;
        String[] faceSuffixs = new String[]{"_top","_front","$$$$","_back","$$$$","_bottom","_side"};
        for (int i=0; i<faceSuffixs.length; i++){
            if (suffix.contains(faceSuffixs[i])) return (byte) (i+1);
        }
        if (suffix.contains("_inner")){
            if (mName.contains("CAULDRON")) return 1;
            else return 7;
        }
        if (mName.contains("GRINDSTONE")) return 0;
        if (suffix.contains("_end")) return 6;
        if (suffix.contains("_tip")) return 7;

        /* ligne of code for test full know texture support
        for (String faceSuffix : new String[]{"_lit","_down","_up","_stage","_overlay","_cracked","_on","_off","_save","_load","_data","_corner","_base","_inside","_outside","_lock"}) {
            if (suffix.contains(faceSuffix)) return 0;
        }
        ChatUtils.sendMessage(sender,"§c"+name+": suffix = "+suffix+" not found face correspondance !");
        return -1;*/
        return 0;
    }

    private void checkAndDelUselessFile(){
        ChatUtils.sendMessage(sender,"§echecking texture and delete unnecessary files...");
        int nbDelete=0;
        for (Path file : list){
            if (!Files.isRegularFile(file)) {FileUtils.tryDelete(file); nbDelete++;}
            String[] nameSplit = file.getFileName().toString().split("\\.");
            if (nameSplit[nameSplit.length-1].equals("mcmeta")){
                FileUtils.tryDelete(file);
                FileUtils.tryDelete(Path.of(PixelArtisan.PATH_CUSTOM_TEXTURE+"/"+nameSplit[0]+".png"));
                nbDelete+=2;
            } else if (!nameSplit[nameSplit.length-1].equals("png")) {FileUtils.tryDelete(file); nbDelete++;}
            for (String s : new String[]{"destroy","_plant","grass","end_portal","composter","debug","chorus","bamboo","farmland","campfire","shulker_box","coral"}){
                if (nameSplit[0].contains(s)) {FileUtils.tryDelete(file); nbDelete++;}
            }
        }
        ChatUtils.sendMessage(sender,"§e"+nbDelete+" files have been deleted");
    }

    private int dataProcessing(){
        ChatUtils.sendMessage(sender,"§edata processing...");
        treeList = new ArrayList<>(6);
        for (int i=0; i<6; i++) treeList.add(new TreeMap<>());
        try {
            list = Files.newDirectoryStream(PixelArtisan.PATH_CUSTOM_TEXTURE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int nbError=0;
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
        if (nbError>0) ChatUtils.sendMessage(sender,"§cnb error processing = "+nbError);
        else ChatUtils.sendMessage(sender,"§ano process error detected");
        return nbError;
    }
}
