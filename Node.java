import weka.core.*;
import java.util.*;

public class Node {
  Attribute attr;
  double gain;
  HashMap<String, Node> children;
  
  public Node() {
  }
  
  public Node(Attribute attr, double gain, HashMap<String, Node> children) {
    this.attr = attr;
    this.gain = gain;
    this.children = children;
  }
  
  protected void printSpaces(int num) {
    for (int i=0; i<num; i++) {
      System.out.print(" ");
    }
  }

  public void print(int indent) {
    printSpaces(indent+2);
    System.out.println("(" + this.attr.name() + ")");
    for (String key : this.children.keySet()) {
      printSpaces(indent+3);
      System.out.println(key);
      this.children.get(key).print(indent+2);
    }
  }
}
