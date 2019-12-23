package ru.geekbrains.gb_android_libraries.ui.image;

import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import ru.geekbrains.gb_android_libraries.mvp.model.image.IImageLoader;

public class PicassoImageLoader implements IImageLoader<ImageView> {
    @Override
    public void loadInto(String url, ImageView container) {
        Picasso.get().load(url).into(container);
    }
}
