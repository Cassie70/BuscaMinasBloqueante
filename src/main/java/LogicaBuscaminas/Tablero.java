package LogicaBuscaminas;

import java.io.Serializable;
import java.util.Random;

public class Tablero implements Serializable, Cloneable {

    private Dificultad dificultad;
    private int[][] tablero;
    private boolean[][] mascara;
    private boolean[][] banderas;
    private int minasEncontradas = 0;
    private int banderasColocadas = 0;
    private boolean victoria;
    private boolean derrota;
    private int casillasReveladadas = 0;
    private final int[][] direcciones = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    public Tablero(Dificultad dificultad) {
        this.dificultad = dificultad;
        tablero = new int[dificultad.getFilas()][dificultad.getColumnas()];
        mascara = new boolean[dificultad.getFilas()][dificultad.getColumnas()];
        banderas = new boolean[dificultad.getFilas()][dificultad.getColumnas()];
        colocarMinas();
        colocarPistas();
        victoria = false;
        derrota = false;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    private void colocarMinas() {
        Random random = new Random();
        int i = 0;
        int x, y;
        while (i < dificultad.getMinas()) {
            x = random.nextInt(dificultad.getFilas());
            y = random.nextInt(dificultad.getColumnas());

            if (tablero[x][y] != -1) {
                tablero[x][y] = -1;
                i++;
            }
        }
    }

    private void colocarPistas() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                if (tablero[i][j] == -1) {
                    for (int[] dir : direcciones) {
                        int x = i + dir[0];
                        int y = j + dir[1];
                        if (x >= 0 && x < tablero.length && y >= 0 && y < tablero[i].length && tablero[x][y] != -1)
                            tablero[x][y] += 1;
                    }
                }
            }
        }
    }

    public void imprimirTablero() {
        for (int[] x : tablero) {
            for (int y : x) {
                System.out.print(y + "\t");
            }
            System.out.println();
        }
    }

    public boolean esVisible(int fila, int columna) {
        return mascara[fila][columna];
    }


    public void imprimirJuego() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                if (mascara[i][j]) {
                    if (tablero[i][j] == -1)
                        System.out.print("💣" + "\t");
                    else
                        System.out.print(tablero[i][j] + "\t");
                } else {
                    if (banderas[i][j])
                        System.out.print("🚩" + "\t");
                    else
                        System.out.print("⬜" + "\t");
                }
            }
            System.out.println();
        }
    }

    public void revelarCasilla(int fila, int columna) {
        if (banderas[fila][columna]) {
            return;
        }

        if (!mascara[fila][columna]) {
            mascara[fila][columna] = true;
            casillasReveladadas++;
            if (tablero[fila][columna] == 0) {
                revelarAdyacentes(fila, columna);
            }
            if (tablero[fila][columna] == -1) {
                gameOver();
                derrota = true;
            }
        }
        if (casillasReveladadas == (dificultad.getFilas() * dificultad.getColumnas()) - dificultad.getMinas())
            victoria = true;

    }

    private void revelarAdyacentes(int fila, int columna) {
        for (int[] dir : direcciones) {
            int nuevaFila = fila + dir[0];
            int nuevaColumna = columna + dir[1];


            if (nuevaFila >= 0 && nuevaFila < tablero.length && nuevaColumna >= 0 && nuevaColumna < tablero[0].length) {
                if (!mascara[nuevaFila][nuevaColumna] && tablero[nuevaFila][nuevaColumna] != -1 && !banderas[nuevaFila][nuevaColumna]) {
                    mascara[nuevaFila][nuevaColumna] = true;
                    casillasReveladadas++;

                    if (tablero[nuevaFila][nuevaColumna] == 0) {
                        revelarAdyacentes(nuevaFila, nuevaColumna);
                    }
                }
            }
        }
    }

    public int getValor(int x, int y) {
        return tablero[x][y];
    }

    public boolean tieneBandera(int fila, int columna) {
        return banderas[fila][columna];
    }

    public void alternarBandera(int fila, int columna) {

        if (!mascara[fila][columna] && banderasColocadas < 10) {
            banderas[fila][columna] = !banderas[fila][columna];

            if (banderas[fila][columna]) {
                banderasColocadas++;
            } else {
                banderasColocadas--;
            }
            if (tablero[fila][columna] == -1) {
                if (banderas[fila][columna]) {
                    minasEncontradas++;
                    System.out.println("minas encontradas:" + minasEncontradas);

                    if (minasEncontradas == dificultad.getMinas()) {
                        victoria = true;
                    }
                } else {
                    minasEncontradas--;
                }
            }
        }
    }

    public void gameOver() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                if (tablero[i][j] == -1) {
                    mascara[i][j] = true;
                }
            }
        }
    }

    public boolean isVictoria() {
        return victoria;
    }

    public boolean isDerrota() {
        return derrota;
    }

    @Override
    public Tablero clone() {
        try {
            Tablero clon = (Tablero) super.clone();

            clon.tablero = new int[dificultad.getFilas()][dificultad.getColumnas()];
            clon.mascara = new boolean[dificultad.getFilas()][dificultad.getColumnas()];
            clon.banderas = new boolean[dificultad.getFilas()][dificultad.getColumnas()];

            for (int i = 0; i < tablero.length; i++) {
                clon.tablero[i] = tablero[i].clone();
                clon.mascara[i] = mascara[i].clone();
                clon.banderas[i] = banderas[i].clone();
            }

            return clon;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
