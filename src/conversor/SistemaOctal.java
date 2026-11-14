package conversor;

public class SistemaOctal extends SistemaNumerico {

    public SistemaOctal() {
        super(8, "Octal");
    }

    @Override
    public boolean validarEntrada(String numeroStr) {
        if (numeroStr == null || numeroStr.isEmpty()) {
            return false;
        }
        for (int i = 0; i < numeroStr.length(); i++) {
            char c = numeroStr.charAt(i);
            if (c < '0' || c > '7') {
                return false;
            }
        }
        return true;
    }
}