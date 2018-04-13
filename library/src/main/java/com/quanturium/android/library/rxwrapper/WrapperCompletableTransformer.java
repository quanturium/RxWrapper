package com.quanturium.android.library.rxwrapper;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;

public class WrapperCompletableTransformer<C> implements CompletableTransformer {

	private final WrapperCompletableCallback<C> callback;

	public WrapperCompletableTransformer(WrapperCompletableCallback<C> callback) {
		this.callback = callback;
	}

	@Override
	public CompletableSource apply(Completable upstream) {
		return new WrapperCompletable<>(upstream, callback);
	}
}
