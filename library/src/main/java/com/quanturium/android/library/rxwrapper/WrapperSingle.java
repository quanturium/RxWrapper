package com.quanturium.android.library.rxwrapper;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

public final class WrapperSingle<T, C> extends Single<T> {

	private final Single<T> source;
	private final WrapperSingleCallback<T, C> callback;

	public WrapperSingle(Single<T> source, WrapperSingleCallback<T, C> callback) {
		this.source = ObjectHelper.requireNonNull(source, "source is null");
		this.callback = ObjectHelper.requireNonNull(callback, "callback is null");
	}

	@Override
	protected void subscribeActual(SingleObserver<? super T> observer) {
		source.subscribe(new WrapperSingleObserver<>(observer, callback));
	}

	static final class WrapperSingleObserver<T, C> implements SingleObserver<T>, Disposable {

		private final SingleObserver<? super T> downstream;
		private final WrapperSingleCallback<T, C> callback;
		private Disposable d;
		private C item;

		public WrapperSingleObserver(SingleObserver<? super T> downstream, WrapperSingleCallback<T, C> callback) {
			this.downstream = downstream;
			this.callback = callback;
		}

		@Override
		public void onSubscribe(Disposable d) {
			if (DisposableHelper.validate(this.d, d)) {
				this.d = d;

				item = callback.onSubscribe();
				downstream.onSubscribe(this);
			}
		}

		@Override
		public void onSuccess(T value) {
			try {
				callback.onEvent(value, null, item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				downstream.onError(ex);
				return;
			}

			downstream.onSuccess(value);
		}

		@Override
		public void onError(Throwable e) {
			try {
				callback.onEvent(null, e, item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				e = new CompositeException(e, ex);
			}
			downstream.onError(e);
		}

		@Override
		public void dispose() {
			try {
				callback.onDispose(item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				RxJavaPlugins.onError(ex);
			}
			d.dispose();
		}

		@Override
		public boolean isDisposed() {
			return d.isDisposed();
		}
	}
}
