package com.quanturium.android.library.rxwrapper;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WrapperMaybeTest {

	@Test
	public void noItem() {

		Integer object = 12; // random number

		Maybe<String> maybe = Maybe.<String>empty()
				.compose(new WrapperMaybeTransformer<>(new WrapperMaybeCallback<String, Integer>() {
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
						assertEquals(12, item.intValue());
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		maybe.subscribe(subscriber1);
		subscriber1.assertComplete();
		subscriber1.dispose();
	}

	@Test
	public void singleItem() {

		String singleItem = "a";
		Integer object = 12; // random number

		Maybe<String> maybe = Maybe.just(singleItem)
				.compose(new WrapperMaybeTransformer<>(new WrapperMaybeCallback<String, Integer>() {
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
						assertEquals(12, item.intValue());
					}
				}));

		TestObserver<String> subscriber1 = new TestObserver<>();
		maybe.subscribe(subscriber1);
		subscriber1.assertValue(singleItem);
		subscriber1.dispose();
	}

	@Test
	public void error() {

		Integer object = 12; // random number

		Maybe<String> single = Maybe.<String>error(new IllegalStateException())
				.compose(new WrapperMaybeTransformer<>(new WrapperMaybeCallback<String, Integer>() {
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
						assertEquals(12, item.intValue());
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

		Maybe<String> single = Maybe.just(singleItem)
				.delay(1, TimeUnit.SECONDS, Schedulers.newThread())
				.compose(new WrapperMaybeTransformer<>(new WrapperMaybeCallback<String, Integer>() {
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
