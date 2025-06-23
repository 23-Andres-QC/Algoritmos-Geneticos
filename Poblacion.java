import java.util.Random;
import java.util.Scanner;


public class Poblacion {
    
private static int [][] Mcromosomas;
private static int N;

public static int[][] CrearPoblacion(Integer[][] matriz){
        Scanner scanner = new Scanner(System.in);
        int NespacioVacio = 0;
        System.out.print("¿Ingrese el numero de cromosomas que desea? ");
        int Ncromosomas = scanner.nextInt();

        for(int i = 0 ; i<N ; i++){
            for(int j = 0 ; j<N ; j++){
                if(matriz[i][j] == null){
                    NespacioVacio++;
                }
            }
        }
        int[][] cromosomas = new int[Ncromosomas][NespacioVacio];
        Mcromosomas = CrearMatrizCromosomas(cromosomas);
        System.err.println(NespacioVacio);
        scanner.close();    
        return Mcromosomas;
    }
public static int [][] CrearMatrizCromosomas(int[][] cromosomas){
    Random rand = new Random();
    for(int i = 0 ; i<cromosomas.length; i++){
            for(int j = 0 ; j<cromosomas[i].length ; j++){
                cromosomas[i][j] = rand.nextInt(N) +1 ;  
                }
            }
            return cromosomas;
        }

     public static void imprimirMatriz(int[][] matriz) {
        for (int[] fila : matriz) {
            for (int valor : fila) {
                     System.out.printf("[%3d] ", valor);
                
            }
            System.out.println();
        }
    }
    /* 
public static int [][] Update_Poblacion(int[][] MPoblacion){
    int[][] MPoblacionNew = new int [MPoblacion.length][MPoblacion[0].length];
        for(int i = 0 ; i<N ; i++){
            for(int j = 0 ; j<N ; j++){
                MPoblacion[i][j] =
            }
        }

}
        */
    
public static void main(String[] args){
    CrearSudoku sudoku = new CrearSudoku();
    Scanner scanner = new Scanner(System.in);
    System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
     N = scanner.nextInt();
    Integer[][] MSudoku = sudoku.generarSudoku(N);
    sudoku.imprimirTablero(MSudoku);
    int[][] MPoblacion  = CrearPoblacion(MSudoku);
    imprimirMatriz(MPoblacion);
    scanner.close();    

}


}
