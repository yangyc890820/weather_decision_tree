//obliczyc przyrost informacji
import weka.core.*;
import java.io.*;
import java.util.*;

class wekaDT {
    static HashMap<Attribute, int[]> attrClassesCount;
    
    public static void main(String[] args) throws Exception {
      Instances instances = new Instances(new FileReader("weather.nominal.arff"));
      ArrayList<Instance> insts = new ArrayList<Instance>();
      for (int i=0; i<instances.numInstances(); i++) {
        insts.add(instances.instance(i));
      }
      ArrayList<Attribute> attrs = new ArrayList<Attribute>();
      for (int a=0; a<instances.numAttributes()-1; a++) {
        attrs.add(instances.attribute(a));
      }
      Node tree = findBestTree(insts, attrs);//, [])
      tree.print(0);
    }
    
    public static Node findBestTree(ArrayList<Instance> insts, ArrayList<Attribute> attrs) {
      Attribute bestAttr = null;
      double bestGain = 0.0;
      for (Attribute a : attrs) {
        double gain = calculateGainForAttribute(a, insts);
        if (bestAttr == null || gain > bestGain) {
          bestAttr = a;
          bestGain = gain;
        }
      }
      HashMap<String, Node> children = new HashMap<String, Node>();
//       best_attr.values.each do |val|
//         filtered_instances = find_instances_for_value(instances, best_attr.col, val)
//         if filtered_instances.map { |i| i[-1] }.uniq.size == 1
//           children[val] = Leaf.new(filtered_instances[0][-1])
//         else
//           if (attrs - [best_attr]).size > 0
//             children[val] = best_tree(filtered_instances, attrs - [best_attr], excluded_attrs + [best_attr])
//           end
//         end
//       end
      return new Node(bestAttr, bestGain, children);
    }
    
    public static double calculateGainForAttribute(Attribute a, ArrayList<Instance> insts) {
      return 0.5;
    }
        
    public static ArrayList<Instance> findInstancesForValue(ArrayList<Instance> insts, int col, String val) {
      ArrayList<Instance> filtered = new ArrayList<Instance>();
      for (Instance i : insts) {
        if (i.stringValue(col).equals(val)) {
          filtered.add(i);
        }
      }
      return filtered;
    }
    
/*    public static void old() throws Exception {
        FileReader reader = new FileReader("weather.nominal.arff");
        Instances dane = new Instances(reader);

        Attribute attr;
        HashMap<String, int[]> valuesClassesCount = new HashMap<String, int[]>();
        attrClassesCount = new HashMap<Attribute, int[]>();
        
        for (int i=0; i<dane.numAttributes()-1; i++) {

            double info2 = 0;
            double info3 = 0;
            //double gain = 0;
            
            attr = dane.attribute(i);
            System.out.println("==== Atrybut: " + attr.name());
            int[] classes = {0, 0};
            attrClassesCount.put(attr, classes);
            String value;
            for (int j=0; j<attr.numValues(); j++) {
                value = attr.value(j);
                int[] array = {0, 0};
                valuesClassesCount.put(value, array);
                System.out.println("---- Wartość: " + value);
                for (int k=0; k<dane.numInstances(); k++) {
                    if (dane.instance(k).stringValue(i).equals(value)) {
                        String klass = dane.instance(k).stringValue(dane.numAttributes()-1);
                        if (klass.equals("yes")) {
                            array[0] += 1;
                            classes[0] += 1;
                        } else {
                            array[1] += 1;
                            classes[1] += 1;
                        }
                    }
                }
                
                System.out.println("["+ array[0] + "," + array[1] + "]");
                double info = info(array); //liczy entropie dla wartosci
                info2 = info(classes); // liczy entropie dla calego atrybutu
                info3 += info3(array, info); //liczy drugą czesc do gain'a
                System.out.println("Entropia: " + info);
            }
            System.out.println("Entropia dla atrybutu: " + info2);
            double gain = gain(info2, info3); //liczy gaina
            System.out.println("Gain: " + gain);
            
            //System.out.println("Info3: "+ info3);
            System.out.println("\n");
            //System.out.println("["+ classes[0] + "," + classes[1] + "]");
        }
    }*/
    
    private static double info(int[] data) {
        double sum = (data[0] + data[1]) * 1.0;
        if((data[0] / sum) == 1 || (data[1] / sum) == 1)  // dodal Tomek
            return 0;
        else
            return entropy(data[0] / sum, data[1] / sum);
    }
    
    private static double info3(int[] data, double entr) { //liczenie 2 czesci do gaina
        double sumi = (data[0] + data[1]) * 1.0;
//        double sumc = (classes[0] + classes[1]) * 1.0;
//        System.out.println(" " + sumi + " " + sumc + " " + entr);
        return ((sumi/14) * entr);
    }

    private static double entropy(double a, double b) {
        return - a * Math.log(a)/Math.log(2) - b * Math.log(b)/Math.log(2);
    }
    
    private static double gain(double info2, double info3) {
    //private static double gain(Attribute attr, double info2) {
        return info2 - info3; // dodal Tomek
        //return 0;
    }
}


/*
W programie obliczylismy przyrosty (gain) dla wszystkich atrybutow. Nastepnie program musi wybrac najwiekszy z tych przyrostow i 
budowac drzewo od pierwszej jego wartosci (czyli np dla outlooka bedzie to sunny). Sunny trzeba traktowac teraz jako atrybut 
(nie brac pod uwage wczesniejszych atrybutow jak outlook i reszta) i ponownie obliczyc przyrosty informacji dla pozostalych 3 poddrzew.
Nastepnie program ma wybierac ten atrybut (temperature, humidity lub windy) ktory ma najwiekszy przyrost. 
Potem program ma wrocic do wartosci sunny i w jej miejsce wstawic kolejna wartosc outlooka overcast, no i analogicznie sie robi druga 
galąz ale juz sie nie bierze pod uwage outlook i temperature
czyli po ludzku dojsc do tego co przedstawia rysunek na stronie 92...
*/

//      entropia(atrybut) - entropia(jendostkowa)*entropia(atrybutu) + ...
//dodac licznik ile jest pod galez i w petli potem polizcyc kazda osobna entropie i je zsumowac i dostaniemy gain
// gain(outlook) = info([9,5]) - info([2,3], [4,0], [3,2]) = [9/14*log(9/14)-5/14*log(5/14)] - {[(2/5*log(2/5)-3/5*log(3/5)]*(5/14) + .... + ....}
