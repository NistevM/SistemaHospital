package sistemahospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UsuarioInsertFrame extends JFrame {
    private JTextField txtNombre, txtIdMedico;
    private JPasswordField txtContrasena;
    private JButton btnSave, btnCancel;

    public UsuarioInsertFrame() {
        super("Nuevo Usuario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(400, 200);
        setLocationRelativeTo(null);

        txtNombre      = new JTextField();
        txtContrasena  = new JPasswordField();
        txtIdMedico    = new JTextField();
        btnSave        = new JButton("Guardar");
        btnCancel      = new JButton("Cancelar");

        add(new JLabel("Nombre de Usuario:"));  add(txtNombre);
        add(new JLabel("Contraseña:"));         add(txtContrasena);
        add(new JLabel("ID Médico Asociado:")); add(txtIdMedico);
        add(btnSave);                            add(btnCancel);

        btnSave.addActionListener(e -> saveUsuario());
        btnCancel.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
    }

    private void saveUsuario() {
        String sql = "INSERT INTO usuario(nombre, contrasena, id_medico) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, new String(txtContrasena.getPassword()));
            ps.setInt   (3, Integer.parseInt(txtIdMedico.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario guardado con éxito");
            dispose();
            new GenericTableFrame("usuario").setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}
