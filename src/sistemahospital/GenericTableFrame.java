package sistemahospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenericTableFrame extends JFrame {
    private final String tableName;
    private JTable table;
    private DefaultTableModel model;

    public GenericTableFrame(String tableName) {
        super("SGI – " + capitalize(tableName));
        this.tableName = tableName.toLowerCase();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Tabla
        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de botones
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Botón "Nuevo ..."
        JButton btnNew = new JButton("Nuevo " + capitalize(tableName));
        pnl.add(btnNew);
        btnNew.addActionListener(e -> {
            dispose();
            switch (tableName) {
                case "usuario":
                    new UsuarioInsertFrame(null).setVisible(true);
                    break;
                case "paciente":
                    new PacienteInsertFrame(null).setVisible(true);
                    break;
                case "medico":
                    new MedicoInsertFrame(null).setVisible(true);
                    break;
                case "diagnostico":
                    new DiagnosticoInsertFrame(null).setVisible(true);
                    break;
                default:
                    break;
            }
        });

        // --- Nuevo bloque: botón "Buscar Paciente" sólo para la tabla paciente ---
        if (tableName.equals("paciente")) {
            JButton btnBuscar = new JButton("Buscar Paciente");
            pnl.add(btnBuscar);
            btnBuscar.addActionListener(e -> {
                dispose();
                new BuscarPaciente().setVisible(true);
            });
        }

        if (tableName.equals("usuario")) {
            // Botón "Editar Usuario"
            JButton btnEdit = new JButton("Editar Usuario");
            pnl.add(btnEdit);
            btnEdit.addActionListener(e -> editUsuario());

            // Botón "Eliminar Usuario"
            JButton btnDelete = new JButton("Eliminar Usuario");
            pnl.add(btnDelete);
            btnDelete.addActionListener(e -> deleteUsuario());
        }

        // Botón "Volver"
        JButton btnBack = new JButton("Volver");
        pnl.add(btnBack);
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });

        add(pnl, BorderLayout.NORTH);
    }

    private void loadData() {
        model.setRowCount(0);
        model.setColumnCount(0);

        String query = "SELECT * FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            // Decide qué columnas mostrar (ocultar "contrasena" en usuarios)
            List<Integer> showCols = new ArrayList<>();
            for (int i = 1; i <= cols; i++) {
                String label = md.getColumnLabel(i);
                if (tableName.equals("usuario") && label.equalsIgnoreCase("contrasena")) {
                    continue;  // saltar columna de contraseña
                }
                showCols.add(i);
                model.addColumn(label);
            }

            // Rellenar filas solo con columnas visibles
            while (rs.next()) {
                Object[] row = new Object[showCols.size()];
                for (int j = 0; j < showCols.size(); j++) {
                    row[j] = rs.getObject(showCols.get(j));
                }
                model.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error cargando datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUsuario() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un usuario para editar.",
                "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer id = (Integer) model.getValueAt(row, 0);
        dispose();
        new UsuarioInsertFrame(id).setVisible(true);
    }

    private void deleteUsuario() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un usuario para eliminar.",
                "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer id = (Integer) model.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar usuario con ID " + id + "?",
            "Confirmar eliminación",
            JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Usuario eliminado correctamente.");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error eliminando usuario: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
