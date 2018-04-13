package com.quanturium.android.library.rxwrapper;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

public final class WrapperCompletable<C> extends Completable {

	private final Completable source;
	private final WrapperCompletableCallback<C> callback;

	public WrapperCompletable(Completable source, WrapperCompletableCallback<C> callback) {
		this.source = ObjectHelper.requireNonNull(source, "source is null");
		this.callback = ObjectHelper.requireNonNull(callback, "callback is null");
	}

	@Override
	protected void subscribeActual(CompletableObserver observer) {
		source.subscribe(new WrapperSingleObserver<>(observer, callback));
	}

	static final class WrapperSingleObserver<C> implements CompletableObserver, Disposable {

		private final CompletableObserver downstream;
		private final WrapperCompletableCallback<C> callback;
		private Disposable d;
		private C item;

		public WrapperSingleObserver(CompletableObserver downstream, WrapperCompletableCallback<C> callback) {
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
		public void onComplete() {
			try {
				callback.onEvent(null, item);
			} catch (Throwable ex) {
				Exceptions.throwIfFatal(ex);
				downstream.onError(ex);
				return;
			}

			downstream.onComplete();
		}

		@Override
		public void onError(Throwable e) {
			try {
				callback.onEvent(e, item);
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
