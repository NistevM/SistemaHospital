package sistemahospital;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PacienteInsertFrame extends JFrame {
    private JComboBox<String> comboTipo;
    private JComboBox<String> comboGenero;        // Nuevo combo para sexo
    private JTextField txtNro, txtNombre, txtEdad,
                       txtContacto, txtCorreo, txtDireccion;
    private JButton btnSave, btnCancel;

    public PacienteInsertFrame(Integer id) {
        super("Nuevo Paciente");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 2, 5, 5));
        setSize(450, 350);
        setLocationRelativeTo(null);

        // 1) Tipos de documento
        String[] tipos = {
            "Cédula de ciudadanía",
            "Tarjeta de identidad",
            "Cédula extranjera",
            "Pasaporte"
        };
        comboTipo = new JComboBox<>(tipos);

        // 2) Opciones de sexo
        String[] generos = {
            "Masculino",
            "Femenino",
            "Prefiero no decir"
        };
        comboGenero = new JComboBox<>(generos);

        txtNro       = new JTextField();
        txtNombre    = new JTextField();
        txtEdad      = new JTextField();
        txtContacto  = new JTextField();
        txtCorreo    = new JTextField();
        txtDireccion = new JTextField();
        btnSave      = new JButton("Guardar");
        btnCancel    = new JButton("Cancelar");

        // 3) Asignar filtros de documento
        ((PlainDocument)txtNro.getDocument()).setDocumentFilter(new DigitFilter());
        ((PlainDocument)txtContacto.getDocument()).setDocumentFilter(new DigitFilter());

        // Cambiar filtro de txtNro según tipo
        comboTipo.addActionListener(e -> {
            String sel = (String)comboTipo.getSelectedItem();
            DocumentFilter filter = "Pasaporte".equals(sel)
                ? new AlnumFilter()           // letras y dígitos
                : new DigitFilter();          // solo dígitos
            ((PlainDocument)txtNro.getDocument()).setDocumentFilter(filter);
        });

        // Construir formulario
        add(new JLabel("Tipo Documento:"));  add(comboTipo);
        add(new JLabel("Nro. Documento:"));  add(txtNro);
        add(new JLabel("Nombre Completo:")); add(txtNombre);
        add(new JLabel("Edad:"));            add(txtEdad);
        add(new JLabel("Sexo:"));            add(comboGenero);
        add(new JLabel("Contacto:"));        add(txtContacto);
        add(new JLabel("Correo:"));          add(txtCorreo);
        add(new JLabel("Dirección:"));       add(txtDireccion);
        add(btnSave);                        add(btnCancel);

        // Acciones
        btnSave.addActionListener(e -> savePaciente());
        btnCancel.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
    }

    private void savePaciente() {
        String sql = "INSERT INTO paciente(tipo_documento, nro_documento, nombre_completo, edad, genero, nro_contacto, correo, direccion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (String) comboTipo.getSelectedItem());
            ps.setString(2, txtNro.getText());
            ps.setString(3, txtNombre.getText());
            ps.setInt   (4, Integer.parseInt(txtEdad.getText()));
            ps.setString(5, (String) comboGenero.getSelectedItem());
            ps.setString(6, txtContacto.getText());
            ps.setString(7, txtCorreo.getText());
            ps.setString(8, txtDireccion.getText());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Paciente guardado con éxito");
            dispose();
            new GenericTableFrame("paciente").setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Permite únicamente dígitos (0–9). */
    private static class DigitFilter extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
            throws BadLocationException {
            if (str.chars().allMatch(Character::isDigit))
                super.insertString(fb, offs, str, a);
        }
        @Override public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a)
            throws BadLocationException {
            if (str.chars().allMatch(Character::isDigit))
                super.replace(fb, offs, len, str, a);
        }
    }

    /** Permite letras y dígitos. */
    private static class AlnumFilter extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
            throws BadLocationException {
            if (str.chars().allMatch(ch -> Character.isLetterOrDigit(ch)))
                super.insertString(fb, offs, str, a);
        }
        @Override public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a)
            throws BadLocationException {
            if (str.chars().allMatch(ch -> Character.isLetterOrDigit(ch)))
                super.replace(fb, offs, len, str, a);
        }
    }
}
