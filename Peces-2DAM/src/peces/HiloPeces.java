package peces;

class HiloPeces extends Thread {
    private Tablero tablero;

    HiloPeces(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                tablero.moverPeces();
                tablero.repaint();
                Thread.sleep(30);
            }
        } catch (InterruptedException e) {
            
        }
    }
}
