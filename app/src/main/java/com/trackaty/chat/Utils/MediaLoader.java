package com.trackaty.chat.Utils;


import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

public class MediaLoader implements AlbumLoader {

    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        Picasso.get()
                .load(url)
                .placeholder(R.mipmap.ic_picture_gallery_white_512px)
                .error(R.drawable.ic_broken_image_512px)
                .into(imageView);
    }
}