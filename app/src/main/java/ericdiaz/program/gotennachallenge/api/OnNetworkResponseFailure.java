package ericdiaz.program.gotennachallenge.api;

import androidx.annotation.NonNull;

public interface OnNetworkResponseFailure {
    void onFailure(@NonNull final Throwable throwable);

}
