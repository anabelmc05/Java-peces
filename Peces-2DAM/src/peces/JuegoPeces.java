package peces;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class JuegoPeces {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                abrirVentana();
            }
        });
    }

    private static void abrirVentana() {
        String reglas = "Mueve el pez dorado con las flechas. Empiezas con tamaño 5.\n"
                + "Cada punto dorado suma 1. Comer un pez verde suma su tamaño.\n"
                + "Si chocas con un pez igual o mayor, pierdes.\n"
                + "Ganas al llegar a 50.";

        JOptionPane.showMessageDialog(
                null,
                reglas,
                "Bienvenida a Océano",
                JOptionPane.INFORMATION_MESSAGE
        );

        Sonidos sonidos = new Sonidos();
        Tablero tablero = new Tablero(sonidos);
        HiloPeces hiloPeces = new HiloPeces(tablero);
        HiloComida hiloComida = new HiloComida(tablero);

        JButton reiniciar = new JButton("Volver a jugar");
        reiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                tablero.iniciar();
                tablero.requestFocusInWindow();
            }
        });

        JPanel pie = new JPanel();
        pie.add(reiniciar);

        JFrame ventana = new JFrame("Océano");
        ventana.add(tablero, BorderLayout.CENTER);
        ventana.add(pie, BorderLayout.SOUTH);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setResizable(false);
        ventana.pack();
        ventana.setLocationRelativeTo(null);

        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                hiloPeces.interrupt();
                hiloComida.interrupt();
                tablero.cerrar();
            }
        });

        ventana.setVisible(true);
        tablero.requestFocusInWindow();
        hiloPeces.start();
        hiloComida.start();
    }
}