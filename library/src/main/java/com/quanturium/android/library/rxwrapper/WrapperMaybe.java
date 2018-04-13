package com.quanturium.android.library.rxwrapper;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

public final class WrapperMaybe<T, C> extends Maybe<T> {

	private final Maybe<T> source;
	private final WrapperMaybeCallback<T, C> callback;

	public WrapperMaybe(Maybe<T> source, WrapperMaybeCallback<T, C> callback) {
		this.source = ObjectHelper.requireNonNull(source, "source is null");
		this.callback = ObjectHelper.requireNonNull(callback, "callback is null");
	}

	@Override
	protected void subscribeActual(MaybeObserver<? super T> observer) {
		source.subscribe(new WrapperMaybeObserver<>(observer, callback));
	}

	static final class WrapperMaybeObserver<T, C> implements MaybeObserver<T>, Disposable {

		private final MaybeObserver<? super T> downstream;
		private final WrapperMaybeCallback<T, C> callback;
		private Disposable d;
		private C item;

		public WrapperMaybeObserver(MaybeObserver<? super T> downstream, WrapperMaybeCallback<T, C> callback) {
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

			d = DisposableHelper.DISPOSED;

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
			d = DisposableHelper.DISPOSED;

			try {
				callback.onEvent(null, e, item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				e = new CompositeException(e, ex);
			}
			downstream.onError(e);
		}

		@Override
		public void onComplete() {
			d = DisposableHelper.DISPOSED;

			try {
				callback.onEvent(null, null, item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				downstream.onError(ex);
				return;
			}

			downstream.onComplete();
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
			d = DisposableHelper.DISPOSED;
		}

		@Override
		public boolean isDisposed() {
			return d.isDisposed();
		}
	}
}
