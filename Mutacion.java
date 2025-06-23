import java.util.Scanner;

public class Mutacion {
private static int N;
    
/* 
    public static int[][] NewCromosomas (int[][] Puntajes, int[][] MatrizCromosomas){
        //scores habran 0 , 3
        //MatrizCromo 2 , 3
        Integer[][] NewCromosomas = new Integer[MatrizCromosomas.length][MatrizCromosomas[0].length];
        for(int i = 0 ; i<MatrizCromosomas.length; i++){
            for(int j = 0 ; j<MatrizCromosomas[i].length ; j++){
                    if(Puntajes[i][j] == 1){
                        
                    }else{

                    }
                }
            }
        



        }
*/
    public static void main (String[] args){
        CrearSudoku sudoku = new CrearSudoku();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
        N = scanner.nextInt();
        Integer[][] MSudoku = sudoku.generarSudoku(N);
        sudoku.imprimirTablero(MSudoku);
        Poblacion poblacion = new Poblacion();
        int[][] MPoblacion = poblacion.CrearPoblacion(MSudoku,N);
        poblacion.imprimirMatriz(MPoblacion);
        scanner.close();
    }






}
