# RxWrapper

[![Core](https://api.bintray.com/packages/quanturium/maven/rxwrapper/images/download.svg) ](https://bintray.com/quanturium/maven/rxwrapper/_latestVersion)
[![Build Status](https://travis-ci.org/quanturium/RxWrapper.svg?branch=master)](https://travis-ci.org/quanturium/RxWrapper)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/quanturium/RxWrapper/blob/master/LICENSE.txt)

This library allows you to pass an object from onSubscribe to onEvent() / onDispose() without interfering with your Single/Maybe/Completable stream.

This can be really useful if you have a reactive database and want to bundle calls within a SQLite transaction:

```java

Completable completable = Completable.concatArray(query1, query2, query3)
				.compose(new WrapperCompletableTransformer<>(new WrapperCompletableCallback<TransactionWrapper>() {
					@Override
					public Integer onSubscribe() {
						TransactionWrapper tw = new TransactionWrapper(db);
						tw.start();
						return tw;
					}

					@Override
					public void onEvent(String s, Throwable e, TransactionWrapper tw) {
						if(e == null)
							tw.markSuccessful();
						tw.end();
					}

					@Override
					public void onDispose(TransactionWrapper tw) {
						tw.end();
					}
				}));


public static class TransactionWrapper extends AtomicReference<BriteDatabase.Transaction> {

		private final BriteDatabase database;

		public TransactionWrapper(BriteDatabase database) {
			this.database = database;
		}

		public void start() {
			set(database.newTransaction());
			Timber.i("Transaction started %s", get().toString());
		}

		public void markSuccessful() {
			get().markSuccessful();
			Timber.i("Transaction successful %s", get().toString());
			end();
		}

		public void end() {
			BriteDatabase.Transaction transaction = getAndSet(null);
			if (transaction != null) {
				transaction.end();
				Timber.i("Transaction end %s", transaction.toString());
			}
		}
	}
```

## License
    Copyright (c) 2018 Arnaud Frugier

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
