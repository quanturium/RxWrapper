package com.quanturium.android.library.rxwrapper;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;

public class WrapperMaybeTransformer<T, C> implements MaybeTransformer<T, T> {

	private final WrapperMaybeCallback<T, C> callback;

	public WrapperMaybeTransformer(WrapperMaybeCallback<T, C> callback) {
		this.callback = callback;
	}

	@Override
	public MaybeSource<T> apply(Maybe<T> upstream) {
		return new WrapperMaybe<>(upstream, callback);
	}
}
