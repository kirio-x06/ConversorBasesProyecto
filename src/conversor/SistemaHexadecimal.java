package conversor;

public class SistemaHexadecimal extends SistemaNumerico {

    public SistemaHexadecimal() {
        super(16, "Hexadecimal");
    }

    @Override
    public boolean validarEntrada(String numeroStr) {
        if (numeroStr == null || numeroStr.isEmpty()) {
            return false;
        }
        String mayusculas = numeroStr.toUpperCase();
        for (int i = 0; i < mayusculas.length(); i++) {
            char c = mayusculas.charAt(i);
            boolean esNumero = (c >= '0' && c <= '9');
            boolean esLetraHex = (c >= 'A' && c <= 'F');
            if (!esNumero && !esLetraHex) {
                return false;
            }
        }
        return true;
    }
}