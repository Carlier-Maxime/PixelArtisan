package fr.metouais.pixelartisan.Utils;

import fr.metouais.pixelartisan.data.Acess;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

    private static final String path = "plugins/PixelArtisan/data";
    private static ArrayList<TreeMap<Integer,Short>> db = null;

    private FileChannel f;
    private ByteBuffer buf;
    private CommandSender sender;

    public DataManager(CommandSender sender) {
        this.sender = sender;
        if (db==null) loadData();
        this.f = null;
        this.buf = ByteBuffer.allocate(Element.BYTES);
    }

    private void writeOneData(Element e){
        try {
            buf.clear();
            buf.putInt(e.color);
            buf.putShort(e.mID);
            buf.flip();
            while (buf.hasRemaining()) f.write(buf);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private Element readOneData(){
        try {
            buf.clear();
            while (buf.hasRemaining()){
                if (f.read(buf)==-1) return null;
            }
            buf.flip();
            return new Element(buf.getInt(), buf.getShort());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private int getNbData(Path path){
        try {
            return (int) (Files.size(path)/Element.BYTES);
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public void compareAndSave(ArrayList<TreeMap<Integer,Short>> data){
        try {
            sender.sendMessage("§ecompare data with default data..");
            int nbAdd=0;
            for (int i=0; i<6; i++) {
                if (db==null) break;
                if (i >= data.size() || data.get(i).size() >= db.get(i).size()) continue;
                for (int key : db.get(i).keySet()){
                    boolean found = false;
                    for (int k : data.get(i).keySet()){
                        if (k==key){
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
            sender.sendMessage("§e"+nbAdd+" missing data have been added");
            saveCustomData(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveCustomData(ArrayList<TreeMap<Integer,Short>> data){
        // delete old custom data
        File[] dataFiles = new File(path).listFiles();
        if (dataFiles!=null){
            for (File file : dataFiles) file.delete();
        }
        // save custom data
        sender.sendMessage("§esave custom data...");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sender.sendMessage("§adata saved");
    }

    public void loadData(boolean custom){
        try {
            db = new ArrayList<>();
            for (int i=0; i<6; i++){
                File file;
                if (!custom) {
                    URL url = Acess.class.getResource("data" + i + ".dat");
                    if (url==null) {sender.sendMessage("§c[PixelArtisan] INTERNAL ERROR : file not found in the plugin !"); continue;}
                    file = new File(url.toURI());
                } else file = new File(path+"/custom"+i+".dat");
                db.add(new TreeMap<>());
                f = FileChannel.open(
                        FileSystems.getDefault().getPath(file.getAbsolutePath()),
                        StandardOpenOption.READ,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE
                );
                f.position(0);
                Element e;
                while ((e=readOneData())!=null){
                    db.get(i).put(e.color,e.mID);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadData(){
        loadData(false);
    }

    public short getBestMaterial(int color){
        return 350;
    }
}
