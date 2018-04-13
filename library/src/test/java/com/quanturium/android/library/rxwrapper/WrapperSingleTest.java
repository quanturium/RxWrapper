package com.quanturium.android.library.rxwrapper;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WrapperSingleTest {

	@Test
	public void singleItem() {

		String singleItem = "a";
		Integer object = 12; // random number

		Single<String> single = Single.just(singleItem)
				.compose(new WrapperSingleTransformer<>(new WrapperSingleCallback<String, Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(String s, Throwable e, Integer item) {
						assertEquals(12, item.intValue());
					}

					@Override
					public void onDispose(Integer item) {
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		single.subscribe(subscriber1);
		subscriber1.assertValue(singleItem);
		subscriber1.dispose();

	}

	@Test
	public void error() {

		Integer object = 12; // random number

		Single<String> single = Single.<String>error(new IllegalStateException())
				.compose(new WrapperSingleTransformer<>(new WrapperSingleCallback<String, Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(String s, Throwable e, Integer item) {
						assertEquals(12, item.intValue());
					}

					@Override
					public void onDispose(Integer item) {
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		single.subscribe(subscriber1);
		subscriber1.assertError(IllegalStateException.class);
		subscriber1.dispose();
	}

	@Test
	public void dispose() {

		String singleItem = "a";
		Integer object = 12; // random number

		Single<String> single = Single.just(singleItem)
				.delay(1, TimeUnit.SECONDS, Schedulers.newThread())
				.compose(new WrapperSingleTransformer<>(new WrapperSingleCallback<String, Integer>() {
					@Override
					public Integer onSubscribe() {
						return object;
					}

					@Override
					public void onEvent(String s, Throwable e, Integer item) {
						assertTrue(false); // Make sure this is not called
					}

					@Override
					public void onDispose(Integer item) {
						assertEquals(12, item.intValue());
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		single.subscribe(subscriber1);
		subscriber1.dispose();
		subscriber1.assertNoValues();
	}
}
