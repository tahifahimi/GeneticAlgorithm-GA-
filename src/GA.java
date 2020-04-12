package Genetics;


import java.io.*;
import java.nio.file.Paths;
import java.util.Random;
import java.util.zip.DataFormatException;

public class GA{
    private Genetics.Chromosome[] population ;
    private Genetics.Chromosome[] newPopulation =null;
    private Integer populationSize;
    private int[][] edges;  //all chromosome have the same graph structure, so we use edge matrix to save the graph structure
    private int numberOfNodesInChromosome;
    private int tournementSize;
    private Genetics.Chromosome[] parents = null;
    private double mutationRate = 0.01;
//    private int numberOfGeneration = 1;

    //saved data for analysis
    private double[][] savedData;

    public GA(Integer populationSize,Integer numberOfNodesInChromosome,Integer tournementSize,Integer numberOfGeneration,double mutationRate) {
        this.populationSize = populationSize;
        population = new Genetics.Chromosome[populationSize];
        this.numberOfNodesInChromosome = numberOfNodesInChromosome;
        edges = new int[numberOfNodesInChromosome][numberOfNodesInChromosome];
        this.tournementSize = tournementSize;
        parents = new Genetics.Chromosome[populationSize/tournementSize];
//        this.numberOfGeneration = numberOfGeneration;
        this.mutationRate = mutationRate;
        savedData = new double[numberOfGeneration][3];

        //read the graph format and save it for any edges
        String filePath = Paths.get("D:\\study\\ArtificialIntel\\Project\\rubik-cubic\\Main\\src\\Genetics\\map.txt").toAbsolutePath().toString();
        try {
            readGraphFromTxt(filePath);
//            printGraph();
        } catch (IOException | DataFormatException e) {
            System.out.println("error in reading data from file");
            e.printStackTrace();
        }


        for (int l=0;l<numberOfGeneration;l++){
            colorPopulation();
            printGraph();
            fitnessChromosome();
            chooseParent();
            createNextGeneration();
            mutation();
            saveDataInMatrix(l);
            replacePopulation();
            //save Data for analysis
        }

        //write saved data in the file
        try {
            int[] changeData = new int[3];
            String fileN = "DataForGeneration"+numberOfGeneration+"Mutation"+mutationRate+".txt";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileN));
            String m ;
            for (int i=0;i<numberOfGeneration;i++){
//                System.out.println(savedData[i][0]+" "+savedData[i][1]+" "+savedData[i][2]);
//                changeData[0] = (savedData[i][0]);
//                changeData[1] = (int)(savedData[i][1]);
//                changeData[2] = (int)(savedData[i][2]);
////                m = new String(i+" "+savedData[i][0]+" "+savedData[i][1]+" "+savedData[i][2]);
//                m = new String(i+" "+changeData[0]+" "+changeData[1]+" "+changeData[2]);
                m = savedData[i][0]+" "+savedData[i][1]+" "+savedData[i][2];
                bufferedWriter.write(m);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printGraph() {
        for (int i=0;i<numberOfNodesInChromosome;i++)
            for (int j=0;j<numberOfNodesInChromosome;j++)
                if (edges[i][j]==1)
                    System.out.printf("(%d , %d) \n",i+1,j+1);
    }

    private void saveDataInMatrix(int place) {
        double maxFit = 0;
        double minFit =10000000;
        double sum =0;
        for (int i=0;i<populationSize;i++){
            System.out.printf("the fitness of them are : %f\n",population[i].fitness);
            if (population[i].fitness < minFit)
                minFit = population[i].fitness;
            if (population[i].fitness>maxFit)
                maxFit = population[i].fitness;
            sum =  sum+population[i].fitness;
        }
        savedData[place][0] = maxFit;
        savedData[place][1] = minFit;
        savedData[place][2] = sum/(double)populationSize;

        System.out.println(place+" "+savedData[place][0]+" "+savedData[place][1]+" "+savedData[place][2]);


    }

    private void replacePopulation() {
        if (population.length == newPopulation.length)
            population = newPopulation;
        else
            System.out.println("error in replacing the generation");
    }

    //set mutation on new population
    private void mutation() {
        //find the number of mutation in the population
        int mutatedGenomes = (int) (populationSize*numberOfNodesInChromosome*mutationRate);

        //use random to choose the chromosome and the gen and color
        Random random = new Random();
        int genMutated = 0;
        int chroMutated = 0;
        int color = 0;
        for (int i=0;i<mutatedGenomes;i++){
            chroMutated = random.nextInt(newPopulation.length);
            genMutated = random.nextInt(numberOfNodesInChromosome);
            color = random.nextInt(3);
            newPopulation[chroMutated].genes[genMutated] = color;
        }
    }

    // create next generation by cross over...
    private void createNextGeneration() {
        //use random for finding a good point for cross over
        Random random = new Random();
        int crossOverPlace = 2;
        newPopulation = new Genetics.Chromosome[populationSize];
        if (populationSize%2==0){
            //create two children from each parent
            for (int child = 0;child<populationSize;child=child+2){
                //creating children
                newPopulation[child] = new Genetics.Chromosome(numberOfNodesInChromosome);
                newPopulation[child+1] = new Genetics.Chromosome(numberOfNodesInChromosome);

                //find random place for crossover
                crossOverPlace = random.nextInt(numberOfNodesInChromosome);
                if (crossOverPlace<=0 || crossOverPlace>=(numberOfNodesInChromosome-1))
                    crossOverPlace = numberOfNodesInChromosome/2;

                //assign father and mother color to them
                int[] firstFarther = new int[crossOverPlace];
                int[] secondFather = new int[numberOfNodesInChromosome - crossOverPlace];
                int[] firstMother = new int[crossOverPlace];
                int[] secondMother = new int[numberOfNodesInChromosome - crossOverPlace];

                //replace father and mother data in the arrays
                int count = 0;
                for (int i=0;i<crossOverPlace;i++){
                    firstFarther[i] = parents[child%parents.length].genes[i];
                    firstMother[i] = parents[(child+1)%parents.length].genes[i];
                    count = i;
                }
                for (int i=0;i<numberOfNodesInChromosome-crossOverPlace;i++){
                    secondFather[i] = parents[child%parents.length].genes[count];
                    secondMother[i] = parents[(child+1)%parents.length].genes[count];
                    count = count+1;
                }
                newPopulation[child].changingColor(firstFarther,secondMother);
                newPopulation[child+1].changingColor(firstMother,secondFather);

            }
        }

    }

    //choose parent for next generation with TORNUMENT selection
    private void chooseParent() {
        Random random = new Random();
        int[] chromosomeInEachTour = new int[tournementSize];
        //tournement algorithm
        for (int i=0;i<parents.length;i++){
            //find the k number of chromosome by random
            for (int j=0;j<tournementSize;j++)
                chromosomeInEachTour[j] = random.nextInt(populationSize);

            //choose the best chromosome in "chromosomeInEachTour"
            double bestFitness = -100;
            Genetics.Chromosome bestChromosome = null;
            for (int j=0;j<tournementSize;j++){
                if (bestFitness<population[chromosomeInEachTour[j]].fitness){
                    bestFitness = population[chromosomeInEachTour[j]].fitness;
                    bestChromosome = population[chromosomeInEachTour[j]];
                }
            }
            //set the winner chromosome as a new parent
            parents[i] = bestChromosome;
//            System.out.printf("the best parent is : %f \n",parents[i].fitness);
        }
    }


    //calculate the fitness of each chromosome and set it.
    private void fitnessChromosome() {
        //calculate all number of edges in the graph
        int numberOfEdges = 0;
        for (int row=0;row<numberOfNodesInChromosome;row++)
            for (int col=0;col<numberOfNodesInChromosome;col++)
                if (edges[row][col]==1)
                    numberOfEdges=numberOfEdges+1;

        numberOfEdges = numberOfEdges/2;
        System.out.printf("the number of edges are :  %d \n",numberOfEdges);

        //calculate the fitness value for each chromosome
        for (int chro=0;chro<populationSize;chro++){
//            System.out.printf("chromosome %d : \n",chro+1);
            int sumDelta =0;
            //searching in just one chromosome
            for (int i=0;i<numberOfNodesInChromosome;i++){
//                System.out.printf("node %d is color %d \n",i+1,population[chro].genes[i]);
                for (int j=0;j<numberOfNodesInChromosome;j++){
                    if (edges[i][j]==1){
                        if (population[chro].genes[i] != population[chro].genes[j]){
//                            System.out.printf("color of %d and %d are different\n",i+1,j+1 );
                            sumDelta = sumDelta+1;
                        }
                    }
                }
            }
            //set the fitness for that chromosome
            population[chro].fitness = (float)sumDelta/numberOfEdges;
//            System.out.println("##############");
//            System.out.printf("the fitness %d is %f \n ",chro+1,population[chro].fitness);
        }
    }

    //read data from the text file
    private void readGraphFromTxt(String filePath) throws IOException, DataFormatException {
        //first change all the edges to zero
        for (int i=0;i<numberOfNodesInChromosome;i++)
            for (int j=0;j<numberOfNodesInChromosome;j++)
                edges[i][j] = 0;

        File readFile = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(readFile.getCanonicalPath()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] vertices = line.split("\\s");
                if (vertices.length != 2) {
                    throw new DataFormatException(line + "  should contain exactly 2 integers");
                }
                try {
                    System.out.println(vertices[0]);
                    System.out.println(vertices[1]);
                    int vertexV = Integer.parseInt(vertices[0]);
                    int vertexW = Integer.parseInt(vertices[1]);
                    edges[vertexV-1][vertexW-1] = 1;
                    edges[vertexW-1][vertexV-1] = 1;

                } catch (NumberFormatException exception) {
                    String msg = "One of the following is not intepretible as an integer: "
                            + vertices[0] + " " + vertices[1];
                    throw new NumberFormatException(msg);
                }
            }
        }
    }



    //assign color to all gens in each chromosome randomly.
    //color number are 0,1,2,3
    private void colorPopulation() {
        Random random = new Random();
        for (int chro =0;chro<populationSize;chro++){
            population[chro] = new Genetics.Chromosome(numberOfNodesInChromosome);
            for (int gen=0;gen<numberOfNodesInChromosome;gen++){
                population[chro].genes[gen] = random.nextInt(3);
            }
        }
    }

}
