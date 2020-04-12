public class Main {

    public static void main(String[] args) {
        //first parameters:
        // numberOfGenereation=50 -- populationSize=10 -- tournementSize=2 -- mutationRate=0.01
//        new Coloring_Graph_problem(10,31,2,50,0.01);
//        int numberOfnodes = 5;
        int numberOfnodes = 31;
        new Genetics.Coloring_Graph_problem(10,numberOfnodes,2,50,0.01);

        new Genetics.Coloring_Graph_problem(100,numberOfnodes,2,500,0.02);
        new Genetics.Coloring_Graph_problem(100,numberOfnodes,5,500,0.05);
        new Genetics.Coloring_Graph_problem(100,numberOfnodes,10,500,0.1);

        new Genetics.Coloring_Graph_problem(1000,numberOfnodes,2,5000,0.01);
        new Genetics.Coloring_Graph_problem(1000,numberOfnodes,5,5000,0.05);
        new Genetics.Coloring_Graph_problem(1000,numberOfnodes,10,5000,0.1);
    }
}
