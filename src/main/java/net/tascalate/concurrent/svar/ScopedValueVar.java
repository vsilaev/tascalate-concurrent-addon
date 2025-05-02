/**
 * Copyright 2015-2025 Valery Silaev (http://vsilaev.com)
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
package net.tascalate.concurrent.svar;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.tascalate.concurrent.var.ContextVar;

public class ScopedValueVar<T> implements ContextVar<T> {

    private final ScopedValue<T> delegate;
    
    private ScopedValueVar(ScopedValue<T> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public T get() {
        return delegate.isBound() ? delegate.get() : null;
    }
    
    @Override
    public void runWith(T capturedValue, Runnable code) {
        ScopedValue.runWhere(delegate, capturedValue, code);
    }

    @Override
    public <V> V supplyWith(T capturedValue, Supplier<V> code) {
        return ScopedValue.getWhere(delegate, capturedValue, code);
    }
    
    @Override
    public <V> V callWith(T capturedValue, Callable<V> code) throws Exception {
        return ScopedValue.callWhere(delegate, capturedValue, code);
    }
    
    @Override
    public String toString() {
        return String.format("<scoped-value-ctx-var>[%s]", delegate);
    } 
    
    public static <T> ScopedValueVar<T> of(ScopedValue<T> scopedValue) {
        return new ScopedValueVar<>(scopedValue);
    }
    
    @SafeVarargs
    public static <T> ContextVar<List<? extends T>> of(ScopedValue<? extends T>... scopedValues) {
        return of(List.of(scopedValues));
    }
    
    public static <T> ContextVar<List<? extends T>> of(List<? extends ScopedValue<? extends T>> scopedValues) {
        if (null == scopedValues || scopedValues.isEmpty()) {
            return ContextVar.empty();
        } else {
            return new ScopedValueVarGroup<>(scopedValues);
        }
    }

}
