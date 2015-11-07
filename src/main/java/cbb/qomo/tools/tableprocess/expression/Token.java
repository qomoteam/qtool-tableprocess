package cbb.qomo.tools.tableprocess.expression;

public class Token {
	public enum Type {
		EOF, ID, NUM, STR, COMP, MATCH, ARITH, LOG, UNI, LRB, RRB, COMMA
	}

	private Type type;
	private String value;

	public Token(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public Type getType() {
		return type;
	}

	public String toString() {
		return "<" + type.toString() + "," + value + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
