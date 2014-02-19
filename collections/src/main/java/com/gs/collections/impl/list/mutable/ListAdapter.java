/*
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.list.mutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.api.list.primitive.MutableByteList;
import com.gs.collections.api.list.primitive.MutableCharList;
import com.gs.collections.api.list.primitive.MutableDoubleList;
import com.gs.collections.api.list.primitive.MutableFloatList;
import com.gs.collections.api.list.primitive.MutableIntList;
import com.gs.collections.api.list.primitive.MutableLongList;
import com.gs.collections.api.list.primitive.MutableShortList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.ListIterate;

/**
 * This class provides a MutableList wrapper around a JDK Collections List interface instance.  All of the MutableList
 * interface methods are supported in addition to the JDK List interface methods.
 * <p/>
 * To create a new wrapper around an existing List instance, use the {@link #adapt(List)} factory method.
 */
public final class ListAdapter<T>
        extends AbstractListAdapter<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<T> delegate;

    ListAdapter(List<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("ListAdapter may not wrap null");
        }
        if (newDelegate instanceof RandomAccess)
        {
            throw new IllegalArgumentException("Use RandomAccessListAdapter instead");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected List<T> getDelegate()
    {
        return this.delegate;
    }

    public static <E> MutableList<E> adapt(List<E> list)
    {
        if (list instanceof MutableList)
        {
            return (MutableList<E>) list;
        }
        if (list instanceof ArrayList)
        {
            return ArrayListAdapter.adapt((ArrayList<E>) list);
        }
        if (list instanceof RandomAccess)
        {
            return new RandomAccessListAdapter<E>(list);
        }
        return new ListAdapter<E>(list);
    }

    public ImmutableList<T> toImmutable()
    {
        return Lists.immutable.ofAll(this.delegate);
    }

    public MutableList<T> asUnmodifiable()
    {
        return UnmodifiableMutableList.of(this);
    }

    public MutableList<T> asSynchronized()
    {
        return SynchronizedMutableList.of(this);
    }

    @Override
    public MutableList<T> clone()
    {
        return FastList.newList(this.delegate);
    }

    /**
     * @deprecated use {@link FastList#newList()} instead (inlineable)
     */
    @Deprecated
    public MutableList<T> newEmpty()
    {
        return Lists.mutable.of();
    }

    @Override
    public void forEach(Procedure<? super T> procedure)
    {
        ListIterate.forEach(this.delegate, procedure);
    }

    public void reverseForEach(Procedure<? super T> procedure)
    {
        ListIterate.reverseForEach(this.delegate, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.forEachWithIndex(this.delegate, objectIntProcedure);
    }

    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.forEachWithIndex(this.delegate, fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return ListIterate.detect(this.delegate, predicate);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return ListIterate.count(this.delegate, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return ListIterate.anySatisfy(this.delegate, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return ListIterate.allSatisfy(this.delegate, predicate);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return ListIterate.injectInto(injectedValue, this.delegate, function);
    }

    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.forEach(this.delegate, fromIndex, toIndex, procedure);
    }

    public ListAdapter<T> sortThis(Comparator<? super T> comparator)
    {
        Iterate.sortThis(this.delegate, comparator);
        return this;
    }

    public ListAdapter<T> sortThis()
    {
        return this.sortThis(Comparators.naturalOrder());
    }

    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        return this.sortThis(Comparators.byFunction(function));
    }

    public ListAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public ListAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public ListAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public ListAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    public ListAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    public ListAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    public ListAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        return ListIterate.select(this.delegate, predicate, FastList.<T>newList());
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        return ListIterate.reject(this.delegate, predicate, FastList.<T>newList());
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return ListIterate.partition(this.delegate, predicate);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return ListIterate.selectInstancesOf(this.delegate, clazz);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return ListIterate.collect(this.delegate, function, FastList.<V>newList(this.delegate.size()));
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return ListIterate.collectBoolean(this.delegate, booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return ListIterate.collectBoolean(this.delegate, booleanFunction, target);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return ListIterate.collectByte(this.delegate, byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return ListIterate.collectByte(this.delegate, byteFunction, target);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return ListIterate.collectChar(this.delegate, charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return ListIterate.collectChar(this.delegate, charFunction, target);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return ListIterate.collectDouble(this.delegate, doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return ListIterate.collectDouble(this.delegate, doubleFunction, target);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return ListIterate.collectFloat(this.delegate, floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return ListIterate.collectFloat(this.delegate, floatFunction, target);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return ListIterate.collectInt(this.delegate, intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return ListIterate.collectInt(this.delegate, intFunction, target);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return ListIterate.collectLong(this.delegate, longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return ListIterate.collectLong(this.delegate, longFunction, target);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return ListIterate.collectShort(this.delegate, shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return ListIterate.collectShort(this.delegate, shortFunction, target);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ListIterate.flatCollect(this.delegate, function, FastList.<V>newList(this.delegate.size()));
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return ListIterate.collectIf(this.delegate, predicate, function, FastList.<V>newList());
    }

    @Override
    public <V> FastListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.groupBy(this.delegate, function, FastListMultimap.<V, T>newMultimap());
    }

    @Override
    public <V> FastListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return ListIterate.groupByEach(this.delegate, function, FastListMultimap.<V, T>newMultimap());
    }

    @Override
    public <P> MutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.selectWith(this.delegate, predicate, parameter, FastList.<T>newList());
    }

    @Override
    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.rejectWith(this.delegate, predicate, parameter, FastList.<T>newList());
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return ListIterate.collectWith(this.delegate, function, parameter, FastList.<V>newList(this.delegate.size()));
    }

    public MutableList<T> distinct()
    {
        return ListIterate.distinct(this.delegate, FastList.<T>newList());
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return ListIterate.zip(this.delegate, that, FastList.<Pair<T, S>>newList(this.delegate.size()));
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return ListIterate.zipWithIndex(this.delegate, FastList.<Pair<T, Integer>>newList(this.delegate.size()));
    }

    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return ListIterate.takeWhile(this.delegate, predicate);
    }

    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return ListIterate.dropWhile(this.delegate, predicate);
    }

    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return ListIterate.partitionWhile(this.delegate, predicate);
    }

    @Override
    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this.delegate);
    }
}
