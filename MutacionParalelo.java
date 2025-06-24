import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MutacionParalelo {
    private static final Random rand = new Random();

    // Calcula el score por fila (cantidad de genes correctos) en paralelo
    public static int[] calcularScoresPorFila(Integer[][] puntajes) {
        ThreadPool threadPool = ThreadPool.getInstance();
        int[] scores = new int[puntajes.length];
        CountDownLatch latch = new CountDownLatch(puntajes.length);
        
        // Calcular score de cada fila en paralelo
        for (int i = 0; i < puntajes.length; i++) {
            final int indice = i;
            threadPool.submit(() -> {
                try {
                    int total = 0;
                    for (int j = 0; j < puntajes[0].length; j++) {
                        if (puntajes[indice][j] == 1) total++;
                    }
                    scores[indice] = total;
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
        
        return scores;
    }

    // Reproduce toda la población cruzando al mejor con todos en paralelo
    public static Integer[][] reproducirPoblacionFija(
            Integer[][] poblacion,
            Integer[][] puntajes,
            int N
    ) {
        int nCromosomas = poblacion.length;
        int nGenes = poblacion[0].length;

        int[] scores = calcularScoresPorFila(puntajes);
        int maxScore = scores[0];
        int mejorIndice = 0;

        // Encontrar el mejor cromosoma (primer mejor si hay empate)
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                mejorIndice = i;
            }
        }

        // Cruzar el mejor con todos en paralelo
        ThreadPool threadPool = ThreadPool.getInstance();
        Integer[][] nuevaPoblacion = new Integer[nCromosomas][nGenes];
        CountDownLatch latch = new CountDownLatch(nCromosomas);
        
        final int mejorIndiceFinal = mejorIndice;
        for (int i = 0; i < nCromosomas; i++) {
            final int indice = i;
            threadPool.submit(() -> {
                try {
                    nuevaPoblacion[indice] = cruzar(
                        poblacion[mejorIndiceFinal], puntajes[mejorIndiceFinal],
                        poblacion[indice], puntajes[indice], N
                    );
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

        return nuevaPoblacion;
    }

    // Cruza dos cromosomas para formar un hijo, preservando genes correctos
    private static Integer[] cruzar(Integer[] padre1, Integer[] score1, Integer[] padre2, Integer[] score2, int N) {
        int nGenes = padre1.length;
        Integer[] hijo = new Integer[nGenes];
        
        for (int i = 0; i < nGenes; i++) {
            // Prioridad 1: Si ambos padres tienen el gen correcto, usar el del padre1
            if (score1[i] == 1 && score2[i] == 1) {
                hijo[i] = padre1[i]; // ambos correctos, tomar del padre1
            } 
            // Prioridad 2: Si solo el padre1 tiene el gen correcto, usarlo
            else if (score1[i] == 1) {
                hijo[i] = padre1[i]; // padre1 correcto
            } 
            // Prioridad 3: Si solo el padre2 tiene el gen correcto, usarlo
            else if (score2[i] == 1) {
                hijo[i] = padre2[i]; // padre2 correcto
            } 
            // Prioridad 4: Si ninguno es correcto, herencia con mutación
            else {
                // 70% probabilidad de heredar del mejor padre, 30% mutación
                if (rand.nextDouble() < 0.7) {
                    hijo[i] = rand.nextBoolean() ? padre1[i] : padre2[i];
                } else {
                    hijo[i] = rand.nextInt(N) + 1; // mutación aleatoria
                }
            }
        }
        return hijo;
    }
}
