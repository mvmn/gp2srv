package x.mvmn.lang.util;

public class ImmutablePair<A, B> {

	private final A a;
	private final B b;

	public ImmutablePair(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}
}
