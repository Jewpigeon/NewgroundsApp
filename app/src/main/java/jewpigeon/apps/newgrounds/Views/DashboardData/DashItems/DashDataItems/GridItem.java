package jewpigeon.apps.newgrounds.Views.DashboardData.DashItems.DashDataItems;

import android.graphics.drawable.Drawable;


public class GridItem {

    private Drawable image;
    private String title;
    private String author;

    public GridItem(Drawable image, String title, String author) {
        if(image == null){

        }
        this.image = image;
        this.title = title;
        this.author = author;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


}
