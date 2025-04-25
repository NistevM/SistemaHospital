package sistemahospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class GenericTableFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private String tableName;

    public GenericTableFrame(String tableName) {
        super("SGI - " + capitalize(tableName));
        this.tableName = tableName;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initControls();
        initTable();
        loadData();
        pack();
        setLocationRelativeTo(null);
    }

    private void initControls() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Volver");
        JButton btnNew = new JButton("Añadir " + capitalize(tableName));
        topPanel.add(btnBack);
        topPanel.add(btnNew);
        add(topPanel, BorderLayout.NORTH);

        btnBack.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
        });

        btnNew.addActionListener(e -> {
            dispose();
            openInsertForm();
        });
    }

    private void initTable() {
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        String query;

        if ("usuario".equalsIgnoreCase(tableName)) {
            query = "SELECT id_usuario, nombre, id_medico FROM usuario";
        } else if ("diagnostico".equalsIgnoreCase(tableName)) {
            query = """
                SELECT d.id_diagnostico, d.fecha, d.hora,
                       m.nombre_medico AS medico,
                       d.observaciones,
                       p.nombre_completo AS paciente
                FROM diagnostico d
                JOIN medico m ON d.id_medico = m.id_medico
                JOIN paciente p ON d.id_paciente = p.id_paciente
            """;
        } else {
            query = "SELECT * FROM " + tableName;
        }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            Vector<String> cols = new Vector<>();
            for (int i = 1; i <= colCount; i++) cols.add(meta.getColumnLabel(i));
            model.setColumnIdentifiers(cols);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= colCount; i++) row.add(rs.getObject(i));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
        }
    }

    private void openInsertForm() {
        switch (tableName.toLowerCase()) {
            case "paciente":
                new PacienteInsertFrame().setVisible(true);
                break;
            case "medico":
                new MedicoInsertFrame().setVisible(true);
                break;
            case "diagnostico":
                new DiagnosticoInsertFrame().setVisible(true);
                break;
            case "usuario":
                new UsuarioInsertFrame().setVisible(true);
                break;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
