package com.example.raghuveer.georeference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        Picasso.with(getContext()).load(place.getIcon()).into(place_icon);
        TextView placeView = (TextView) convertView.findViewById(R.id.textView_place_name);
        placeView.setText(place.getName());
        final TextView get_directions = (TextView) convertView.findViewById(R.id.textView_getDirections);
        get_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+place.getLatitude()+","+place.getLongitude()));
                get_directions.getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
