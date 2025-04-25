package sistemahospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UsuarioInsertFrame extends JFrame {
    private JTextField txtNombre, txtContrasena;
    private JComboBox<Integer> comboMedicos;
    private JButton btnGuardar, btnEliminar, btnCancelar;
    private Integer usuarioId; // null = crear, no-null = editar

    public UsuarioInsertFrame(Integer id) {
        super(id == null ? "Añadir Usuario" : "Editar Usuario");
        this.usuarioId = id;
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        loadMedicos();
        if (usuarioId != null) {
            loadUsuarioData();
            btnEliminar.setEnabled(true);
        } else {
            btnEliminar.setEnabled(false);
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtNombre      = new JTextField();
        txtContrasena  = new JTextField();
        comboMedicos   = new JComboBox<>();
        btnGuardar     = new JButton(usuarioId == null ? "Guardar" : "Actualizar");
        btnEliminar    = new JButton("Eliminar");
        btnCancelar    = new JButton("Cancelar");

        panel.add(new JLabel("Nombre de Usuario:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel("Médico (ID):"));
        panel.add(comboMedicos);
        panel.add(btnGuardar);
        panel.add(btnEliminar);
        // Añadimos Cancelar en su propia fila
        panel.add(new JLabel());
        panel.add(btnCancelar);

        add(panel);

        btnGuardar.addActionListener(e -> saveUsuario());
        btnEliminar.addActionListener(e -> deleteUsuario());
        btnCancelar.addActionListener(e -> {
            // Cerrar este frame y volver al listado de usuarios
            dispose();
            SwingUtilities.invokeLater(() -> new GenericTableFrame("usuario").setVisible(true));
        });
    }

    private void loadMedicos() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_medico FROM medico")) {
            while (rs.next()) {
                comboMedicos.addItem(rs.getInt("id_medico"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error cargando médicos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsuarioData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT nombre, contrasena, id_medico FROM usuario WHERE id_usuario = ?")) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNombre.setText(rs.getString("nombre"));
                    txtContrasena.setText(rs.getString("contrasena"));
                    comboMedicos.setSelectedItem(rs.getInt("id_medico"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error cargando usuario: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUsuario() {
        String nombre    = txtNombre.getText().trim();
        String clave     = txtContrasena.getText().trim();
        Integer idMedico = (Integer) comboMedicos.getSelectedItem();

        if (nombre.isEmpty() || clave.isEmpty() || idMedico == null) {
            JOptionPane.showMessageDialog(this,
                "Completa todos los campos.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (usuarioId == null) {
                // INSERT
                try (PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO usuario (nombre, contrasena, id_medico) VALUES (?, ?, ?)")) {
                    ps.setString(1, nombre);
                    ps.setString(2, clave);
                    ps.setInt(3, idMedico);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Usuario creado correctamente.");
            } else {
                // UPDATE
                try (PreparedStatement ps = conn.prepareStatement(
                     "UPDATE usuario SET nombre = ?, contrasena = ?, id_medico = ? WHERE id_usuario = ?")) {
                    ps.setString(1, nombre);
                    ps.setString(2, clave);
                    ps.setInt(3, idMedico);
                    ps.setInt(4, usuarioId);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            }

            // Volver al listado de usuarios
            dispose();
            SwingUtilities.invokeLater(() ->
                new GenericTableFrame("usuario").setVisible(true)
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar usuario: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUsuario() {
        if (usuarioId == null) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "¿Eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM usuario WHERE id_usuario = ?")) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");

            // Volver al listado de usuarios
            dispose();
            SwingUtilities.invokeLater(() ->
                new GenericTableFrame("usuario").setVisible(true)
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar usuario: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
