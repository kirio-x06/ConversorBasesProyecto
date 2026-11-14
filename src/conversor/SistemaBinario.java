package conversor;

public class SistemaBinario extends SistemaNumerico {

    public SistemaBinario() {
        super(2, "Binario");
    }

    @Override
    public boolean validarEntrada(String numeroStr) {
        if (numeroStr == null || numeroStr.isEmpty()) {
            return false;
        }
        for (int i = 0; i < numeroStr.length(); i++) {
            char c = numeroStr.charAt(i);
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}