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

    // Reproduce toda la población cruzando al mejor con todos (incluido consigo mismo)
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

        // Cruzar el mejor con todos (incluido él mismo si quieres mantenerlo)
        Integer[][] nuevaPoblacion = new Integer[nCromosomas][nGenes];
        for (int i = 0; i < nCromosomas; i++) {
            nuevaPoblacion[i] = cruzar(
                poblacion[mejorIndice], puntajes[mejorIndice],
                poblacion[i], puntajes[i], N
            );
        }

        return nuevaPoblacion;
    }

    // Cruza dos cromosomas para formar un hijo
    private static Integer[] cruzar(Integer[] padre1, Integer[] score1, Integer[] padre2, Integer[] score2, int N) {
        int nGenes = padre1.length;
        Integer[] hijo = new Integer[nGenes];
        for (int i = 0; i < nGenes; i++) {
            if (score1[i] == 1 && score2[i] == 1) {
                hijo[i] = padre1[i]; // ambos correctos
            } else if (score1[i] == 1) {
                hijo[i] = padre1[i];
            } else if (score2[i] == 1) {
                hijo[i] = padre2[i];
            } else {
                hijo[i] = rand.nextInt(N) + 1; // aleatorio
            }
        }
        return hijo;
    }
}
