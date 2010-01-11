#!/usr/bin/env ruby

arff = File.read("weather.nominal.arff").split("\n")

class Attribute
  attr_reader :data, :col, :values, :name
  
  def initialize(col, name, values)
    @col = col
    @name = name
    @values = values
    @data = []
  end
  
  def calc_gain(instances, excluded_attrs)
    [self, rand * 1.0]
  end
end

attributes = []
col = 0

for line in arff[2..6]
  line =~ /^@attribute (\w+) \{([\w\s,]+)\}$/
  attributes << Attribute.new(col, $1, $2.split(",").map { |v| v.strip })
  col += 1
end

instances = []

for line in arff[9..22]
  if line.size > 0
    instance = line.split(",")
    col = 0
    for val in instance
      attributes[col].data << val
      col += 1
    end
    instances << instance
  end
end

class Leaf
  def initialize(s)
    @s = s
  end
  
  def print(indent=0)
    " " * (indent+2) + @s
  end
end

class Node
  def initialize(attr, gain, children)
    @attr = attr
    @gain = gain
    @children = children
  end
  
  def print(indent=0)
    " " * (indent+2) + "(#{@attr.name})\n" + @children.keys.map { |k| " " * (indent+2+1) + "#{k}\n#{@children[k].print(indent+2)}" }.join("\n")
  end
end

def find_instances_for_value(instances, col, val)
  instances.select { |i| i[col] == val }
end

def best_tree(instances, attrs, excluded_attrs)
  attr_gains = attrs.map { |a| a.calc_gain(instances, excluded_attrs) }.sort_by { |ag| -ag[1] }
  best_attr = attr_gains.first[0]
  children = {}
  best_attr.values.each do |val|
    filtered_instances = find_instances_for_value(instances, best_attr.col, val)
    if filtered_instances.map { |i| i[-1] }.uniq.size == 1
      children[val] = Leaf.new(filtered_instances[0][-1])
    else
      if (attrs - [best_attr]).size > 0
        children[val] = best_tree(filtered_instances, attrs - [best_attr], excluded_attrs + [best_attr])
      end
    end
  end
  Node.new(best_attr, attr_gains.first[1], children)
end

tree = best_tree(instances, attributes[0..-2], [])
puts tree.print
