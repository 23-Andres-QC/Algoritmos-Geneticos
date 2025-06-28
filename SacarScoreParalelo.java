public class SacarScoreParalelo {

    public static Integer[][] evaluarSoluciones(Integer[][] sudoku, Integer[][] soluciones) {
        int n = sudoku.length;
        int m = soluciones.length;

        int espaciosVacios = contarEspaciosVacios(sudoku);
        Integer[][] matrizScore = new Integer[m][espaciosVacios];

        PoolDeHilos poolDeHilos = PoolDeHilos.obtenerInstancia();
        // Crear tareas en paralelo
        for (int iSol = 0; iSol < m; iSol++) {
            final int index = iSol;
            poolDeHilos.enviar(() -> {
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
            });
        }
        poolDeHilos.esperarFinalizacion();
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


}
