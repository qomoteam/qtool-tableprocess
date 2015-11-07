package cbb.qomo.tools.tableprocess.expression;

public class UnexpectedTokenException extends Exception {

	public UnexpectedTokenException(Token token) {
		super("Unexpected token: " + token);
	}
}
