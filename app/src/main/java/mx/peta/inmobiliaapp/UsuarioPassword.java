package mx.peta.inmobiliaapp;

/**
 * Created by rayo on 11/3/16.
 */
public class UsuarioPassword {
    private static UsuarioPassword ourInstance = new UsuarioPassword();

    public static UsuarioPassword getInstance() {
        return ourInstance;
    }

    private UsuarioPassword() {
    }

    String usuario;
    public String getUsuario() { return this.usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    String password;
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

}