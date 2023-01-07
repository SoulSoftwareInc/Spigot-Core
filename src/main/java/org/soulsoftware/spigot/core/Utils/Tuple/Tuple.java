package org.soulsoftware.spigot.core.Utils.Tuple;

import lombok.Getter;
import lombok.Setter;

public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A a() {
        return this.a;
    }

    public void a(A a) {
        this.a = a;
    }

    public B b() {
        return this.b;
    }

    public void b(B a) {
        this.b = a;
    }
}
