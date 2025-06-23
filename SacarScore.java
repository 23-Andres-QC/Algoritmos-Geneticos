import java.util.Arrays;

public class SacarScore {
    
    public SacarScore() {
        // Constructor vacío
    }

    public static class Resultado {
        public Integer[][] ais;
        public Integer[] scores;

        public Resultado(Integer[][] ais, Integer[] scores) {
            this.ais = ais;
            this.scores = scores;
        }
    }

    public static Resultado evaluarSoluciones(Integer[][] sudoku, Integer[][] soluciones) {
        int n = sudoku.length;
        int m = soluciones.length;

        int espaciosVacios = contarEspaciosVacios(sudoku);
        Integer[][] ais = new Integer[m][espaciosVacios];
        Integer[] scores = new Integer[m];        for (int iSol = 0; iSol < m; iSol++) {
            Integer[][] tablero = clonarSudoku(sudoku);
            int idx = 0;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (tablero[i][j] == null) {
                        if (idx < soluciones[iSol].length) {
                            tablero[i][j] = soluciones[iSol][idx++];
                        }
                    }
                }
            }
            
            idx = 0;
            int aciertos = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (sudoku[i][j] == null) {
                        Integer val = tablero[i][j];
                        boolean conflicto = hayConflicto(tablero, i, j, val);
                        if (conflicto) {
                            ais[iSol][idx] = 0;
                        } else {
                            ais[iSol][idx] = 1;
                            aciertos++;
                        }
                        idx++;
                    }
                }
            }

            scores[iSol] = aciertos;
        }

        return new Resultado(ais, scores);
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

    // Ejemplo de prueba
    public static void main(String[] args) {
        Integer[][] sudoku = {
            {1, null, 3, null},
            {null, 4, null, 2},
            {null, 1, null, null},
            {4, null, 2, null}
        };


        Integer[][] soluciones = {
            {2, 4, 3, 1, 3, 2, 4, 1, 2},
            {3, 2, 1, 4, 2, 3, 1, 4, 2},
            {4, 3, 2, 1, 1, 2, 3, 4, 2}
        };


        Resultado r = evaluarSoluciones(sudoku, soluciones);

        System.out.println("Matriz AIS:");
        for (Integer[] fila : r.ais) {
            System.out.println(Arrays.toString(fila));
        }

        System.out.println("Scores:");
        System.out.println(Arrays.toString(r.scores));
    }
}
