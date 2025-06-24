import java.util.*;

public class Mutacion {
    private static final Random rand = new Random();

<<<<<<< HEAD
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
=======
    // ==== Genera nueva generación con relación padre-hijo ====
    public static Integer[][] NuevaGeneracionExtendidaMatriz(
        Integer[][] poblacion,
        Integer[][] puntajes,
        int N,
        int[][] relacionesPadres // matriz que se llenará con índices de padres
    ) {
        int nCromosomas = poblacion.length;
        int nGenes = poblacion[0].length;

        // === 1. Calcular score por cromosoma
        int[] scores = calcularScoresPorFila(puntajes);
        int maxScore = Arrays.stream(scores).max().getAsInt();

        // === 2. Identificar más aptos y no aptos
        List<Integer> indicesAptos = new ArrayList<>();
        List<Integer> indicesNoAptos = new ArrayList<>();
        for (int i = 0; i < nCromosomas; i++) {
            if (scores[i] == maxScore) indicesAptos.add(i);
            else indicesNoAptos.add(i);
        }

        // === 3. Determinar cantidad total de cromosomas
        int cantidadHijos = (indicesAptos.size() * (indicesAptos.size() - 1)) / 2 +
                            indicesAptos.size() * indicesNoAptos.size();
        int cantidadTotal = cantidadHijos + indicesAptos.size();

        Integer[][] nuevaPoblacion = new Integer[cantidadTotal][nGenes];
        for (int i = 0; i < cantidadTotal; i++) {
            nuevaPoblacion[i] = new Integer[nGenes];
        }

        for (int i = 0; i < cantidadTotal; i++) {
            relacionesPadres[i] = new int[2];
        }

        // === 4. Cruzamiento entre padres aptos
        int idx = 0;
        for (int i = 0; i < indicesAptos.size(); i++) {
            for (int j = i + 1; j < indicesAptos.size(); j++) {
                int p1 = indicesAptos.get(i);
                int p2 = indicesAptos.get(j);
                nuevaPoblacion[idx] = cruzar(poblacion[p1], puntajes[p1], poblacion[p2], puntajes[p2], N);
                relacionesPadres[idx][0] = p1;
                relacionesPadres[idx][1] = p2;
                idx++;
            }
        }

        // === 5. Cruzamiento entre aptos y no aptos
        for (int i : indicesAptos) {
            for (int j : indicesNoAptos) {
                nuevaPoblacion[idx] = cruzar(poblacion[i], puntajes[i], poblacion[j], puntajes[j], N);
                relacionesPadres[idx][0] = i;
                relacionesPadres[idx][1] = j;
                idx++;
            }
        }

        // === 6. Agregar directamente los padres más aptos
        for (int i = 0; i < indicesAptos.size(); i++) {
            int padreIdx = indicesAptos.get(i);
            nuevaPoblacion[idx] = Arrays.copyOf(poblacion[padreIdx], nGenes);
            relacionesPadres[idx][0] = -1;        // Indicador de que es padre directo
            relacionesPadres[idx][1] = padreIdx;
            idx++;
        }

        return nuevaPoblacion;
    }

    // ==== Cruce genético por gen según score ====
    private static Integer[] cruzar(Integer[] padre1, Integer[] score1, Integer[] padre2, Integer[] score2, int N) {
        int nGenes = padre1.length;
        Integer[] hijo = new Integer[nGenes];
        for (int i = 0; i < nGenes; i++) {
            if (score1[i] == 1 && score2[i] == 1) {
                hijo[i] = padre1[i];
            } else if (score1[i] == 1) {
                hijo[i] = padre1[i];
            } else if (score2[i] == 1) {
                hijo[i] = padre2[i];
            } else {
                hijo[i] = rand.nextInt(N) + 1; // mutación
            }
        }
        return hijo;
    }

    // ==== Imprime la nueva generación con relación padre-hijo ====
    public static void imprimirPoblacionConPadres(Integer[][] nuevaPoblacion, int[][] relacionesPadres) {
        for (int i = 0; i < nuevaPoblacion.length; i++) {
            if (relacionesPadres[i][0] == -1) {
                System.out.printf("Padre directo #%d (original %d): ", i + 1, relacionesPadres[i][1]);
            } else {
                System.out.printf("Hijo #%d (de padre %d y padre %d): ", i + 1, relacionesPadres[i][0], relacionesPadres[i][1]);
            }
            for (int val : nuevaPoblacion[i]) {
                System.out.printf("[%2d] ", val);
            }
            System.out.println();
        }
    }

    // ==== Calcula score total por fila (cantidad de 1s) ====
    public static int[] calcularScoresPorFila(Integer[][] puntajes) {
        int[] scores = new int[puntajes.length];
        for (int i = 0; i < puntajes.length; i++) {
            int total = 0;
            for (int j = 0; j < puntajes[i].length; j++) {
                if (puntajes[i][j] != null && puntajes[i][j] == 1) {
                    total++;
                }
            }
            scores[i] = total;
        }
        return scores;
>>>>>>> 5fc5ebadbad1e37819e18efffaf9504696d6a366
    }
}
