package uk.co.perspective.app.objects;

import android.graphics.Bitmap;

public class FileItem {

    private int id;
    private Bitmap image;
    private String title;
    private String filePath;

    public FileItem(int id, Bitmap image, String title, String FilePath) {
        super();
        this.id = id;
        this.image = image;
        this.title = title;
        this.filePath = FilePath;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
