import java.util.Arrays;

public class SacarScore {
    
    public SacarScore() {
    }      
    public static Integer[][] evaluarSoluciones(Integer[][] sudoku, Integer[][] soluciones) {
        int n = sudoku.length;
        int m = soluciones.length;

        int espaciosVacios = contarEspaciosVacios(sudoku);
        Integer[][] matrizScore = new Integer[m][espaciosVacios];

        for (int iSol = 0; iSol < m; iSol++) {
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
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (sudoku[i][j] == null) {
                        Integer val = tablero[i][j];
                        boolean conflicto = hayConflicto(tablero, i, j, val);
                        if (conflicto) {
                            matrizScore[iSol][idx] = 0;
                        } else {
                            matrizScore[iSol][idx] = 1;
                        }
                        idx++;
                    }
                }
            }
        }

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
        
        Integer[][] matrizScore = evaluarSoluciones(sudoku, soluciones);

        System.out.println("Matriz Score:");
        for (Integer[] fila : matrizScore) {
            System.out.println(Arrays.toString(fila));
        }
    }
}
