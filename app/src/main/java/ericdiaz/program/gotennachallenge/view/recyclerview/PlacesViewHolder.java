package ericdiaz.program.gotennachallenge.view.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;

/**
 * RecyclerView ViewHolder Implementation for showing list of places
 * <p>
 * Created: 8/15/19
 *
 * @author Eric Diaz
 */

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private TextView placeNameTextView;
    private TextView placeDescriptionTextView;
    private TextView moreInfoTextView;
    private OnItemViewClickedListener onItemViewClickedListener;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews(itemView);
        initOnItemViewClickedListener(itemView);
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private void findViews(@NonNull View itemView) {
        placeNameTextView = itemView.findViewById(R.id.place_name_text_view);
        placeDescriptionTextView = itemView.findViewById(R.id.place_description_text_view);
        moreInfoTextView = itemView.findViewById(R.id.more_info_text_view);
    }

    private void initOnItemViewClickedListener(@NonNull View itemView) {
        onItemViewClickedListener =
          itemView.getContext() instanceof OnItemViewClickedListener ?
            (OnItemViewClickedListener) itemView.getContext() : null;
    }

    void onBind(@NonNull final Place place) {
        placeNameTextView.setText(place.getName());
        placeDescriptionTextView.setText(place.getDescription());

        moreInfoTextView.setOnClickListener(v ->
          onItemViewClickedListener.onItemViewClicked(place));
    }

    //==============================================================================================
    // Class Interface Methods
    //==============================================================================================

    public interface OnItemViewClickedListener {
        void onItemViewClicked(Place place);
    }
}
