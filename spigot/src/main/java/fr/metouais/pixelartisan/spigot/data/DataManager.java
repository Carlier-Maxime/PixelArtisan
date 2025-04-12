package fr.metouais.pixelartisan.spigot.data;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.utils.FileUtils;
import fr.metouais.pixelartisan.common.utils.MessageSender;
import fr.metouais.pixelartisan.spigot.utils.Misc;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
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

    public static final String DEFAULT_DATA = "default";
    private static ArrayList<TreeMap<Integer,Short>> db = null;

    private FileChannel f;
    private final ByteBuffer buf;
    private final MessageSender sender;

    public DataManager(MessageSender sender) {
        this.sender = sender;
        this.buf = ByteBuffer.allocate(Element.BYTES);
        if (db==null) {
            try {
                loadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.f = null;
    }

    private void loadData() throws IOException {
        if (FileUtils.isFolderEmpty(PixelArtisan.PATH_DATA.resolve(DEFAULT_DATA))) {
            sender.send( "generate default data...");
            try {
                FileUtils.extractBlockTexturesFromClientMC(Misc.getMCVersion(), PixelArtisan.PATH_INPUT_TEXTURE);
            } catch (Exception e) {
                String msg = "Failed download and extract vanilla block textures for generate default data: "+e.getMessage();
                MessageSender.CONSOLE.send(msg);
                sender.send( "§c INTERNAL ERROR: "+msg);
                throw new RuntimeException(e);
            }
            DataGenerator.generateFromTexturesBlock(sender, PixelArtisan.PATH_INPUT_TEXTURE, DEFAULT_DATA, this);
        }
        sender.send( "load default data...");
        loadData(DEFAULT_DATA);
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

    private Element readOneData(){
        try {
            buf.clear();
            while (buf.hasRemaining()) if (f.read(buf)==-1) return null;
            buf.flip();
            return new Element(buf.getInt(), buf.getShort());
        } catch (Exception e){
            sender.send("§cError in readOneData");
            PixelArtisan.LOGGER.error("Failed readOneData", e);
        }
        return null;
    }

    private void compareAndCompleteWithLoadedData(ArrayList<TreeMap<Integer,Short>> data) {
        try {
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
            sender.send("§e"+nbAdd+" missing data have been added");
        } catch (Exception e){
            sender.send("§cERROR in compareAndSave");
            PixelArtisan.LOGGER.error("Failed compare and save", e);
        }
    }

    public void compareWithDefaultAndSave(ArrayList<TreeMap<Integer,Short>> data, @NotNull String name) throws IOException {
        if (!DEFAULT_DATA.equals(name)) {
            sender.send("§eloading default data..");
            loadData(DEFAULT_DATA);
            sender.send("§edefault data loaded");
            sender.send("§ecompare data with default data..");
            compareAndCompleteWithLoadedData(data);
        }
        saveCustomData(data, name);
    }

    private void saveCustomData(ArrayList<TreeMap<Integer,Short>> data, @NotNull String name){
        sender.send("§esave custom data on "+name+"...");
        Path folder = PixelArtisan.PATH_DATA.resolve(name);
        try {
            Files.createDirectories(folder);
            for (int i=0; i<6; i++){
                f = FileChannel.open(
                        FileSystems.getDefault().getPath(folder+"/data"+i+".dat"),
                        StandardOpenOption.READ,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE
                );
                for (int k : data.get(i).keySet()){
                    writeOneData(new Element(k,data.get(i).get(k)));
                }
                f.close();
            }
        } catch (IOException e) {
            PixelArtisan.LOGGER.error("Failed save custom data of face", e);
            sender.send("§adata save has been failed");
            return;
        }
        sender.send("§adata saved");
    }

    public void loadData(String name) throws IOException {
        db = new ArrayList<>();
        for (int i=0; i<6; i++){
            Path path = PixelArtisan.PATH_DATA.resolve(name).resolve("data"+i+".dat");
            if (!Files.exists(path)) throw new IllegalArgumentException("File '"+path+"' does not exist");
            f = FileChannel.open(
                    path,
                    StandardOpenOption.READ,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE
            );
            f.position(0);
            db.add(new TreeMap<>());
            Element e;
            while ((e=readOneData())!=null){
                db.get(i).put(e.color,e.mID);
            }
        }
    }

    public short getBestMaterial(int colorObjectif, byte face, boolean flat){
        var tree = db.get(face);
        Color goal = new Color(colorObjectif,true);
        Color bestColor = new Color(tree.firstKey(),true);
        for (int clr : tree.keySet()){
            Color color = new Color(clr,true);
            Color tmp = getBestMatchColor(goal,bestColor,color);
            Material m = Misc.MATERIALS[tree.get(tmp.getRGB())];
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
