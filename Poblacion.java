import java.util.Random;


public class Poblacion {
    
public Poblacion(){

}

public  Integer[][] CrearPoblacion(Integer[][] matriz, int N, int Ncromosomas){
        int NespacioVacio = 0;
        
        // Contar espacios vacíos
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (matriz[i][j] == null) {
                    NespacioVacio++;
                }
            }
        }
        Integer[][] cromosomas = new Integer[Ncromosomas][NespacioVacio];
        Integer [][]Mcromosomas = CrearMatrizCromosomas(cromosomas,N);
        System.err.println(NespacioVacio);
        return Mcromosomas;
    }
public  Integer [][] CrearMatrizCromosomas(Integer[][] cromosomas,int N){
    Random rand = new Random();
    for(Integer[] cromosoma : cromosomas){
            for(int j = 0 ; j<cromosoma.length ; j++){
                cromosoma[j] = rand.nextInt(N) +1 ;  
                }
        }
    return cromosomas;
}

public  void imprimirMatriz(Integer[][] matriz) {
    for (Integer[] fila : matriz) {
        for (int valor : fila) {
            System.out.printf("[%3d] ", valor);
                
            }
            System.out.println();
        }
    }

}
