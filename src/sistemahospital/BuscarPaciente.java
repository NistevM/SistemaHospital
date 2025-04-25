package sistemahospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BuscarPaciente extends JFrame {
    private JTextField txtBuscar;
    private JTextArea txtResultado;
    private JButton btnBuscar, btnVolver;

    public BuscarPaciente() {
        super("Buscar Paciente");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtBuscar = new JTextField();
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(txtResultado);

        btnBuscar = new JButton("Buscar");
        btnVolver = new JButton("Volver");

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("Buscar por nombre o número de documento:"), BorderLayout.NORTH);
        topPanel.add(txtBuscar, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnBuscar);
        btnPanel.add(btnVolver);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);

        btnBuscar.addActionListener(e -> buscarPaciente());
        btnVolver.addActionListener(e -> dispose());
    }

    private void buscarPaciente() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un nombre o número de documento para buscar.");
            return;
        }

        String query = "SELECT p.tipo_documento, p.nro_documento, p.nombre_completo, p.edad, p.genero, " +
                       "d.fecha, d.hora, d.observaciones, m.nombre_medico AS nombre_medico " +
                       "FROM paciente p " +
                       "JOIN diagnostico d ON p.id_paciente = d.id_paciente " +
                       "JOIN medico m ON d.id_medico = m.id_medico " +
                       "WHERE p.nro_documento LIKE ? OR p.nombre_completo LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + termino + "%");
            stmt.setString(2, "%" + termino + "%");

            ResultSet rs = stmt.executeQuery();
            StringBuilder resultado = new StringBuilder();

            while (rs.next()) {
                resultado.append("Tipo de Documento: ").append(rs.getString("tipo_documento")).append("\n");
                resultado.append("Número de Documento: ").append(rs.getString("nro_documento")).append("\n");
                resultado.append("Nombre: ").append(rs.getString("nombre_completo")).append("\n");
                resultado.append("Edad: ").append(rs.getInt("edad")).append("\n");
                resultado.append("Género: ").append(rs.getString("genero")).append("\n");
                resultado.append("Fecha del diagnóstico: ").append(rs.getDate("fecha")).append("\n");
                resultado.append("Hora del diagnóstico: ").append(rs.getString("hora")).append("\n");
                resultado.append("Observaciones: ").append(rs.getString("observaciones")).append("\n");
                resultado.append("Médico: ").append(rs.getString("nombre_medico")).append("\n");
                resultado.append("--------------------------------------------------\n");
            }

            txtResultado.setText(resultado.length() > 0 ? resultado.toString() : "No se encontraron resultados.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar el paciente: " + e.getMessage());
        }
    }
}

