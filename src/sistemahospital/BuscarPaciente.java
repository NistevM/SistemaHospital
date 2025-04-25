package sistemahospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuscarPaciente extends JFrame {
    private JTextField txtNroDocumento;
    private JTextField txtNombre;
    private JTextArea  txtResultado;
    private JButton    btnBuscar;
    private JButton    btnVolver;

    public BuscarPaciente() {
        super("Buscar Paciente");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new GridLayout(3, 2, 5, 5));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelTop.add(new JLabel("Número de documento:"));
        txtNroDocumento = new JTextField();
        panelTop.add(txtNroDocumento);

        panelTop.add(new JLabel("Nombre completo:"));
        txtNombre = new JTextField();
        panelTop.add(txtNombre);

        btnBuscar = new JButton("Buscar");
        btnVolver = new JButton("Volver");
        panelTop.add(btnBuscar);
        panelTop.add(btnVolver);

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtResultado,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(panelTop, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscarPaciente());
        btnVolver.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() ->
                new GenericTableFrame("paciente").setVisible(true)
            );
        });
    }

    private void buscarPaciente() {
        String nro    = txtNroDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (nro.isEmpty() && nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese número de documento o nombre para buscar.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Construir cláusula WHERE según criterios
        List<String> condiciones = new ArrayList<>();
        if (!nro.isEmpty())    condiciones.add("p.nro_documento = ?");
        if (!nombre.isEmpty()) condiciones.add("p.nombre_completo LIKE ?");
        String where = String.join(" OR ", condiciones);

        // Cadena SQL con espacios antes de WHERE
        String sql =
            "SELECT " +
            "  p.tipo_documento, p.nro_documento, p.nombre_completo, p.edad, p.genero, " +
            "  d.fecha, d.hora, d.observaciones, m.nombre_medico " +
            "FROM paciente p " +
            "JOIN diagnostico d ON p.id_paciente = d.id_paciente " +
            "JOIN medico m      ON d.id_medico  = m.id_medico " +
            "WHERE " + where;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (!nro.isEmpty()) {
                ps.setString(idx++, nro);
            }
            if (!nombre.isEmpty()) {
                ps.setString(idx, "%" + nombre + "%");
            }

            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append("Tipo de documento:   ").append(rs.getString("tipo_documento")).append("\n");
                sb.append("Número de documento: ").append(rs.getString("nro_documento")).append("\n");
                sb.append("Nombre completo:     ").append(rs.getString("nombre_completo")).append("\n");
                sb.append("Edad:                ").append(rs.getInt   ("edad")).append("\n");
                sb.append("Género:              ").append(rs.getString("genero")).append("\n");
                sb.append("Fecha diagnóstico:   ").append(rs.getDate  ("fecha")).append("\n");
                sb.append("Hora diagnóstico:    ").append(rs.getTime  ("hora")).append("\n");
                sb.append("Observaciones:       ").append(rs.getString("observaciones")).append("\n");
                sb.append("Médico:              ").append(rs.getString("nombre_medico")).append("\n");
                sb.append("--------------------------------------------------------\n");
            }

            txtResultado.setText(sb.length() == 0
                ? "No se encontraron resultados."
                : sb.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar paciente: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Para probar en solitario
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BuscarPaciente().setVisible(true));
    }
}
