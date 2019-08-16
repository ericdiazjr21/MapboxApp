package ericdiaz.program.gotennachallenge.view.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;

class PlacesViewHolder extends RecyclerView.ViewHolder {

    private TextView placeNameTextView;
    private TextView placeDescriptionTextView;

    PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        placeNameTextView = itemView.findViewById(R.id.place_name_text_view);
        placeDescriptionTextView = itemView.findViewById(R.id.place_description_text_view);
    }

    void onBind(@NonNull final Place place) {
        placeNameTextView.setText(place.getName());
        placeDescriptionTextView.setText(place.getDescription());
    }
}
