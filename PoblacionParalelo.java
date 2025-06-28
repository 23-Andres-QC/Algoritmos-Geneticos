import java.util.Random;

public class PoblacionParalelo {
    
    public PoblacionParalelo(){
    }

    public Integer[][] CrearPoblacion(Integer[][] matriz, int N, int Ncromosomas) {
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
        Integer [][]Mcromosomas = CrearMatrizCromosomasParalelo(cromosomas, N);
        System.err.println("Espacios vacíos: " + NespacioVacio);
        return Mcromosomas;
    }

    public Integer[][] CrearMatrizCromosomasParalelo(Integer[][] cromosomas, int N){
        PoolDeHilos poolDeHilos = PoolDeHilos.obtenerInstancia();
        // Crear cada cromosoma en paralelo
        for(int i = 0 ; i < cromosomas.length; i++){
            final int indice = i;
            poolDeHilos.enviar(() -> {
                Random rand = new Random();
                for(int j = 0; j < cromosomas[indice].length; j++){
                    cromosomas[indice][j] = rand.nextInt(N) + 1;  
                }
            });
        }
        // Esperar a que todas las tareas terminen
        poolDeHilos.esperarFinalizacion();
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
