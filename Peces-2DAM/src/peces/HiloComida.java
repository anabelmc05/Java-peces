package peces;

class HiloComida extends Thread {
    private Tablero tablero;

    HiloComida(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Thread.sleep(800);
                tablero.anadirComida();
            }
        } catch (InterruptedException e) {
            
        }
    }
}
