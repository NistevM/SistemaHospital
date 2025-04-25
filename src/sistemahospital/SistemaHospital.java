package sistemahospital;

public class SistemaHospital {
    public static void main(String[] args) {
        // Inicia la ventana de Login en el hilo de Swing
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}