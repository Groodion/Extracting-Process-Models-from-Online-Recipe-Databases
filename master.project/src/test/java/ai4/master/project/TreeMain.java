package ai4.master.project;

import ai4.master.project.tree.Node;
import ai4.master.project.tree.TreeTraverser;

/**
 * Created by René Bärnreuther on 05.06.2017.
 * Implements an example for the Nodes and the tree traverser
 * TODO Do we need a wrapper method around node?
 */
public class TreeMain {

    public static void main(String [] args){

        Node<String> root = new Node<String>("Root");

        Node<String> child1 = new Node<String>("Child1");
        root.addChild(child1);

        Node<String> child2 = new Node<String>("Child2");
        root.addChild(child2);

        Node<String> child12 = new Node<String>("Child12");
        child1.addChild(child12);

        child2.addChild("Child21");

        TreeTraverser<String> treeTraverser = new TreeTraverser<String>(root);
        for (Node node:
                treeTraverser.preOrder()) {
            System.out.println(node.getData().toString());

        }

    }
}
