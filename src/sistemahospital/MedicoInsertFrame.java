package sistemahospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MedicoInsertFrame extends JFrame {
    private JTextField txtNombre, txtEspecialidad;
    private JButton btnSave, btnCancel;

    public MedicoInsertFrame(Integer id) {
        super("Nuevo Médico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 5, 5));
        setSize(400, 200);
        setLocationRelativeTo(null);

        txtNombre      = new JTextField();
        txtEspecialidad= new JTextField();
        btnSave        = new JButton("Guardar");
        btnCancel      = new JButton("Cancelar");

        add(new JLabel("Nombre del Médico:"));      add(txtNombre);
        add(new JLabel("Especialidad:"));           add(txtEspecialidad);
        add(btnSave);                                add(btnCancel);

        btnSave.addActionListener(e -> saveMedico());
        btnCancel.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
    }

    private void saveMedico() {
        String sql = "INSERT INTO medico(nombre_medico, especialidad) VALUES(?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtEspecialidad.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Médico guardado con éxito");
            dispose();
            new GenericTableFrame("medico").setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}