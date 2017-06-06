package ai4.master.project.tree;

/**
 * Created by René Bärnreuther on 06.06.2017.
 * Only used as a wrapper class because its better readable.
 */
public class Tree<T> {


    private Node<T> root = null;

    public Tree(){}
    public Tree(Node<T>root){
        this.root = root;
    }

    public Node<T> getRoot(){
        return this.root;
    }

    public void setRoot(Node<T> root){
        this.root = root;
    }
}
