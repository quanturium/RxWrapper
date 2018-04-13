package com.quanturium.android.library.rxwrapper;

public interface WrapperSingleCallback<T, C> {

	/**
	 * Called when the observer subscribes to the source Single and allows you to
	 * pass an object to the {@link #onEvent(Object, Throwable, Object)}
	 * and {@link #onDispose(Object)} methods
	 */
	C onSubscribe();

	/**
	 * Called when the source Single emits onSuccess() or onError(). It includes the
	 * object that was returned during the {@link #onSubscribe()} call
	 */
	void onEvent(T t, Throwable e, C item);

	/**
	 * Called when the observer disposes the source Single it was subscribed to. It includes an
	 * object that was returned during the {@link #onSubscribe} call
	 */
	void onDispose(C item);
}
