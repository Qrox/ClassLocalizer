package china.qrox.ClassLocalizer.classfile;

import china.qrox.ClassLocalizer.classfile.constant.GenericConstant;
import china.qrox.ClassLocalizer.classfile.constant.StringReference;
import china.qrox.ClassLocalizer.classfile.constant.Utf8;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public abstract class Constant {

    protected Constant(int tag) {
        this.tag = tag;
    }

    public static Constant get(DataInputStream s) throws IOException {
        int tag = s.readUnsignedByte();
        Integer len = size.get(tag);
        if (len != null) {
            return new GenericConstant(tag, len, s);
        } else if (tag == 8) {
            return new StringReference(tag, s);
        } else if (tag == 1) {
            return new Utf8(tag, s);
        } else {
            throw new IOException("未知的常量池元素: " + tag);
        }
    }

    public final void save(DataOutputStream s) throws IOException {
        s.writeByte(tag);
        saveConstant(s);
    }

    public abstract void saveConstant(DataOutputStream s) throws IOException;
    private final int tag;
    static final HashMap<Integer, Integer> size;
    
    public int getTag(){
        return tag;
    }

    static {
        size = new HashMap<>();
        size.put(7,  2);//class
        size.put(9,  4);//field
        size.put(10, 4);//method
        size.put(11, 4);//interfacemethod
        size.put(3,  4);//integer
        size.put(4,  4);//float
        size.put(5,  8);//long
        size.put(6,  8);//double
        size.put(12, 4);//nameandtype
        size.put(15, 3);//methodhandle
        size.put(16, 2);//methodtype
        size.put(18, 4);//invokedynamic
    }
}