package ericdiaz.program.gotennachallenge.data.api.interfaces;

import androidx.annotation.NonNull;

/**
 * A Interface for communicating network failure response
 * <p>
 * Created: 8/16/19
 *
 * @author Eric Diaz
 */

public interface OnNetworkResponseFailure {
    void onFailure(@NonNull final Throwable throwable);
}
