package china.qrox.ClassLocalizer.classfile;

import china.qrox.ClassLocalizer.classfile.constant.StringReference;
import china.qrox.ClassLocalizer.classfile.constant.Utf8;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassResolver {

    private class NotAClassException extends IOException {
    }

    private void init(InputStream ins, long size) throws IOException {
        FileInput in = new FileInput(ins, size);
        try (DataInputStream s = new DataInputStream(in)) {
            if (s.readInt() != 0xCAFEBABE) {
                throw new NotAClassException();
            }
            ver = new byte[4];
            s.readFully(ver);
            constants = new Constant[s.readUnsignedShort()];
            HashSet<Integer> set = new HashSet<>();
            constants[0] = null;//element zero is reserved as null, according to java class file specification
            for (int i = 1; i < constants.length; i++) {
                Constant con = Constant.get(s);
                constants[i] = con;
                if (con instanceof StringReference) {
                    set.add(((StringReference) constants[i]).ref);
                }
                int tag = con.getTag();
                if (tag == 5 || tag == 6) {//long and double values occupy two entries, according to java class file specification
                    constants[++i] = null;
                }
            }
            int len = set.size();
            table = new HashMap<>();
            int i = 0;
            for (Integer ref : set) {
				Utf8 utf8 = (Utf8) constants[ref];
				table.put(utf8.str, utf8);
                i++;
            }
            len = in.bytesRemain();
            remaining = new byte[len];
            s.readFully(remaining);
        } catch (EOFException ex) {
            throw new NotAClassException();
        }
    }

    public ClassResolver(InputStream ins, long size) throws IOException {
        init(ins, size);
    }

    public ClassResolver(File f) throws IOException {
        if (f.isDirectory()) {
            throw new IOException("[" + f.getAbsolutePath() + "] 是目录!");
        }
        try {
            init(new FileInputStream(f), f.length());
        } catch (NotAClassException ex) {
            throw new IOException("文件 [" + f.getAbsolutePath() + "] 不是类文件!");
        }
    }
	
	private static String encode(String s) {
		StringBuilder str = new StringBuilder();
		int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
				case '\n':
					str.append("\\n");
					break;
				case '\r':
					str.append("\\r");
					break;
                case '\\':
                    str.append("\\\\");
                    break;
                default:
                    str.append(ch);
                    break;
            }
        }
		return str.toString();
	}
	
	private static String decode(String s) {
		StringBuilder str = new StringBuilder();
		int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            if (ch == '\\' && i < len - 1) {
				switch (ch = s.charAt(++i)) {
					case 'n' : str.append('\n'); break;
					case 'r' : str.append('\r'); break;
					case '\\': str.append('\\'); break;
					default  : str.append('\\').append(ch); break;
				}
			} else str.append(ch);
        }
		return str.toString();
	}

    public synchronized String[][] getTable() {
		String tbl[][] = new String[table.size()][2];
		int i = 0;
		for (Entry<String, Utf8> e : table.entrySet()) {
			tbl[i][0] = encode(e.getKey());
			tbl[i][1] = encode(e.getValue().str);
			i++;
		}
        return tbl;
    }

    public synchronized void setTable(String[][] t) {
		for (String[] s : t) {
			Utf8 utf8 = table.get(decode(s[0]));
			if (utf8 != null) {
				utf8.str = decode(s[1]);
			}
		}
    }

    public void save(OutputStream out) throws IOException {
        DataOutputStream o = new DataOutputStream(out);
        o.writeInt(0xCAFEBABE);
        o.write(ver);
        o.writeShort(constants.length);
        for (Constant con : constants) {
            if (con != null) {
                con.save(o);
            }
        }
        o.write(remaining);
    }

	private static String Export(String key, String value) {
		return ParseExport(key).append(" = ").append(ParseExport(value)).toString();
	}
	
    private static StringBuilder ParseExport(String s) {
		int len = s.length();
        StringBuilder str = new StringBuilder().append('"');
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
				case '\n':
					str.append("\\n");
					break;
				case '\r':
					str.append("\\r");
					break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '"':
					str.append("\\\"");
					break;
                default:
                    str.append(ch);
                    break;
            }
        }
		return str.append('"');
    }
	
	private static final Matcher m = Pattern.compile(
		"(?:\\s*(\"(?:\\\\.|[^\"])*\")\\s*|([^=]*))=(?:\\s*(\"(?:\\\\.|[^\"])*\")\\s*|(.*))"
	).matcher("");
	//(?:\s*("(?:\\.|[^"])*")\s*|([^=]*))=(?:\s*("(?:\\.|[^"])*")\s*|(.*))
	
	private void Import(String s) {
		int i, len = s.length();
		String key, value;
		if (m.reset(s).matches()) {
			key = m.group(1);
			if (key == null) {
				key = m.group(2);
				if (key == null) return;
				else key = ParseImport(key, false);
			} else key = ParseImport(key, true);
			
			Utf8 utf8 = table.get(key);
			if (utf8 != null) {
				value = m.group(3);
				if (value == null) {
					value = m.group(4);
					if (value == null) return;
					else utf8.str = ParseImport(value, false);
				} else utf8.str = ParseImport(value, true);
			}
		} else return;
	}

    private static String ParseImport(String s, boolean quote) {
        StringBuilder str = new StringBuilder();
		int i, len = s.length();
		if (quote) {
			i = 1;
			len--;
		} else {
			i = 0;
		}
        for (; i < len; i++) {
            char ch = s.charAt(i);
            if (ch == '\\' && i < len - 1) {
                ch = s.charAt(++i);
				switch (ch) {
					case 'n' : str.append('\n'); break;
					case 'r' : str.append('\r'); break;
					case '\\': str.append('\\'); break;
					case '"':
						if (quote) {
							str.append('"');
							break;
						}//else default
					default:
						str.append('\\').append(ch);
				}
            } else str.append(ch);
        }
        return str.toString();
    }

    public void exportText(OutputStream out) throws IOException {
        PrintStream o = new PrintStream(out);
        for (Entry<String, Utf8> e : table.entrySet()) {
			o.println(Export(e.getKey(), e.getValue().str));
        }
    }

    public void importText(InputStream in) throws IOException {
        BufferedReader o = new BufferedReader(new InputStreamReader(in));
        String s;
        while ((s = o.readLine()) != null) {
            Import(s);
        }
    }
    private Constant[] constants;
    private HashMap<String, Utf8> table;
    private byte[] ver;
    private byte[] remaining;

    static class FileInput extends InputStream {

        FileInput(InputStream in, long size) {
            this.in = in;
            remain = size;
        }

        FileInput(File f) throws IOException {
            this(new FileInputStream(f), f.length());
        }

        public int read() throws IOException {
            if (remain <= 0) {
                return -1;
            }
            int ret = in.read();
            if (ret >= 0) {
                remain--;
            }
            return ret;
        }

        public int read(byte[] b) throws IOException {
            int ret = in.read(b);
            if (ret > 0) {
                remain -= ret;
            }
            return ret;
        }

        public int bytesRemain() throws IOException {
            if (remain > Integer.MAX_VALUE) {
                throw new IOException("文件过大");
            }
            return (int) remain;
        }
        private InputStream in;
        private long remain;
    }
	
	public static void main(String... args) {
		BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
		String s, s1;
		while (true) {
			try {
				s = rd.readLine();
				System.out.println("encode>>" + encode(s) + "<<");
				System.out.println("decode>>" + decode(s) + "<<");
				/*s1 = rd.readLine();
				s = Export(s, s1);
				System.out.println(s);*/
				String key, value;
				if (m.reset(s).matches()) {
					key = m.group(1);
					if (key == null) {
						key = m.group(2);
						if (key == null) return;
						else key = ParseImport(key, false);
					} else key = ParseImport(key, true);

					value = m.group(3);
					if (value == null) {
						value = m.group(4);
						if (value == null) continue;
						else value = ParseImport(value, false);
					} else value = ParseImport(value, true);
				} else continue;
				System.out.println(">>" + key + "<<");
				System.out.println(">>" + value + "<<");
				s = Export(key, value);
				System.out.println(s);
				System.out.println();
			} catch (IOException ex) {}
		}
	}
}