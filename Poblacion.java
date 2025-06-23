import java.util.Random;
import java.util.Scanner;

public class Poblacion {
    
private static int Ncromosomas;



public static Integer[][] generarMatriz(int n) {
        Integer[][] matriz = new Integer[n][n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 70% de probabilidad de poner un número, 30% de dejarlo vacío (null)
                if (rand.nextDouble() < 0.7) {
                    matriz[i][j] = rand.nextInt(n)+1; // Número entre 0 y 99
                } else {
                    matriz[i][j] = null;
                }
            }
        }

        return matriz;
    }

    public static void imprimirMatriz(Integer[][] matriz) {
        for (Integer[] fila : matriz) {
            for (Integer valor : fila) {
                if (valor == null) {
                    System.out.print("[   ] ");
                } else {
                    System.out.printf("[%3d] ", valor);
                }
            }
            System.out.println();
        }
    }

    public static int[][] CrearPoblacion(int[][] Matriz ){
        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Ingrese el tamaño de la Poblacion");
        int Ncromosomas = scanner.nextInt();
        int[][] 

    }


public static void main(String[] args){
    int n = 5;
    Integer[][] matriz = generarMatriz(n);
    imprimirMatriz(matriz);
    
}


}
