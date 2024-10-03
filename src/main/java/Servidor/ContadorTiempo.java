package Servidor;

public class ContadorTiempo implements Runnable {
    private long tiempoInicio;
    private long tiempoFinal;
    private boolean enMarcha;

    public void iniciar() {
        tiempoInicio = System.currentTimeMillis();
        enMarcha = true;
        new Thread(this).start();
    }

    public void detener() {
        enMarcha = false;
        tiempoFinal = System.currentTimeMillis();
    }

    public long getTiempoTotal() {
        return tiempoFinal - tiempoInicio;
    }

    @Override
    public void run() {
        while (enMarcha) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
