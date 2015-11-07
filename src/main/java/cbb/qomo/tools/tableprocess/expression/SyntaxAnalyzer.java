package cbb.qomo.tools.tableprocess.expression;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import cbb.qomo.tools.tableprocess.expression.Token.Type;

public class SyntaxAnalyzer {
	private Token token;
	private LexicalAnalyzer la;
	private Tree<Token> tree;
	private static String[][] ops = new String[6][];
	protected static HashMap<String, Integer> opPrior = new HashMap<String, Integer>();
	static {
		ops[0] = new String[] { "||" };
		ops[1] = new String[] { "&&" };
		ops[2] = new String[] { "==", "!=", "<", ">", "<=", ">=", "=~" };
		ops[3] = new String[] { "+", "-" };
		ops[4] = new String[] { "*", "/" };
		ops[5] = new String[] { "^" };
		for (int i = 0; i < ops.length; i++) {
			for (int j = 0; j < ops[i].length; j++) {
				opPrior.put(ops[i][j], i);
//				 String tout = i < 3 ? "Boolean" : "Double";
//				 String tin = i < 2 ? "Boolean" : "Double";
//				 System.out
//				 .println("binFunc.put(\"OP\", new BinFunc() {\npublic Object apply(Object left, Object right) {\nreturn (TIN) left OP (TIN) right;}});"
//				 .replaceAll("OP", ops[i][j]).replaceAll("TIN", tin));
			}
		}
	}

	public SyntaxAnalyzer(Reader input) {
		this.la = new LexicalAnalyzer(input);
		advance();
	}

	private void match(String value) throws UnexpectedTokenException {
		if (token.getValue().equals(value)) {
			advance();
		} else {
			error();
		}
	}

	private Tree<Token> newTree(Token token) {
		return new Tree<Token>(token);
	}

	private void advance() {
		try {
			token = la.nextToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void error() throws UnexpectedTokenException {
		throw new UnexpectedTokenException(token);
	}

	public Tree<Token> parse() throws UnexpectedTokenException {
		if (tree == null) {
			tree = expr(tree);
		}
		return tree;
	}

	private Tree<Token> tryMatchOp(String value) {
		if (token.getValue().equals(value)) {
			Tree<Token> tmp = newTree(token);
			advance();
			return tmp;
		} else {
			return null;
		}
	}

	private Tree<Token> tryMatchOpList(String[] ops)
			throws UnexpectedTokenException {
		Tree<Token> tmp = null;
		for (String op : ops) {
			tmp = tryMatchOp(op);
			if (tmp != null)
				break;
		}
		if (tmp == null) {
			error();
		}
		return tmp;
	}

	private boolean lowerThan(String op) {
		return opPrior.get(token.getValue()) < opPrior.get(op);
	}

	public Tree<Token> expr(Tree<Token> tree) throws UnexpectedTokenException {
		return binExpr(tree, 0);
	}

	public Tree<Token> binExpr(Tree<Token> tree, int level)
			throws UnexpectedTokenException {
		if (level < ops.length) {
			switch (token.getType()) {
			case LRB:
			case ID:
			case NUM:
			case STR:
			case UNI:
				tree = binExpr(tree, level + 1);
				tree = binExpr2(tree, level);
				break;
			case ARITH:
				int plusLevel = opPrior.get("+");
				if (level < plusLevel) {
					tree = binExpr(tree, level + 1);
					tree = binExpr2(tree, level);
					break;
				} else if (level == plusLevel) {
					tree = binExpr2(newTree(new Token(Type.NUM, "0")), level);
					break;
				} else {
					error();
					break;
				}
			default:
				error();
			}
		} else {
			switch (token.getType()) {
			case LRB:
				advance();
				tree = expr(tree);
				match(")");
				break;
			case ID:
				tree = id(tree);
				break;
			case STR:
			case NUM:
				tree = newTree(token);
				advance();
				break;
			case UNI:
				Tree<Token> tmp = tryMatchOpList(new String[] { "!" });
				tmp.addChild(binExpr(tree, level));
				tree = tmp;
				break;
			default:
				error();
			}
		}
		return tree;
	}

	public Tree<Token> binExpr2(Tree<Token> tree, int level)
			throws UnexpectedTokenException {
		switch (token.getType()) {
		case RRB:
		case COMMA:
		case EOF:
			break;
		case ARITH:
		case COMP:
		case MATCH:
		case LOG:
			if (lowerThan(ops[level][0]))
				break;
			Tree<Token> tmp = tryMatchOpList(ops[level]);
			tmp.addChild(tree);
			tmp.addChild(binExpr(tree, level + 1));
			tree = binExpr2(tmp, level);
			break;
		default:
			error();
			break;
		}
		return tree;
	}

	public Tree<Token> id(Tree<Token> tree) throws UnexpectedTokenException {
		tree = newTree(token);
		advance();
		switch (token.getType()) {
		case LRB:
			advance();
			tree = parameter(tree);
			break;
		default:
			break;
		}
		return tree;
	}

	public Tree<Token> parameter(Tree<Token> tree)
			throws UnexpectedTokenException {
		switch (token.getType()) {
		case RRB:
			advance();
			break;
		case COMMA:
			advance();
			tree = parameter(tree);
			break;
		default:
			tree.addChild(expr(tree));
			tree = parameter(tree);
			break;
		}
		return tree;
	}

	public static void main(String[] args) throws UnexpectedTokenException {
		String exp = "ln(10)+f(1,2,3)*(c11+2)>5";
		SyntaxAnalyzer sa = new SyntaxAnalyzer(new StringReader(exp));
		System.out.println(sa.parse());
	}

}
