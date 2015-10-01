package china.qrox.ClassLocalizer.classfile.constant;

import china.qrox.ClassLocalizer.classfile.Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringReference extends Constant {

    public StringReference(int tag, DataInputStream s) throws IOException {
        super(tag);
        ref = s.readUnsignedShort();
    }
    
    public void saveConstant(DataOutputStream s) throws IOException {
        s.writeShort(ref);
    }
    
    public int ref;
}