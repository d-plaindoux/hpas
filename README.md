# HiPeAS

[![Build Status](https://travis-ci.org/d-plaindoux/hpas.svg?branch=master)](https://travis-ci.org/d-plaindoux/hpas)
[![Coverage Status](https://coveralls.io/repos/github/d-plaindoux/hpas/badge.svg?branch=master)](https://coveralls.io/github/d-plaindoux/hpas?branch=master)

HiPeAS has been designed with one basic idea: all data type provided might be also available as monadic structures.
Functional paradigm deeply drives the design with a taste of OO for encapsulation and chaining methods which mimics infix
operators like Haskell monad `>>=`.

## A taste of HiPeAS

### Synchronous data types

Basically well known `MayBe` and `Try` are available for this purpose. Theses ADT are also the basis for the asynchronous part
of this library.

### Asynchronous computational model

#### Executors

```java
final Executor executor = ExecutorBuilder.create(Executors.newSingleThreadExecutor());
```

#### Async

In Executor **&lt;T&gt; async :: (() -> T) &rarr; Promise&lt;T&gt;**

```java
final Promise<Integer> integerPromise = executor.async(() -> 1);
```

#### `and` or map 

In Promise&lt;T&gt; **&lt;R&gt;map :: (T &rarr; R) &rarr; Promise&lt;R&gt;**
In Promise&lt;T&gt; **&lt;R&gt;and :: (T &rarr; R) &rarr; Promise&lt;R&gt;**

```java
integerPromise.map(i -> i + 1);
integerPromise.and(i -> i + 1);
```

#### `andThen` or flatmap

In Promise&lt;T&gt; **&lt;R&gt;flatmap :: (T &rarr; Promise&lt;R&gt;) &rarr; Promise&lt;R&gt;**
In Promise&lt;T&gt; **&lt;R&gt;andThen :: (T &rarr; Promise&lt;R&gt;) &rarr; Promise&lt;R&gt;**

```java
integerPromise.flatmap(i -> executor.async(() -> i + 1));
integerPromise.andThen(i -> executor.async(() -> i + 1));
```

#### Back to the Future

In Promise&lt;T&gt; **getFuture :: () &rarr; Future&lt;T&gt;**

```java
integerPromise.getFuture();
```

### Conclude on success

In Promise&lt;T&gt; **onSuccess :: (T &rarr; void) &rarr; Promise&lt;T&gt;**

```java
integerPromise.onSuccess(i -> System.println(i))
```

### Conclude on failure

In Promise&lt;T&gt; **onFailure :: (Throwable &rarr; void) &rarr; Promise&lt;T&gt;**

```java
integerPromise.onFailure(t -> t.printStackTrace(System.err))
```

### Conclude on complete

In Promise&lt;T&gt; **onComplete :: (Try&lt;T&gt; &rarr; void) &rarr; Promise&lt;T&gt;**

```java
integerPromise.onComplete(t -> t.onSuccess(integerPromise::onSuccess).onFailure(integerPromise::onFailure));
```

## About the library design 

The library has been designed simulating High Order Type in Java and self type thanks to F-Bounded quantification polymorphism. 

For more information follow [this link](https://gist.github.com/jdegoes/6842d471e7b8849f90d5bb5644ecb3b2).

## License

Copyright (C)2016 D. Plaindoux.

This program is  free software; you can redistribute  it and/or modify
it  under the  terms  of  the GNU  Lesser  General  Public License  as
published by  the Free Software  Foundation; either version 2,  or (at
your option) any later version.

This program  is distributed in the  hope that it will  be useful, but
WITHOUT   ANY  WARRANTY;   without  even   the  implied   warranty  of
MERCHANTABILITY  or FITNESS  FOR  A PARTICULAR  PURPOSE.  See the  GNU
Lesser General Public License for more details.

You  should have  received a  copy of  the GNU  Lesser General  Public
License along with  this program; see the file COPYING.  If not, write
to the  Free Software Foundation,  675 Mass Ave, Cambridge,  MA 02139,
USA.
