public class Leaf extends Node {
  String text;
  
  public Leaf(String s) {
    this.text = s;
  }
  
  public void print(int indent) {
    printSpaces(indent+2);
    System.out.println(this.text);
  }
}
