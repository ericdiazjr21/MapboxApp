package ericdiaz.program.gotennachallenge.api;

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
