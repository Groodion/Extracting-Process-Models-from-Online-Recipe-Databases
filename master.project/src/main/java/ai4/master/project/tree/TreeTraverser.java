package ai4.master.project.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 05.06.2017.
 * Implements a Traverser for a tree starting from the given root.
 */
public class TreeTraverser<T> {


    private Node<T> root;

    private boolean print = false;
    public TreeTraverser(Tree<T> tree){
        if(tree.getRoot() == null){
            System.err.print("Root equals null");
        }
        this.root = tree.getRoot();
    }


    public void activatePrint(){ print = true;}
    public void deactivatePrint() {print = false;}
    // TODO as far as I think we need a levelwise traversing. This needs to be implemented probably. But we still need to check for this ..
    /**
     * Traverses the tree starting from root in preOrder direction
     * @return the nodes in preOrder ordering
     */
    public List<Node<T>> preOrder(){
      List<Node<T>> preOrder = new ArrayList<Node<T>>();
      actuallyPreOrder(root, preOrder);
      return  preOrder;
    }

    // Called by the public method to realize recursion with list.
    private void actuallyPreOrder(Node<T> root, List<Node<T>> list){
        if(root != null){
            if(print)
                System.out.println(root.getData().toString());
            list.add(root);
        }

        for (Node node :
                root.getChildren()) {
            actuallyPreOrder(node, list);
        }
    }


}
