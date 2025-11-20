package com.example.uidesign.network.callbacks;

public interface APICallbacks<T> {

    void onSuccess(T response);

    void onError(Throwable t);
}
