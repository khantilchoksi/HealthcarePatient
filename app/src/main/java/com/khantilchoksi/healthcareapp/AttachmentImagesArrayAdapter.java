package com.khantilchoksi.healthcareapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Khantil on 26-05-2016.
 */
public class AttachmentImagesArrayAdapter extends ArrayAdapter<Bitmap> {

    public AttachmentImagesArrayAdapter(Activity context, List<Bitmap> bitmapsList) {
        super(context, 0, bitmapsList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Bitmap originalBitmap = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_image, parent, false);
        }

        final ImageView thumbImage = (ImageView) convertView.findViewById(R.id.attachment_image_item);
        final int THUMBSIZE = 120;

        Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(originalBitmap,
                THUMBSIZE, THUMBSIZE);
        thumbImage.setImageBitmap(thumbBitmap);

        final ImageButton removeImageButton = (ImageButton) convertView.findViewById(R.id.deleteImageButton);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(getItem(position));
                thumbImage.clearColorFilter();
                removeImageButton.setVisibility(View.INVISIBLE);
            }
        });

        return convertView;
    }


    //Following method is used to convert the bitmap image to Base 64 and encoded string
    public String getStringImage(int position){
        Bitmap bmp = getItem(position);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);     // Half of the actual quality
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
