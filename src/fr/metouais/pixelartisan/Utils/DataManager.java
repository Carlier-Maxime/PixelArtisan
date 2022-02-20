package fr.metouais.pixelartisan.Utils;

import java.io.File;
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

    private ArrayList<TreeMap<Integer,Short>> db;
    private FileChannel f;
    private ByteBuffer buf;

    public DataManager() {
        this.db = null;
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
}
