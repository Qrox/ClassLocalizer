package china.qrox.ClassLocalizer;

import java.util.regex.Pattern;

public class SearchPolicy {

    public SearchPolicy(String text, boolean ignoreCase, int type){
        this.ignoreCase = ignoreCase;
        this.type = type;
		switch (type) {
			case NORMAL:
				if (this.ignoreCase) pattern = text.toLowerCase();
				else pattern = text;
				break;
			case FULLTEXT:
				pattern = text;
				break;
			case WILDCARD:
				throw new UnsupportedOperationException();
			case REGEX:
				pattern = Pattern.compile(text, this.ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
				break;
		}
    }
    public Object pattern;
    public boolean ignoreCase;
    public int type;
    public static final int NORMAL = 0,
            WILDCARD = 1,
            REGEX = 2,
            FULLTEXT = 3;
}
