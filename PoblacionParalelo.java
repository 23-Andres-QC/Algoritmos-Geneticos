import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class PoblacionParalelo {
    
    public PoblacionParalelo(){
    }

    public Integer[][] CrearPoblacion(Integer[][] matriz, int N){
        Scanner scanner = new Scanner(System.in);
        int NespacioVacio = 0;
        System.out.print("¿Ingrese el numero de cromosomas que desea? ");
        int Ncromosomas = scanner.nextInt();
        
        // Contar espacios vacíos
        for(int i = 0 ; i<N ; i++){
            for(int j = 0 ; j<N ; j++){
                if(matriz[i][j] == null){
                    NespacioVacio++;
                }
            }
        }
        
        Integer[][] cromosomas = new Integer [Ncromosomas][NespacioVacio];
        Integer [][]Mcromosomas = CrearMatrizCromosomasParalelo(cromosomas, N);
        System.err.println("Espacios vacíos: " + NespacioVacio);
        return Mcromosomas;
    }

    public Integer[][] CrearMatrizCromosomasParalelo(Integer[][] cromosomas, int N){
        ThreadPool threadPool = ThreadPool.getInstance();
        CountDownLatch latch = new CountDownLatch(cromosomas.length);
        
        // Crear cada cromosoma en paralelo
        for(int i = 0 ; i < cromosomas.length; i++){
            final int indice = i;
            threadPool.submit(() -> {
                try {
                    Random rand = new Random();
                    for(int j = 0; j < cromosomas[indice].length; j++){
                        cromosomas[indice][j] = rand.nextInt(N) + 1;  
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Esperar a que todas las tareas terminen
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return cromosomas;
    }

    public void imprimirMatriz(Integer[][] matriz) {
        for (Integer[] fila : matriz) {
            for (int valor : fila) {
                System.out.printf("[%3d] ", valor);
            }
            System.out.println();
        }
    }
}
