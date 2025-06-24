import java.util.*;

public class Mutacion {
    private static final Random rand = new Random();

    // Calcula el score por fila (cantidad de genes correctos)
    public static int[] calcularScoresPorFila(Integer[][] puntajes) {
        int[] scores = new int[puntajes.length];
        for (int i = 0; i < puntajes.length; i++) {
            int total = 0;
            for (int j = 0; j < puntajes[0].length; j++) {
                if (puntajes[i][j] == 1) total++;
            }
            scores[i] = total;
        }
        return scores;
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

    // Reproduce toda la población cruzando al mejor con todos
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

        // Cruzar el mejor con todos
        Integer[][] nuevaPoblacion = new Integer[nCromosomas][nGenes];
        for (int i = 0; i < nCromosomas; i++) {
            nuevaPoblacion[i] = cruzar(
                poblacion[mejorIndice], puntajes[mejorIndice],
                poblacion[i], puntajes[i], N
            );
        }

        return nuevaPoblacion;
    }
}
