package cbb.qomo.tools.tableprocess.expression;

public class UnexpectedCharacterException extends Exception {
	public UnexpectedCharacterException(String ch) {
		super("Unexpected character: " + ch);
	}
}
