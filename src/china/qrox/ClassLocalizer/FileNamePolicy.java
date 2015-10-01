package china.qrox.ClassLocalizer;

public final class FileNamePolicy {

    public FileNamePolicy(int policy) {
        changePolicy(policy);
    }

    public void changePolicy(int policy) {
        if (policy < 0 || policy > 1) {
            throw new IllegalArgumentException("无效的数值!");
        }
        this.policy = policy;
    }
    public int policy;
    public static final int SHOW_SIMPLE_NAME = 0,
            SHOW_FULL_NAME = 1;
}