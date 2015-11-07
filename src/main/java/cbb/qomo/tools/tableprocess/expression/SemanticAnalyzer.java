package cbb.qomo.tools.tableprocess.expression;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SemanticAnalyzer {
	private Tree<Token> tree;
	private Map<String, String> env;

	private static Map<String, BinFunc> binOp = new HashMap<String, BinFunc>();
	private static Map<String, UniFunc> uniOp = new HashMap<String, UniFunc>();
	private static Map<String, Func> functions = new HashMap<String, Func>();

	static {
		binOp.put("||", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Boolean) left || (Boolean) right;
			}
		});
		binOp.put("&&", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Boolean) left && (Boolean) right;
			}
		});
		binOp.put("==", new BinFunc() {
			public Object apply(Object left, Object right) {
				return left.equals(right);
			}
		});
		binOp.put("!=", new BinFunc() {
			public Object apply(Object left, Object right) {
				return !(left.equals(right));
			}
		});
		binOp.put("<", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left < (Double) right;
			}
		});
		binOp.put(">", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left > (Double) right;
			}
		});
		binOp.put("<=", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left <= (Double) right;
			}
		});
		binOp.put(">=", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left >= (Double) right;
			}
		});
		binOp.put("=~", new BinFunc() {
			public Object apply(Object left, Object right) {
				return Pattern.compile((String) right).matcher((String) left)
						.find();
			}
		});
		binOp.put("+", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left + (Double) right;
			}
		});
		binOp.put("-", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left - (Double) right;
			}
		});
		binOp.put("*", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left * (Double) right;
			}
		});
		binOp.put("/", new BinFunc() {
			public Object apply(Object left, Object right) {
				return (Double) left / (Double) right;
			}
		});
		binOp.put("^", new BinFunc() {
			public Object apply(Object left, Object right) {
				return Math.pow((Double) left, (Double) right);
			}
		});
		uniOp.put("!", new UniFunc() {
			public Object apply(Object right) {
				return !(Boolean) right;
			}
		});
		functions.put("ln", new Func() {
			public Object apply(List<Object> param) {
				return Math.log((Double) param.get(0));
			}
		});
	}

	public SemanticAnalyzer(SyntaxAnalyzer synAnlz) {
		try {
			this.tree = synAnlz.parse();
		} catch (UnexpectedTokenException e) {
			e.printStackTrace();
		}
	}

	public SemanticAnalyzer(Tree<Token> tree, Map<String, String> env) {
		this.tree = tree;
		this.env = env;
	}

	public Object eval() {
		return eval(tree, env);
	}

	private static Object eval(Token operator, Object left, Object right) {
		String op = operator.getValue();
		switch (operator.getType()) {
		case ARITH:
			return binOp.get(op).apply(getDouble(left), getDouble(right));
		case COMP:
			return binOp.get(op).apply(getDouble(left), getDouble(right));
		case MATCH:
			return binOp.get(op).apply(left, right);
		case LOG:
			return binOp.get(op).apply(getBool(left), getBool(right));
		default:
			return null;
		}
	}

	private static Object eval(Token operator, Object right) {
		String op = operator.getValue();
		switch (operator.getType()) {
		case UNI:
			return uniOp.get(op).apply(right);
		default:
			return null;
		}
	}

	private static Boolean getBool(Object obj) {
		if (obj instanceof String) {
			return Boolean.valueOf((String) obj);
		} else {
			return (Boolean) obj;
		}
	}

	private static Double getDouble(Object obj) {
		if (obj instanceof String) {
			Double d = Double.NaN;
			try {
				d = Double.valueOf((String) obj);
			} catch (NumberFormatException e) {
				// silently convert unknown strings into NaN
			}
			return d;
		} else {
			return (Double) obj;
		}
	}

	public static Object eval(Tree<Token> tree, Map<String, String> env) {
		Token node = tree.getNode();
		Object left;
		Object right;
		switch (node.getType()) {
		case ARITH:
		case LOG:
		case COMP:
		case MATCH:
			left = eval(tree.getChild(0), env);
			right = eval(tree.getChild(1), env);
			return eval(node, left, right);
		case NUM:
			return Double.valueOf(node.getValue());
		case ID:
			if (tree.getChildren().isEmpty()) {
				// it's a variable
				return env.get(node.getValue());
			} else {
				// it's a function
				ArrayList<Object> param = new ArrayList<Object>();
				for (Tree<Token> child : tree.getChildren()) {
					param.add(eval(child, env));
				}
				return functions.get(node.getValue()).apply(param);
			}
		case STR:
			return node.getValue();
		case UNI:
			right = eval(tree.getChild(0), env);
			return eval(node, right);
		default:
			return null;
		}
	}

	public Map<String, String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

	public static void main(String[] args) throws UnexpectedTokenException {
		String exp = "$1<0.001";
		SyntaxAnalyzer sa = new SyntaxAnalyzer(new StringReader(exp));
		System.out.println(sa.parse());
		Map<String, String> env = new HashMap<String, String>();
		env.put("$1", "NA");
		env.put("$2", "12");
		env.put("$3", "22000");
		System.out.println(new SemanticAnalyzer(sa.parse(), env).eval());
	}
}
