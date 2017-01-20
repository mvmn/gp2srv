package x.mvmn.lang.util;

public class Tuple<A, B, C, D, E> {

	private final A a;
	private final B b;
	private final C c;
	private final D d;
	private final E e;

	public Tuple(final A a, final B b, final C c, final D d, final E e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public C getC() {
		return c;
	}

	public D getD() {
		return d;
	}

	public E getE() {
		return e;
	}
}
