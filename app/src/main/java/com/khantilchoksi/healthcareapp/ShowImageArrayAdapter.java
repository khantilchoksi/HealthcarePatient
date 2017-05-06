package com.khantilchoksi.healthcareapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Khantil on 28-05-2016.
 */
public class ShowImageArrayAdapter extends ArrayAdapter<String> {

    Context mContext;

    public ShowImageArrayAdapter(Activity context, List<String> imagePaths) {
        super(context, 0, imagePaths);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String fileName = getItem(position);


        String clientIssueImagePath = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_image, parent, false);
        }

        final ImageView thumbImage = (ImageView) convertView.findViewById(R.id.attachment_image_item);

        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //change size
                //Bitmap newBitmap = scaleBitmap(bitmap,100,100);
                Log.d("KhantilImageProcessing:","onBitmapLoaded: "+getItem());
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                thumbImage.setImageBitmap(resized);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };*/

        Picasso.with(mContext).load(clientIssueImagePath).
                into(thumbImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Success","Image loaded successfully.");
//                mIssueImagesPagerActivityFragment.showProgress(false);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext,"Issue downloading image!",Toast.LENGTH_SHORT).show();
                    }
                });

        //Picasso.with(getContext()).load(clientIssueImagePath).into(target);

        return convertView;
    }

    /*public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }*/

    /*public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }*/
}
