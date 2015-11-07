package cbb.qomo.tools.tableprocess.expression;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
	private T node;
	private List<Tree<T>> children = new ArrayList<Tree<T>>();

	public Tree() {
	}

	public Tree(T node) {
		this.node = node;
	}

	public void addChild(Tree<T> child) {
		children.add(child);
	}

	public Tree<T> getChild(int i) {
		return children.get(i);
	}

	public List<Tree<T>> getChildren() {
		return children;
	}

	public T getNode() {
		return node;
	}

	public void setNode(T node) {
		this.node = node;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(node.toString());
		for (Tree<T> child : children) {
			sb.append(' ');
			sb.append(child.toString());
		}
		sb.append(')');
		return sb.toString();
	}
}
