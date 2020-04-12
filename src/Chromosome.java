package Genetics;

public class Chromosome {
    public int[] genes;
    public double fitness;

    Chromosome(int numberOfGen){
        genes = new int[numberOfGen];
        fitness =0;
    }

    //change the color of the chromosome by the cross over
    public void changingColor(int[] first,int[] second){
        for (int i=0;i<first.length;i++)
            genes[i] = first[i];

        for (int i=first.length+1 ;i<second.length;i++)
            genes[i]= second[i];
    }
}
