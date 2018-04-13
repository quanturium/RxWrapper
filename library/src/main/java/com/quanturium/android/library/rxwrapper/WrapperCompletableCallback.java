package com.quanturium.android.library.rxwrapper;

public interface WrapperCompletableCallback<C> {

	/**
	 * Called when the observer subscribes to the source Completable and allows you to
	 * pass an object to the {@link #onEvent(Throwable, Object)}
	 * and {@link #onDispose(Object)} methods
	 */
	C onSubscribe();

	/**
	 * Called when the source Completable emits onComplete() or onError(). It includes the
	 * object that was returned during the {@link #onSubscribe()} call
	 */
	void onEvent(Throwable e, C item);

	/**
	 * Called when the observer disposes the source Completable it was subscribed to. It includes an
	 * object that was returned during the {@link #onSubscribe} call
	 */
	void onDispose(C item);

}
