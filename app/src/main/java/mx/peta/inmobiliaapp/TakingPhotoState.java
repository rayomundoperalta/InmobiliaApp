package mx.peta.inmobiliaapp;

/**
 * Created by rayo on 11/8/16.
 */
public class TakingPhotoState {
    private static TakingPhotoState instance = null;

    public static TakingPhotoState getInstance() {
        if (instance == null) {
            instance = new TakingPhotoState();
        }
        return instance;
    }

    private TakingPhotoState() {}

    private static boolean takingPhotoState = false;
    private static String photoFileName = null;

    public boolean getTakingPhotoState() {
        return this.takingPhotoState;
    }

    public void setTakingPhotoState(boolean takingPhotoState) {
        this.takingPhotoState = takingPhotoState;
    }

    public String getPhotoFileName() {
        return this.photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }
}
