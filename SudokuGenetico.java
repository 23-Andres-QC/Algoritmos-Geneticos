import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SudokuGenetico {
    private List<Individuo> poblacion;
    private final int generaciones;
    private final int poblacionSize;
    private final int[][] puzzle;
    private final int N;
    private final double probMutacion;

    public SudokuGenetico(int[][] puzzle, int poblacionSize, int generaciones, double probMutacion) {
        this.puzzle = puzzle;
        this.N = puzzle.length;
        this.poblacionSize = poblacionSize;
        this.generaciones = generaciones;
        this.probMutacion = probMutacion;
        this.poblacion = new ArrayList<>();
    }

    public List<Individuo> resolverConEvolucion() {
        inicializar();
        List<Individuo> historia = new ArrayList<>();

        for (int gen = 0; gen < generaciones; gen++) {
            List<Individuo> nuevaPoblacion = new ArrayList<>();
            for (int i = 0; i < poblacionSize; i++) {
                Individuo padre1 = seleccionarPadre();
                Individuo padre2 = seleccionarPadre();
                Individuo hijo = cruzar(padre1, padre2);
                mutar(hijo);
                nuevaPoblacion.add(hijo);
            }
            poblacion = nuevaPoblacion;
            Individuo mejor = Collections.max(poblacion, Comparator.comparingInt(i -> i.fitness));
            historia.add(mejor.copiar());
            if (mejor.fitness == fitnessMaximo()) break;
        }
        return historia;
    }

    public void inicializar() {
        for (int i = 0; i < poblacionSize; i++) {
            poblacion.add(new Individuo(puzzle));
        }
    }

    public Individuo seleccionarPadre() {
        // Torneo
        Individuo a = poblacion.get(new java.util.Random().nextInt(poblacionSize));
        Individuo b = poblacion.get(new java.util.Random().nextInt(poblacionSize));
        return a.fitness > b.fitness ? a : b;
    }

    public Individuo cruzar(Individuo p1, Individuo p2) {
        Individuo hijo = p1.copiar();
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < N; i++) {
            if (rand.nextBoolean()) {
                for (int j = 0; j < N; j++) {
                    if (!hijo.fijo[i][j]) hijo.tablero[i][j] = p2.tablero[i][j];
                }
            }
        }
        hijo.calcularFitness();
        return hijo;
    }

    public void mutar(Individuo ind) {
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < N; i++) {
            if (rand.nextDouble() < probMutacion) {
                java.util.List<Integer> libres = new java.util.ArrayList<>();
                for (int j = 0; j < N; j++) {
                    if (!ind.fijo[i][j]) libres.add(j);
                }
                if (libres.size() >= 2) {
                    int a = libres.get(rand.nextInt(libres.size()));
                    int b = libres.get(rand.nextInt(libres.size()));
                    int tmp = ind.tablero[i][a];
                    ind.tablero[i][a] = ind.tablero[i][b];
                    ind.tablero[i][b] = tmp;
                }
            }
        }
        ind.calcularFitness();
    }

    public int fitnessMaximo() {
        return N * 2 * N;
    }
}
