package ericdiaz.program.gotennachallenge.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;

/**
 * RecyclerView Adapter implementation for showing list of places
 * <p>
 * Created 8/15/19
 *
 * @author Eric Diaz
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private Place[] places = new Place[0];

    //==============================================================================================
    // RecyclerView Adapter Interface Methods Implementation
    //==============================================================================================

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_item_view, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.onBind(places[position]);
    }

    @Override
    public int getItemCount() {
        return places.length;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public void setData(Place[] places) {
        this.places = places;
        notifyDataSetChanged();
    }
}
