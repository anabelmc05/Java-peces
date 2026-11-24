package peces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

class Tablero extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int ANCHO = 650;
	private static final int ALTO = 400;
	private static final int BORDE_SUPERIOR = 35;
	private static final int META = 50;

	private Image fondo = getToolkit().getImage(getClass().getResource("/imagenes/oceano.png"));
	private Image pezDorado = getToolkit().getImage(getClass().getResource("/imagenes/jugador.png"));
	private Image pezVerde = getToolkit().getImage(getClass().getResource("/imagenes/presa.png"));
	private Image pezRojo = getToolkit().getImage(getClass().getResource("/imagenes/peligro.png"));

	private ArrayList<Pez> peces = new ArrayList<Pez>();
	private ArrayList<Point> comidas = new ArrayList<Point>();
	private Random aleatorio = new Random();
	private Sonidos sonidos;

	private Pez jugador;
	private int turno;
	private boolean jugando;
	private String mensaje;

	Tablero(Sonidos sonidos) {
		this.sonidos = sonidos;
		setPreferredSize(new Dimension(ANCHO, ALTO));
		setBackground(new Color(167, 224, 222));

		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evento) {
				moverJugador(evento.getKeyCode());
			}
		});
		iniciar();
	}

	synchronized void iniciar() {
		jugador = new Pez(ANCHO / 2, ALTO / 2, 5, 1);
		peces.clear();
		comidas.clear();
		turno = 0;
		mensaje = "";
		jugando = true;

		for (int i = 0; i < 6; i++) {
			anadirComida();
			anadirPez();
		}
		repaint();
	}

	synchronized void anadirComida() {
		if (jugando && comidas.size() < 10) {
			int x = 20 + aleatorio.nextInt(ANCHO - 40);
			int y = alturaAleatoria();
			comidas.add(new Point(x, y));
		}
	}

	private void anadirPez() {
		int tamano = 2 + aleatorio.nextInt(18);
		int sentido = 1;
		int x = -30;
		if (aleatorio.nextInt(2) == 1) {
			sentido = -1;
			x = ANCHO + 30;
		}
		peces.add(new Pez(x, alturaAleatoria(), tamano, sentido));
	}

	private int alturaAleatoria() {
		return BORDE_SUPERIOR + 20 + aleatorio.nextInt(ALTO - BORDE_SUPERIOR - 40);
	}

	synchronized void moverPeces() {
		if (!jugando) {
			return;
		}

		for (int i = peces.size() - 1; i >= 0 && jugando; i--) {
			Pez pez = peces.get(i);
			pez.mover();

			if (pez.x < -32 || pez.x > ANCHO + 32) {
				peces.remove(i);
			} else if (chocan(pez.x, pez.y, pez.radio())) {
				if (pez.tamano < jugador.tamano) {
					peces.remove(i);
					comer(pez.tamano);
				} else {
					jugando = false;
					mensaje = "Te ha comido un pez de tamaño " + pez.tamano;
					sonidos.derrota();
				}
			}
		}

		for (int i = comidas.size() - 1; i >= 0 && jugando; i--) {
			Point comida = comidas.get(i);
			if (chocan(comida.x, comida.y, 5)) {
				comidas.remove(i);
				comer(1);
			}
		}

		turno++;
		if (turno == 25) {
			if (jugando && peces.size() < 12) {
				anadirPez();
			}
			turno = 0;
		}
	}

	private synchronized void moverJugador(int tecla) {
		if (!jugando) {
			return;
		}

		int paso = 6;
		int radio = jugador.radio();

		switch (tecla) {
		case KeyEvent.VK_LEFT:
			jugador.x = Math.max(radio, jugador.x - paso);
			jugador.sentido = -1;
			break;
		case KeyEvent.VK_RIGHT:
			jugador.x = Math.min(ANCHO - radio, jugador.x + paso);
			jugador.sentido = 1;
			break;
		case KeyEvent.VK_UP:
			jugador.y = Math.max(BORDE_SUPERIOR + radio, jugador.y - paso);
			break;
		case KeyEvent.VK_DOWN:
			jugador.y = Math.min(ALTO - radio, jugador.y + paso);
			break;
		default:
			break;
		}
	}

	private boolean chocan(double x, int y, int radio) {
		double diferenciaX = jugador.x - x;
		int diferenciaY = jugador.y - y;
		int sumaRadios = jugador.radio() + radio;
		return diferenciaX * diferenciaX + diferenciaY * diferenciaY < sumaRadios * sumaRadios;
	}

	private void comer(int valor) {
		jugador.tamano = jugador.tamano + valor;
		sonidos.comer();

		if (jugador.tamano >= META) {
			jugando = false;
			mensaje = "¡Has ganado! Tamaño final: " + jugador.tamano;
		}
	}

	synchronized void cerrar() {
		jugando = false;
		sonidos.cerrar();
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(fondo, 0, BORDE_SUPERIOR, ANCHO, ALTO - BORDE_SUPERIOR, this);

		g.setColor(new Color(255, 211, 107));
		for (Point comida : comidas) {
			g.fillOval(comida.x - 5, comida.y - 5, 10, 10);
		}

		for (Pez pez : peces) {
			if (pez.tamano < jugador.tamano) {
				dibujarPez(g, pez, pezVerde);
			} else {
				dibujarPez(g, pez, pezRojo);
			}
		}
		dibujarPez(g, jugador, pezDorado);

		g.setColor(new Color(9, 29, 49));
		g.fillRect(0, 0, ANCHO, BORDE_SUPERIOR);
		g.setColor(Color.WHITE);
		g.drawString("Tu tamaño: " + jugador.tamano + "     Meta: " + META, 12, 22);
		if (!jugando) {
			g.drawString(mensaje, 280, 22);
		}
	}

	private void dibujarPez(Graphics g, Pez pez, Image imagen) {
		int radio = pez.radio();
		int ancho = radio * 3;
		int alto = radio * 2;
		int x = (int) pez.x - ancho / 2;
		int y = pez.y - alto / 2;

		if (pez.sentido == 1) {
			g.drawImage(imagen, x, y, ancho, alto, this);
		} else {
			
			g.drawImage(imagen, x + ancho, y, -ancho, alto, this);
		}

		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(pez.tamano), (int) pez.x - 5, y);
	}
}
