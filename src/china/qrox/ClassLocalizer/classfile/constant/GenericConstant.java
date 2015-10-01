package china.qrox.ClassLocalizer.classfile.constant;

import china.qrox.ClassLocalizer.classfile.Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GenericConstant extends Constant {

    public GenericConstant(int tag, int len, DataInputStream s) throws IOException {
        super(tag);
        data = new byte[len];
        s.read(data);
    }

    public void saveConstant(DataOutputStream s) throws IOException {
        s.write(data);
    }
    byte data[];
}