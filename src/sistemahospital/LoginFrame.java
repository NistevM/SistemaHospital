package sistemahospital;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("SGI - Hospital San Juan de Dios");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(30, 30, 80, 25);
        add(lblUser);
        txtUser = new JTextField(); txtUser.setBounds(120, 30, 180, 25); add(txtUser);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, 70, 80, 25);
        add(lblPass);
        txtPass = new JPasswordField(); txtPass.setBounds(120, 70, 180, 25); add(txtPass);

        btnLogin = new JButton("Ingresar");
        btnLogin.setBounds(120, 110, 100, 30);
        add(btnLogin);

        btnLogin.addActionListener(e -> authenticate());
    }

    private void authenticate() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM usuario WHERE nombre = ? AND contrasena = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtUser.getText());
            ps.setString(2, new String(txtPass.getPassword()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               // JOptionPane.showMessageDialog(this, "Acceso garantizado");
                // Dentro de authenticate(), en lugar de new UsuarioFrame():
                new DashboardFrame().setVisible(true);
                dispose();
                             // Cierra la ventana de login
            } else {
                JOptionPane.showMessageDialog(this, "Error de usuario o contraseña");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage());
        }
    }
}