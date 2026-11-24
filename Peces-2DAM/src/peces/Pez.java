package peces;

class Pez {
    double x;
    int y;
    int tamano;
    int sentido;

    Pez(double x, int y, int tamano, int sentido) {
        this.x = x;
        this.y = y;
        this.tamano = tamano;
        this.sentido = sentido;
    }

    void mover() {
        x = x + sentido * tamano / 4.0;
    }

    int radio() {
        return 6 + tamano / 2;
    }
}
