package com.example.ActivityRemainder.georeference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by raghuveer on 11/24/2015.
 */
public class PlacesAdapter extends ArrayAdapter<Place>{
    Context mContext;
    List<Place> placeList;
    Place place;

    public PlacesAdapter(Context context, List<Place> objects) {
        super(context, R.layout.place_layout, objects);
        mContext = context;
        placeList = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.place_layout, null);
        }

        place = placeList.get(position);
        ImageView place_icon = (ImageView) convertView.findViewById(R.id.imageView_icon);
        Picasso.with(getContext()).load(place.getIcon()).resize(100,100).centerInside().transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int size = Math.min(source.getWidth(), source.getHeight());

                int x = (source.getWidth() - size) / 2;
                int y = (source.getHeight() - size) / 2;

                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                if (squaredBitmap != source) {
                    source.recycle();
                }

                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                paint.setShader(shader);
                paint.setAntiAlias(true);

                float r = size / 2f;
                canvas.drawCircle(r, r, r, paint);

                squaredBitmap.recycle();
                return bitmap;
            }

            @Override
            public String key() {
                return "circle";
            }
        }).into(place_icon);
        TextView placeView = (TextView) convertView.findViewById(R.id.textView_place_name);
        placeView.setText(place.getName());
        final TextView get_directions = (TextView) convertView.findViewById(R.id.textView_getDirections);
        return convertView;
    }
}
