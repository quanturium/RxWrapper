package com.quanturium.android.library.rxwrapper;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class WrapperSingleTransformer<T, C> implements SingleTransformer<T,T> {

	private final WrapperSingleCallback<T, C> callback;

	public WrapperSingleTransformer(WrapperSingleCallback<T, C> callback) {
		this.callback = callback;
	}

	@Override
	public SingleSource<T> apply(Single<T> upstream) {
		return new WrapperSingle<>(upstream, callback);
	}
}
