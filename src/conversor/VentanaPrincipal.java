package conversor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;

public class VentanaPrincipal extends JFrame {

    // Instancias de nuestras clases de lógica (Polimorfismo y Composición)
    private final SistemaNumerico sistemaBinario = new SistemaBinario();
    private final SistemaNumerico sistemaOctal = new SistemaOctal();
    private final SistemaNumerico sistemaHexadecimal = new SistemaHexadecimal();
    private final ComplementoADos complementoADos = new ComplementoADos();

    public VentanaPrincipal() {
        setTitle("Conversor de Bases, Aritmética Binaria y Complemento a Dos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 620);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(new Font("SansSerif", Font.BOLD, 13));

        pestañas.addTab("Fase 1: Decimal a Otras Bases", crearPanelFase1());
        pestañas.addTab("Fase 2: Otras Bases a Decimal", crearPanelFase2());
        pestañas.addTab("Fase 3: Complemento a Dos (Ca2)", crearPanelFase3());
        pestañas.addTab("Fase 4: Suma y Resta en Ca2", crearPanelFase4());

        add(pestañas);
    }

    /**
     * Panel para la FASE 1: Decimal positivo a Binario, Octal, Hexadecimal o Todas.
     */
    private JPanel crearPanelFase1() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField txtDecimal = new JTextField();
        String[] opciones = {"Todas las bases (Binario, Octal, Hexadecimal)", "Binario (Base 2)", "Octal (Base 8)", "Hexadecimal (Base 16)"};
        JComboBox<String> comboDestino = new JComboBox<>(opciones);
        JButton btnConvertir = new JButton("Convertir (Divisiones Sucesivas)");

        panelSuperior.add(new JLabel("Ingrese un número Decimal entero positivo:"));
        panelSuperior.add(txtDecimal);
        panelSuperior.add(new JLabel("Seleccione la base de destino:"));
        panelSuperior.add(comboDestino);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(btnConvertir);

        JTextArea areaResultados = crearAreaTexto();
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        btnConvertir.addActionListener(e -> {
            String entrada = txtDecimal.getText().trim();
            try {
                long numero = Long.parseLong(entrada);
                if (numero < 0) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un número entero positivo (>= 0).", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                StringBuilder pasos = new StringBuilder();
                int seleccion = comboDestino.getSelectedIndex();

                if (seleccion == 0) { // Todas las bases usando Polimorfismo
                    SistemaNumerico[] sistemas = {sistemaBinario, sistemaOctal, sistemaHexadecimal};
                    StringBuilder resumen = new StringBuilder("=== RESUMEN DE RESULTADOS ===\n");
                    for (SistemaNumerico sis : sistemas) {
                        String res = sis.convertirDesdeDecimalConPasos(numero, pasos);
                        resumen.append("• ").append(sis.getNombreSistema()).append(": ").append(res).append("\n");
                    }
                    areaResultados.setText(resumen.append("\n").append(pasos).toString());
                } else if (seleccion == 1) {
                    String res = sistemaBinario.convertirDesdeDecimalConPasos(numero, pasos);
                    areaResultados.setText("Resultado Binario: " + res + "\n\n" + pasos);
                } else if (seleccion == 2) {
                    String res = sistemaOctal.convertirDesdeDecimalConPasos(numero, pasos);
                    areaResultados.setText("Resultado Octal: " + res + "\n\n" + pasos);
                } else {
                    String res = sistemaHexadecimal.convertirDesdeDecimalConPasos(numero, pasos);
                    areaResultados.setText("Resultado Hexadecimal: " + res + "\n\n" + pasos);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Entrada inválida. Ingrese únicamente dígitos decimales enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Panel para la FASE 2: Binario, Octal o Hexadecimal a Decimal.
     */
    private JPanel crearPanelFase2() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 10, 10));
        String[] basesOrigen = {"Binario (Base 2)", "Octal (Base 8)", "Hexadecimal (Base 16)"};
        JComboBox<String> comboOrigen = new JComboBox<>(basesOrigen);
        JTextField txtNumero = new JTextField();
        JButton btnConvertir = new JButton("Convertir a Decimal (Expansión Polinómica)");

        panelSuperior.add(new JLabel("Seleccione la base de origen:"));
        panelSuperior.add(comboOrigen);
        panelSuperior.add(new JLabel("Ingrese el número a convertir:"));
        panelSuperior.add(txtNumero);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(btnConvertir);

        JTextArea areaResultados = crearAreaTexto();
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        btnConvertir.addActionListener(e -> {
            String entrada = txtNumero.getText().trim().toUpperCase();
            int idx = comboOrigen.getSelectedIndex();

            // Selección polimórfica del sistema numérico
            SistemaNumerico sistemaActual;
            if (idx == 0) sistemaActual = sistemaBinario;
            else if (idx == 1) sistemaActual = sistemaOctal;
            else sistemaActual = sistemaHexadecimal;

            if (!sistemaActual.validarEntrada(entrada)) {
                JOptionPane.showMessageDialog(this,
                        "El valor ingresado no pertenece al sistema " + sistemaActual.getNombreSistema() + " (Base " + sistemaActual.getBase() + ").",
                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder pasos = new StringBuilder();
            long resultadoDecimal = sistemaActual.convertirADecimalConPasos(entrada, pasos);
            areaResultados.setText("Resultado Equivalente en Decimal: " + resultadoDecimal + "\n\n" + pasos);
        });

        return panel;
    }

    /**
     * Panel para la FASE 3: Representación en Complemento a Dos y Verificación.
     */
    private JPanel crearPanelFase3() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField txtNumero = new JTextField();
        String[] bitsOpciones = {"8 bits", "16 bits", "32 bits"};
        JComboBox<String> comboBits = new JComboBox<>(bitsOpciones);
        JButton btnCalcular = new JButton("Generar Complemento a 2 y Verificar");

        panelSuperior.add(new JLabel("Ingrese un número Decimal (positivo o negativo):"));
        panelSuperior.add(txtNumero);
        panelSuperior.add(new JLabel("Seleccione la cantidad de Bits (N):"));
        panelSuperior.add(comboBits);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(btnCalcular);

        JTextArea areaResultados = crearAreaTexto();
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        btnCalcular.addActionListener(e -> {
            try {
                long numero = Long.parseLong(txtNumero.getText().trim());
                int nBits = obtenerBitsSeleccionados(comboBits.getSelectedIndex());

                StringBuilder pasos = new StringBuilder();
                String ca2 = complementoADos.convertirAComplementoDos(numero, nBits, pasos);

                if (ca2 != null) {
                    complementoADos.revertirComplementoDosADecimal(ca2, pasos);
                } else {
                    JOptionPane.showMessageDialog(this, "Overflow: El número no cabe en " + nBits + " bits.", "Overflow", JOptionPane.WARNING_MESSAGE);
                }

                areaResultados.setText(pasos.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número entero válido (ej. -25 o 42).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Panel para la FASE 4: Suma y Resta en Binario usando Complemento a Dos.
     */
    private JPanel crearPanelFase4() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(5, 2, 10, 8));
        JTextField txtNumA = new JTextField();
        JTextField txtNumB = new JTextField();
        String[] operaciones = {"Suma (A + B)", "Resta (A - B)"};
        JComboBox<String> comboOperacion = new JComboBox<>(operaciones);
        String[] bitsOpciones = {"8 bits", "16 bits", "32 bits"};
        JComboBox<String> comboBits = new JComboBox<>(bitsOpciones);
        JButton btnOperar = new JButton("Realizar Operación Binaria en Ca2");

        panelSuperior.add(new JLabel("Primer Número Decimal (A):"));
        panelSuperior.add(txtNumA);
        panelSuperior.add(new JLabel("Segundo Número Decimal (B):"));
        panelSuperior.add(txtNumB);
        panelSuperior.add(new JLabel("Operación Aritmética:"));
        panelSuperior.add(comboOperacion);
        panelSuperior.add(new JLabel("Número de Bits (N):"));
        panelSuperior.add(comboBits);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(btnOperar);

        JTextArea areaResultados = crearAreaTexto();
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        btnOperar.addActionListener(e -> {
            try {
                long numA = Long.parseLong(txtNumA.getText().trim());
                long numB = Long.parseLong(txtNumB.getText().trim());
                boolean esResta = comboOperacion.getSelectedIndex() == 1;
                int nBits = obtenerBitsSeleccionados(comboBits.getSelectedIndex());

                StringBuilder pasos = new StringBuilder();
                complementoADos.operarEnComplementoDos(numA, numB, esResta, nBits, pasos);
                areaResultados.setText(pasos.toString());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese números enteros válidos en ambos campos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private int obtenerBitsSeleccionados(int index) {
        if (index == 0) return 8;
        if (index == 1) return 16;
        return 32;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setMargin(new Insets(10, 10, 10, 10));
        return area;
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}