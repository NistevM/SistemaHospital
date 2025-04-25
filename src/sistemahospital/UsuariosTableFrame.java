package sistemahospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsuariosTableFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public UsuariosTableFrame() {
        super("Listado de Usuarios");                              // Título de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);                   // Cierra la app al cerrar
        setLayout(new BorderLayout());                             // Usa BorderLayout para la tabla :contentReference[oaicite:5]{index=5}
        initTable();                                               // Construye y añade la tabla
        loadData();                                                // Carga datos desde la BD
        pack();                                                    // Ajusta tamaño al contenido
        setLocationRelativeTo(null);                               // Centra en pantalla
    }

    private void initTable() {
        model = new DefaultTableModel();                           // Modelo vacío :contentReference[oaicite:6]{index=6}
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(table), BorderLayout.CENTER);          // Mete la tabla en un scroll pane :contentReference[oaicite:7]{index=7}
    }

    private void loadData() {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios")) {  // Consulta todos los usuarios

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Cabeceras dinámicas
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = meta.getColumnLabel(i);
            }
            model.setColumnIdentifiers(columnNames);               // Asigna nombres de columna :contentReference[oaicite:8]{index=8}

            // Filas de datos
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);                                   // Añade cada fila al modelo :contentReference[oaicite:9]{index=9}
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar datos: " + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Para pruebas individuales
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuariosTableFrame().setVisible(true));  // Invoca en el EDT :contentReference[oaicite:10]{index=10}
    }
}
