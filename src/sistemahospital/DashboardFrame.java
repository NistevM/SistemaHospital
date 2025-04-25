package sistemahospital;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private JButton btnPacientes, btnMedicos, btnDiagnosticos, btnUsuarios;

    public DashboardFrame() {
        super("SGI - Panel Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        btnPacientes = new JButton("Pacientes");
        btnMedicos = new JButton("Médicos");
        btnDiagnosticos = new JButton("Diagnósticos");
        btnUsuarios = new JButton("Usuarios");
        //btnBuscarPaciente = new JButton("Buscar Paciente");

        panel.add(btnPacientes);
        panel.add(btnMedicos);
        panel.add(btnDiagnosticos);
        panel.add(btnUsuarios);
        //panel.add(btnBuscarPaciente);

        add(panel);

        btnPacientes.addActionListener(e -> openTable("paciente"));
        btnMedicos.addActionListener(e -> openTable("medico"));
        btnDiagnosticos.addActionListener(e -> openTable("diagnostico"));
        btnUsuarios.addActionListener(e -> openTable("usuario"));
        //btnBuscarPaciente.addActionListener(e -> {
           // dispose();
           // new BuscarPaciente().setVisible(true);
      //  });
    }

    private void openTable(String tableName) {
        dispose();
        SwingUtilities.invokeLater(() -> new GenericTableFrame(tableName).setVisible(true));
    }
}
