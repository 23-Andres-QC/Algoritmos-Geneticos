public class MiCountDownLatch {
    private int contador;

    public MiCountDownLatch(int cuentaInicial) {
        this.contador = cuentaInicial;
    }

    public synchronized void countDown() {
        contador--;
        if (contador <= 0) {
            notifyAll();
        }
    }

    public synchronized void await() throws InterruptedException {
        while (contador > 0) {
            wait();
        }
    }
}
