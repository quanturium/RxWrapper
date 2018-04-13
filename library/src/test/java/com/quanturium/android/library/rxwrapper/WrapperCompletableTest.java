package com.quanturium.android.library.rxwrapper;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;

public class WrapperCompletableTest {

	@Test
	public void complete() {

		Integer object = 12; // random number

		Completable completable = Completable.complete()
				.compose(new WrapperCompletableTransformer<>(new WrapperCompletableCallback<Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(Throwable e, Integer item) {
						assertEquals(12, item.intValue());
					}

					@Override
					public void onDispose(Integer item) {
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		completable.subscribe(subscriber1);
		subscriber1.assertComplete();
		subscriber1.dispose();

	}

	@Test
	public void error() {

		Integer object = 12; // random number

		Completable completable = Completable.<String>error(new IllegalStateException())
				.compose(new WrapperCompletableTransformer<>(new WrapperCompletableCallback<Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(Throwable e, Integer item) {
						assertEquals(12, item.intValue());
					}

					@Override
					public void onDispose(Integer item) {
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		completable.subscribe(subscriber1);
		subscriber1.assertError(IllegalStateException.class);
		subscriber1.dispose();
	}

	@Test
	public void dispose() {

		Integer object = 12; // random number

		Completable completable = Completable.complete()
				.delay(1, TimeUnit.SECONDS, Schedulers.newThread())
				.compose(new WrapperCompletableTransformer<>(new WrapperCompletableCallback<Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(Throwable e, Integer item) {
						assertEquals(12, item.intValue());
					}

					@Override
					public void onDispose(Integer item) {
						assertEquals(12, item.intValue());
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		completable.subscribe(subscriber1);
		subscriber1.dispose();
		subscriber1.assertNotComplete();
	}
}
