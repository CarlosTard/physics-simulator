package simulator.misc;

public class Vector {

	private final int _n; // dimension of the vector
	private double[] _data; // array of vector's components

	// create the zero vector of length n
	public Vector(int n) {
		_n = n;
		_data = new double[n];
	}

	// copy constructor
	public Vector(Vector v) {
		_n = v._n;
		_data = v._data.clone();
	}

	// create a vector from an array
	public Vector(double[] data) {
		_n = data.length;
		_data = data.clone();
	}

	// return the dimension of the vector
	public int dim() {
		return _n;
	}

	// return the inner product of this Vector a and b
	public double dot(Vector that) {
		if (dim() != that.dim())
			throw new IllegalArgumentException("dimensions disagree");
		double sum = 0.0;
		for (int i = 0; i < _n; i++)
			sum = sum + (_data[i] * that._data[i]);
		return sum;
	}

	// return the length of the vector (Euclidean norm)
	public double magnitude() {
		return Math.sqrt(dot(this));
	}

	// return the distance between this and that (Euclidean)
	public double distanceTo(Vector that) {
		if (dim() != that.dim())
			throw new IllegalArgumentException("dimensions disagree");
		return minus(that).magnitude();
	}

	// create and return a new object whose value is (this + that)
	public Vector plus(Vector that) {
		if (dim() != that.dim())
			throw new IllegalArgumentException("dimensions disagree");
		Vector c = new Vector(_n);
		for (int i = 0; i < _n; i++)
			c._data[i] = _data[i] + that._data[i];
		return c;
	}

	// create and return a new object whose value is (this - that)
	public Vector minus(Vector that) {
		if (dim() != that.dim())
			throw new IllegalArgumentException("dimensions disagree");
		Vector c = new Vector(_n);
		for (int i = 0; i < _n; i++)
			c._data[i] = _data[i] - that._data[i];
		return c;
	}

	// return the corresponding coordinate
	public double coordinate(int i) {
		return _data[i];
	}

	// create and return a new object whose value is (this * factor)
	public Vector scale(double factor) {
		Vector c = new Vector(_n);
		for (int i = 0; i < _n; i++)
			c._data[i] = factor * _data[i];
		return c;
	}

	// return the corresponding unit vector
	public Vector direction() {
		if (magnitude() > 0.0)
			return scale(1.0 / magnitude());
		else
			return new Vector(this);
	}

	// return a string representation of the vector
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('[');
		for (int i = 0; i < _n; i++) {
			s.append(_data[i]);
			if (i < _n - 1)
				s.append(", ");
		}
		s.append(']');
		return s.toString();
	}

}
