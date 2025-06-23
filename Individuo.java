import java.util.*;

public final class Individuo {
    public int[][] tablero;
    public boolean[][] fijo;
    public int fitness;
    private final int N, SQRT_N;

    public Individuo(int[][] puzzle) {
        this.N = puzzle.length;
        this.SQRT_N = (int) Math.sqrt(N);
        this.tablero = new int[N][N];
        this.fijo = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            Set<Integer> usados = new HashSet<>();
            for (int j = 0; j < N; j++) {
                tablero[i][j] = puzzle[i][j];
                if (puzzle[i][j] != 0) {
                    fijo[i][j] = true;
                    usados.add(puzzle[i][j]);
                }
            }
            // Rellenar fila con valores no usados
            List<Integer> faltantes = new ArrayList<>();
            for (int num = 1; num <= N; num++) {
                if (!usados.contains(num)) faltantes.add(num);
            }
            Collections.shuffle(faltantes);
            int idx = 0;
            for (int j = 0; j < N; j++) {
                if (!fijo[i][j]) tablero[i][j] = faltantes.get(idx++);
            }
        }
        calcularFitness();
    }

    public void calcularFitness() {
        int score = 0;
        for (int j = 0; j < N; j++) {
            Set<Integer> col = new HashSet<>();
            for (int i = 0; i < N; i++) col.add(tablero[i][j]);
            score += col.size();
        }

        for (int i = 0; i < N; i += SQRT_N) {
            for (int j = 0; j < N; j += SQRT_N) {
                Set<Integer> block = new HashSet<>();
                for (int r = i; r < i + SQRT_N; r++) {
                    for (int c = j; c < j + SQRT_N; c++) {
                        block.add(tablero[r][c]);
                    }
                }
                score += block.size();
            }
        }
        this.fitness = score;
    }

    public Individuo copiar() {
        Individuo clone = new Individuo(this.tablero);
        clone.tablero = new int[N][N];
        for (int i = 0; i < N; i++) {
            clone.tablero[i] = this.tablero[i].clone();
            clone.fijo[i] = this.fijo[i].clone();
        }
        clone.fitness = this.fitness;
        return clone;
    }
}
