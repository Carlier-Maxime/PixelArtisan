package fr.metouais.pixelartisan.Utils;

import org.bukkit.command.CommandSender;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public DataManager() {
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

    public void compareAndSave(CommandSender sender, ArrayList<TreeMap<Integer,Short>> data){
        try {
            sender.sendMessage("§ecompare data with default data...");
            int nbAdd=0;
            for (int i=0; i<6; i++) {
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

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveCustomData(CommandSender sender, ArrayList<TreeMap<Integer,Short>> data){

    }
}
