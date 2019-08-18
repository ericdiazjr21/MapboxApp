package ericdiaz.program.gotennachallenge.view.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    private TextView placeNameTextView;
    private TextView placeDescriptionTextView;
    private TextView moreInfoTextView;
    private OnItemViewClickedListener onItemViewClickedListener;

    PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        placeNameTextView = itemView.findViewById(R.id.place_name_text_view);
        placeDescriptionTextView = itemView.findViewById(R.id.place_description_text_view);
        moreInfoTextView = itemView.findViewById(R.id.more_info_text_view);

        onItemViewClickedListener =
          itemView.getContext() instanceof OnItemViewClickedListener ?
            (OnItemViewClickedListener) itemView.getContext() : null;
    }

    void onBind(@NonNull final Place place) {
        placeNameTextView.setText(place.getName());
        placeDescriptionTextView.setText(place.getDescription());

        moreInfoTextView.setOnClickListener(v ->
          onItemViewClickedListener.onItemViewClicked(place.getId() - 1));
    }

    public interface OnItemViewClickedListener {
        void onItemViewClicked(int position);
    }
}
