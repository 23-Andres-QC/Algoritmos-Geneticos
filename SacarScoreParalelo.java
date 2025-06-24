import java.util.Arrays;

public class SacarScoreParalelo {

    // Método wrapper para mantener compatibilidad con la interfaz original
    public static Integer[][] evaluarSoluciones(Integer[][] sudoku, Integer[][] soluciones) {
        try {
            return evaluarSolucionesParalelo(sudoku, soluciones);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Evaluación interrumpida", e);
        }
    }

    public static Integer[][] evaluarSolucionesParalelo(Integer[][] sudoku, Integer[][] soluciones) throws InterruptedException {
        int n = sudoku.length;
        int m = soluciones.length;

        int espaciosVacios = contarEspaciosVacios(sudoku);
        Integer[][] matrizScore = new Integer[m][espaciosVacios];

        ThreadPool threadPool = ThreadPool.getInstance();
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(m);

        for (int iSol = 0; iSol < m; iSol++) {
            final int index = iSol;
            threadPool.submit(() -> {
                try {
                    Integer[][] tablero = clonarSudoku(sudoku);
                    int idx = 0;

                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            if (tablero[i][j] == null) {
                                if (idx < soluciones[index].length) {
                                    tablero[i][j] = soluciones[index][idx++];
                                }
                            }
                        }
                    }

                    idx = 0;
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            if (sudoku[i][j] == null) {
                                Integer val = tablero[i][j];
                                boolean conflicto = hayConflicto(tablero, i, j, val);
                                matrizScore[index][idx++] = conflicto ? 0 : 1;
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Espera a que terminen todos los hilos

        return matrizScore;
    }

    private static Integer[][] clonarSudoku(Integer[][] sudoku) {
        int n = sudoku.length;
        Integer[][] copia = new Integer[n][n];
        for (int i = 0; i < n; i++) {
            copia[i] = sudoku[i].clone();
        }
        return copia;
    }

    private static int contarEspaciosVacios(Integer[][] sudoku) {
        int count = 0;
        for (Integer[] fila : sudoku) {
            for (Integer val : fila) {
                if (val == null) count++;
            }
        }
        return count;
    }

    private static boolean hayConflicto(Integer[][] tablero, int fila, int col, Integer val) {
        int n = tablero.length;
        for (int j = 0; j < n; j++) {
            if (j != col && tablero[fila][j] != null && tablero[fila][j].equals(val)) return true;
        }
        for (int i = 0; i < n; i++) {
            if (i != fila && tablero[i][col] != null && tablero[i][col].equals(val)) return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Integer[][] sudoku = {
    { 1, null, null, null,  5, null, null, null,  9, null, null, null, 13, null, null, null },
    { null, 2, null, null,  null, 6, null, null,  null, 10, null, null,  null, 14, null, null },
    { null, null, 3, null,  null, null, 7, null,  null, null, 11, null,  null, null, 15, null },
    { null, null, null, 4,  null, null, null, 8,  null, null, null, 12,  null, null, null, 16 },

    { 5, null, null, null,  1, null, null, null, 13, null, null, null,  9, null, null, null },
    { null, 6, null, null,  null, 2, null, null,  null, 14, null, null,  null, 10, null, null },
    { null, null, 7, null,  null, null, 3, null,  null, null, 15, null,  null, null, 11, null },
    { null, null, null, 8,  null, null, null, 4,  null, null, null, 16,  null, null, null, 12 },

    { 9, null, null, null, 13, null, null, null,  1, null, null, null,  5, null, null, null },
    { null, 10, null, null, null, 14, null, null, null, 2, null, null, null, 6, null, null },
    { null, null, 11, null, null, null, 15, null, null, null, 3, null, null, null, 7, null },
    { null, null, null, 12, null, null, null, 16, null, null, null, 4, null, null, null, 8 },

    {13, null, null, null,  9, null, null, null,  5, null, null, null,  1, null, null, null },
    { null, 14, null, null, null, 10, null, null, null, 6, null, null, null, 2, null, null },
    { null, null, 15, null, null, null, 11, null, null, null, 7, null, null, null, 3, null },
    { null, null, null, 16, null, null, null, 12, null, null, null, 8, null, null, null, 4 }
};


        Integer[][] soluciones = {
    {
        5, 15, 7, 10, 5, 9, 8, 12, 7, 2, 8, 12, 14, 9, 16, 9,
        3, 4, 10, 16, 10, 6, 5, 1, 12, 16, 5, 16, 10, 12, 4, 8,
        15, 7, 13, 3, 9, 4, 15, 9, 3, 13, 14, 1, 6, 11, 1, 16,
        1, 9, 1, 16, 3, 14, 3, 10, 14, 11, 4, 2, 8, 15, 5, 13,
        8, 2, 15, 10, 9, 9, 2, 14, 14, 14, 4, 4, 8, 10, 9, 14,
        9, 3, 7, 16, 14, 11, 16, 6, 1, 4, 14, 4, 3, 12, 11, 2,
        6, 4, 10, 4, 14, 16, 8, 3, 14, 14, 5, 4, 1, 8, 15, 5,
        1, 5, 13, 15, 7, 8, 15, 12, 7, 4, 6, 11, 4, 7, 8, 14,
        9, 9, 6, 14, 12, 10, 15, 7, 2, 4, 1, 5, 13, 11, 4, 4,
        6, 6, 12, 6, 10, 14, 11, 12, 2, 13, 9, 6, 10, 11, 11, 1,
        16, 5, 7, 9, 3, 15, 11, 5, 5, 5, 13, 14, 16, 6, 12, 2,
        1, 10, 9, 10, 4, 14, 2, 8, 3, 16, 6, 3, 5, 5, 8, 12
    },
    {
        15, 9, 2, 4, 14, 3, 6, 6, 4, 1, 5, 4, 8, 7, 1, 9,
        15, 13, 9, 3, 10, 11, 6, 5, 6, 14, 10, 14, 2, 12, 15, 8,
        6, 5, 1, 6, 1, 4, 10, 5, 3, 10, 6, 16, 10, 15, 9, 7,
        9, 6, 8, 4, 9, 8, 4, 3, 7, 1, 11, 1, 6, 14, 5, 7,
        3, 2, 5, 11, 2, 2, 10, 8, 11, 6, 10, 5, 7, 14, 10, 3,
        14, 14, 2, 2, 10, 5, 6, 11, 10, 3, 5, 1, 13, 6, 13, 3,
        5, 5, 6, 13, 12, 7, 7, 16, 7, 14, 13, 3, 12, 15, 10, 6,
        1, 8, 5, 4, 15, 2, 7, 6, 11, 3, 5, 1, 7, 8, 4, 13,
        1, 5, 9, 15, 15, 16, 11, 13, 12, 2, 4, 2, 7, 10, 3, 6,
        9, 6, 6, 6, 16, 15, 1, 6, 9, 11, 2, 12, 16, 2, 16, 2,
        2, 11, 3, 12, 11, 6, 1, 16, 15, 12, 11, 1, 9, 4, 13, 8,
        10, 3, 8, 4, 8, 6, 15, 3, 1, 5, 15, 1, 1, 6, 15, 4
    }
};


        Integer[][] matrizScore = evaluarSolucionesParalelo(sudoku, soluciones);

        System.out.println("Matriz Score:");
        for (Integer[] fila : matrizScore) {
            System.out.println(Arrays.toString(fila));
        }
    }
}
