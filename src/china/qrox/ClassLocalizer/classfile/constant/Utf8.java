package china.qrox.ClassLocalizer.classfile.constant;

import china.qrox.ClassLocalizer.classfile.Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Utf8 extends Constant {

    public Utf8(int tag, DataInputStream s) throws IOException {
        super(tag);
        str = s.readUTF();
    }
    
    public void saveConstant(DataOutputStream s) throws IOException {
        s.writeUTF(str);
    }
    public String str;
}