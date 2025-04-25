package sistemahospital;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DiagnosticoInsertFrame extends JFrame {
    private JFormattedTextField txtFecha;
    private JFormattedTextField txtHora;
    private JTextField txtIdMedico;
    private JTextField txtIdPaciente;
    private JTextArea txtObservaciones;
    private JButton btnSave, btnCancel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public DiagnosticoInsertFrame(Integer id) {
        super("Nuevo Diagnóstico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(500, 320);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fecha (DD/MM/AA) con máscara
        try {
            MaskFormatter mf = new MaskFormatter("##/##/##");
            mf.setPlaceholderCharacter('_');
            mf.setAllowsInvalid(false);
            txtFecha = new JFormattedTextField(mf);
        } catch (ParseException e) {
            txtFecha = new JFormattedTextField();
        }
        txtFecha.setColumns(8);
        txtFecha.setFocusLostBehavior(JFormattedTextField.PERSIST);
        txtFecha.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                try {
                    LocalDate date = LocalDate.parse(txtFecha.getText(), DATE_FMT);
                    return !date.isAfter(LocalDate.now());
                } catch (DateTimeParseException ex) {
                    return false;
                }
            }
            @Override
            public boolean shouldYieldFocus(JComponent input) {
                if (!verify(input)) {
                    JOptionPane.showMessageDialog(
                        DiagnosticoInsertFrame.this,
                        "Ingrese una fecha válida (DD/MM/AA).",
                        "Fecha inválida",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return false;
                }
                return true;
            }
        });

        // Hora (HH:MM) con máscara
        try {
            MaskFormatter mf2 = new MaskFormatter("##:##");
            mf2.setPlaceholderCharacter('_');
            mf2.setAllowsInvalid(false);
            txtHora = new JFormattedTextField(mf2);
        } catch (ParseException e) {
            txtHora = new JFormattedTextField();
        }
        txtHora.setColumns(5);
        txtHora.setFocusLostBehavior(JFormattedTextField.PERSIST);
        txtHora.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                try {
                    LocalTime.parse(txtHora.getText(), TIME_FMT);
                    return true;
                } catch (DateTimeParseException ex) {
                    return false;
                }
            }
            @Override
            public boolean shouldYieldFocus(JComponent input) {
                if (!verify(input)) {
                    JOptionPane.showMessageDialog(
                        DiagnosticoInsertFrame.this,
                        "Ingrese una hora válida (HH:MM).",
                        "Hora inválida",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return false;
                }
                return true;
            }
        });

        // IDs solo dígitos
        txtIdMedico   = new JTextField();
        txtIdPaciente = new JTextField();
        ((PlainDocument)txtIdMedico.getDocument()).setDocumentFilter(new DigitFilter());
        ((PlainDocument)txtIdPaciente.getDocument()).setDocumentFilter(new DigitFilter());
        txtIdMedico.setInputVerifier(new NotEmptyVerifier("ID Médico"));
        txtIdPaciente.setInputVerifier(new NotEmptyVerifier("ID Paciente"));

        // Observaciones
        txtObservaciones = new JTextArea(5, 20);
        JScrollPane scrollObs = new JScrollPane(
            txtObservaciones,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // Botones
        btnSave   = new JButton("Guardar");
        btnCancel = new JButton("Cancelar");
        // Permite hacer clic sin disparar verificación de campos
        btnCancel.setFocusable(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Fecha (DD/MM/AA):"), gbc);
        gbc.gridx = 1; add(txtFecha, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Hora (HH:MM):"), gbc);
        gbc.gridx = 1; add(txtHora, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("ID Médico:"), gbc);
        gbc.gridx = 1; add(txtIdMedico, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("ID Paciente:"), gbc);
        gbc.gridx = 1; add(txtIdPaciente, gbc);
        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; add(scrollObs, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 5; gbc.gridx = 0; add(btnSave, gbc);
        gbc.gridx = 1; add(btnCancel, gbc);

        // Acciones
        btnSave.addActionListener(e -> saveDiagnostico());
        btnCancel.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
    }

    private void saveDiagnostico() {
        // Los campos ya se validaron al perder foco
        String fecha  = txtFecha.getText();
        String hora   = txtHora.getText();

        int idMedico, idPaciente;
        try {
            idMedico   = Integer.parseInt(txtIdMedico.getText());
            idPaciente = Integer.parseInt(txtIdPaciente.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "IDs inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO diagnostico(fecha, hora, id_medico, id_paciente, observaciones) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            ps.setString(2, hora);
            ps.setInt(3, idMedico);
            ps.setInt(4, idPaciente);
            ps.setString(5, txtObservaciones.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Diagnóstico guardado con éxito");
            dispose();
            new GenericTableFrame("diagnostico").setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar: " + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Verifica que el text field no esté vació
    private class NotEmptyVerifier extends InputVerifier {
        private final String fieldName;
        NotEmptyVerifier(String name) { fieldName = name; }
        @Override public boolean verify(JComponent input) {
            return !((JTextField)input).getText().trim().isEmpty();
        }
        @Override public boolean shouldYieldFocus(JComponent input) {
            if (!verify(input)) {
                JOptionPane.showMessageDialog(DiagnosticoInsertFrame.this,
                    fieldName + " es obligatorio.", "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
    }

    // Permite solo dígitos
    private static class DigitFilter extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str.chars().allMatch(Character::isDigit)) super.insertString(fb, offs, str, a);
        }
        @Override public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a)
                throws BadLocationException {
            if (str.chars().allMatch(Character::isDigit)) super.replace(fb, offs, len, str, a);
        }
    }
}

