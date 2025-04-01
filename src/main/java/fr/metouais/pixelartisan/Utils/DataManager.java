package fr.metouais.pixelartisan.Utils;

import fr.metouais.pixelartisan.PixelArtisan;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

public class DataManager {
    private static class Element{
        public static final int BYTES = Integer.BYTES+Short.BYTES;

        public int color;
        public short mID;

        public Element(int color, short mID) {
            this.color = color;
            this.mID = mID;
        }
    }

    private static final Path path = Path.of("plugins/PixelArtisan/data");
    private static ArrayList<TreeMap<Integer,Short>> db = null;

    private FileChannel f;
    private final ByteBuffer buf;
    private final CommandSender sender;
    private InputStream in;

    public DataManager(CommandSender sender) {
        this.sender = sender;
        this.buf = ByteBuffer.allocate(Element.BYTES);
        if (db==null) loadData();
        this.f = null;
        this.in = null;
    }

    private void writeOneData(Element e){
        try {
            buf.clear();
            buf.putInt(e.color);
            buf.putShort(e.mID);
            buf.flip();
            while (buf.hasRemaining()) if (f.write(buf) <= 0) throw new IOException("write failed");
        } catch (Exception exception){
            PixelArtisan.LOGGER.error("Failed write Element", exception);
        }
    }

    private Element readOneData(boolean custom){
        try {
            byte[] buffer = new byte[Element.BYTES];
            buf.clear();
            while (buf.hasRemaining()){
                if (custom) {if (f.read(buf)==-1) return null;}
                else{
                    if (in.read(buffer)==-1) return null;
                    buf.put(buffer);
                }
            }
            buf.flip();
            return new Element(buf.getInt(), buf.getShort());
        } catch (Exception e){
            ChatUtils.sendMessage(sender,"§cError in readOneData");
            PixelArtisan.LOGGER.error("Failed readOneData", e);
        }
        return null;
    }

    public void compareAndSave(ArrayList<TreeMap<Integer,Short>> data){
        try {
            ChatUtils.sendMessage(sender,"§eloading default data..");
            loadData();
            ChatUtils.sendMessage(sender,"§edefault data loaded");
            ChatUtils.sendMessage(sender,"§ecompare data with default data..");
            int nbAdd=0;
            for (int i=0; i<6; i++) {
                if (db==null || db.size()<6) break;
                if (i >= data.size() || data.get(i).size() >= db.get(i).size()) continue;
                for (int key : db.get(i).keySet()){
                    boolean found = false;
                    for (int k : data.get(i).keySet()){
                        if (Objects.equals(db.get(i).get(k), db.get(i).get(key))){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        data.get(i).put(key,db.get(i).get(key));
                        nbAdd++;
                    }
                }
            }
            ChatUtils.sendMessage(sender,"§e"+nbAdd+" missing data have been added");
            saveCustomData(data);
        } catch (Exception e){
            ChatUtils.sendMessage(sender,"§cERROR in compareAndSave");
            PixelArtisan.LOGGER.error("Failed compare and save", e);
        }
    }

    private void saveCustomData(ArrayList<TreeMap<Integer,Short>> data){
        // delete old custom data
        FileUtils.tryDeleteContentOfFolder(path);
        // save custom data
        ChatUtils.sendMessage(sender,"§esave custom data...");
        for (int i=0; i<6; i++){
            try {
                f = FileChannel.open(
                        FileSystems.getDefault().getPath(path+"/custom"+i+".dat"),
                        StandardOpenOption.READ,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE
                );
                for (int k : data.get(i).keySet()){
                    writeOneData(new Element(k,data.get(i).get(k)));
                }
                f.close();
            } catch (IOException e) {
                PixelArtisan.LOGGER.error("Failed save custom data of face {}", i, e);
            }
        }
        ChatUtils.sendMessage(sender,"§adata saved");
    }

    public void loadData(boolean custom){
        try {
            db = new ArrayList<>();
            for (int i=0; i<6; i++){
                Path file;
                if (!custom) {
                    in = PixelArtisan.getInstance().getResource("data/data" + i + ".dat");
                    if (in==null) {ChatUtils.sendMessage(sender,"§cINTERNAL ERROR : file not found in the plugin !"); continue;}
                } else {
                    file = Path.of(path+"/custom"+i+".dat");
                    f = FileChannel.open(
                            FileSystems.getDefault().getPath(file.toFile().getAbsolutePath()),
                            StandardOpenOption.READ,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.CREATE
                    );
                    f.position(0);
                }
                db.add(new TreeMap<>());
                Element e;
                while ((e=readOneData(custom))!=null){
                    db.get(i).put(e.color,e.mID);
                }
            }
        } catch (Exception e){
            ChatUtils.sendMessage(sender,"§cINTERNAL ERROR : load data failed !!");
            PixelArtisan.LOGGER.error("Failed load data", e);
        }
    }

    public void loadData(){
        loadData(false);
    }

    public short getBestMaterial(int colorObjectif, byte face, boolean flat){
        TreeMap<Integer,Short> tree = db.get(face);
        Color goal = new Color(colorObjectif,true);
        Color bestColor = new Color(tree.firstKey(),true);
        for (int clr : tree.keySet()){
            Color color = new Color(clr,true);
            Color tmp = getBestMatchColor(goal,bestColor,color);
            Material m = Material.values()[tree.get(tmp.getRGB())];
            if (!m.isOccluding() && bestColor.getAlpha()==255) continue;
            if (flat && m.hasGravity()) continue;
            bestColor = tmp;
        }
        return tree.get(bestColor.getRGB());
    }

    private static Color getBestMatchColor(Color goal, Color c1, Color c2){
        int a = goal.getAlpha();
        if (Math.abs(c1.getAlpha()-a)<Math.abs(c2.getAlpha()-a)) return c1;
        if (Math.abs(c1.getAlpha()-a)>Math.abs(c2.getAlpha()-a)) return c2;

        int r = goal.getRed();
        int g = goal.getGreen();
        int b = goal.getBlue();
        double diffC1 = Math.sqrt(Math.pow((c1.getRed()-r),2)+Math.pow((c1.getGreen()-g),2)+Math.pow((c1.getBlue()-b),2));
        double diffC2 = Math.sqrt(Math.pow((c2.getRed()-r),2)+Math.pow((c2.getGreen()-g),2)+Math.pow((c2.getBlue()-b),2));
        if (diffC1<diffC2) return c1;
        if (diffC1>diffC2) return c2;
        return c1;
    }
}
