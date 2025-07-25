# HiPeAS

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/052bbdb13acf4b35bd5bbb485f6cad25)](https://app.codacy.com/manual/d-plaindoux/hpas?utm_source=github.com&utm_medium=referral&utm_content=d-plaindoux/hpas&utm_campaign=Badge_Grade_Dashboard)
[![Coverage Status](https://coveralls.io/repos/github/d-plaindoux/hpas/badge.svg?branch=master)](https://coveralls.io/github/d-plaindoux/hpas?branch=master)
[![stable](http://badges.github.io/stability-badges/dist/stable.svg)](http://github.com/badges/stability-badges)
[![Maven Central](https://img.shields.io/maven-central/v/org.smallibs/hpas.svg)](http://search.maven.org/#artifactdetails%7Corg.smallibs%7Chpas%7C0.5%7Cjar)

Functional paradigm deeply drives the design with a taste of OO for encapsulation and chaining methods which mimics infix
operators like Haskell monad function `>>=`. 

In addition, with version 21 of Java an Async/Await DSL like is available. 

Since such ADT provides traditional map, flapmap etc. functions for a DSL perspective are also given in order to increase the code readability.

## A taste of HiPeAS

```java
Executor executor = ExecutorHelper.create(Executors.newSingleThreadExecutor());
Promise<String> helloWorldPromise = executor.async(() -> "Hello").and(s -> s + " world!");
```

## HiPeAS overview

### Synchronous data types

Basically well known `MayBe` and `Try` are available for this purpose. Theses ADT are also the basis for the asynchronous part
of this library.

### Asynchronous computational model

### Executors

```java
Executor executor = ExecutorHelper.create(Executors.newSingleThreadExecutor());
```

#### `async`

In Executor **&lt;T&gt; async :: (() -> T) &rarr; Promise&lt;T&gt;**

```java
Promise<Integer> integerPromise = executor.async(() -> 1);
```

#### `await`

In ExecutorHelper **&lt;T&gt; await :: (Promise&lt;T&gt;, Duration) &rarr; Try&lt;T&gt;**

```java
Try<Integer> result = ExecutorHelper.await(integerPromise, Duration.TWO_SECONDS);
```

### Promise

#### `and` or `map` 

In Promise&lt;T&gt; **&lt;R&gt; map :: (T &rarr; R) &rarr; Promise&lt;R&gt;**

In Promise&lt;T&gt; **&lt;R&gt; and :: (T &rarr; R) &rarr; Promise&lt;R&gt;**

```java
integerPromise.map(i -> i + 1);
integerPromise.and(i -> i + 1);
```

#### `then` or `flatmap`

In Promise&lt;T&gt; **&lt;R&gt; flatmap :: (T &rarr; Promise&lt;R&gt;) &rarr; Promise&lt;R&gt;**

In Promise&lt;T&gt; **&lt;R&gt; then :: (T &rarr; Promise&lt;R&gt;) &rarr; Promise&lt;R&gt;**

```java
integerPromise.flatmap(i -> executor.async(() -> i + 1));
integerPromise.then(i -> executor.async(() -> i + 1));
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
integerPromise.onComplete(t -> t.fold(integerPromise::onSuccess, integerPromise::onFailure));
```

## Async Await DSL

Now we can use async/await mechanism using Loom virtual thread. We can now await for a promise implying an
asynchronous mechanism based on thread parking if it's a virtual thread or a blocking procedure for system
thread.

```java
var executor = ExecutorHelper.create(Executors.newVirtualThreadPerTaskExecutor());

var aLongAddition = executor.async(() -> {
    var firstInteger = executor.async(() -> /* do something */ 2);
    Thread.sleep(3000); // Current thread do nothing during 3 seconds
    var firstInteger = executor.async(() -> /* do something else */ 5);

    return firstInteger.await() + secondInteger.await(); // Await force virtual thread parking 
}).await(); // System thread waiting for the result
```

## Functor, Applicative and Monad

In addition monadic approach is available for each ADT. As usual `Monad` ihnerits `Applicative` which inherits `Functor`.

### Functor

In PromiseHelper **functor&lt;T&gt; :: Promise&lt;T&gt; → Functor&lt;T&gt;**

```java
Functor<Promise, Integer, Promise<Integer>> p1 = functor(executor.async(() -> 1));
HK<Promise, Integer, Promise<Integer>> p2 = p1.map(i -> i + 1);
```
### Applicative

In PromiseHelper **applicative&lt;T&gt; :: Promise&lt;T&gt; → Applicative&lt;T&gt;**

```java
Applicative<Promise, Integer, Promise<Integer>> p1 = applicative(executor.async(() -> 1));
HK<Promise, Integer, Promise<Integer>> p2  = p1.apply(functor(executor.async(() -> i -> i + 1)));
```
### Monad

In PromiseHelper **monad&lt;T&gt; :: Promise&lt;T&gt; → Monad&lt;T&gt;**

```java
Monad<Promise, Integer, Promise<Integer>> p1 = monad(executor.async(() -> 1));
HK<Promise, Integer, Promise<Integer>> p2 = p1.flatmap(i -> executor.async(() -> i + 1));
```

## CompletableFuture and Promise

In CompletableFutureHelper **completableFuture&lt;T&gt; :: Promise&lt;T&gt; → CompletableFuture&lt;T&gt;**

```java
Executor executor = ExecutorHelper.create(Executors.newSingleThreadExecutor());
Promise<String> helloWorldPromise = executor.async(() -> "Hello").and(s -> s + " world!");
CompletableFuture<String> completable = CompletableFutureHelper.completableFuture(helloWorldPromise);
```

In CompletableFutureHelper **promise&lt;T&gt; :: CompletableFuture&lt;T&gt; → Promise&lt;T&gt;**

```java
CompletableFuture<String> completable = CompletableFutureHelper.supplyAsync(() -> "Hello World");
Promise<String> helloWorldPromise = CompletableFutureHelper.promise(completable);
```

## Releases

This library is available at Sonatype OSS Repository Hosting service and can be simply used adding the following 
dependency - for instance - to your pom project.

```
<dependency>
  <groupId>org.smallibs</groupId>
  <artifactId>hpas</artifactId>
  <version>0.11.0</version>
</dependency>
```

## About the library design 

The library has been designed simulating High Order Type in Java and self type thanks to F-Bounded quantification polymorphism. 

For more information follow [this link](https://gist.github.com/jdegoes/6842d471e7b8849f90d5bb5644ecb3b2).

## License

Copyright (C)2016-2025 D. Plaindoux.

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
