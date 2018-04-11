# A fast and effective heuristic for the feedback arc set problem

<img src="Abstract.png" width="800">



[Original paper](https://www.sciencedirect.com/science/article/pii/002001909390079O)

![Topological Ordering](Topological_Ordering.png)

### Input format:

A set of edges in TSV (Tab Separated Values) format:

```
node1⇥parent_node1
node1⇥parent_node2
node1⇥parent_node3
```

one per line.

### Output format:

The set of edges that respect the topological ordering in the same format.

### Usage:

Once the code is compiled and packed in runnable jar:

```
java -jar cycles_removal.jar input_file.tsv output_file.tsv
```