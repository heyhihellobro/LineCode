import sun.misc.CRC16;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * Created by Vladimir Rodin.
 * Built on: Thinkpad Workstation W540
 * Date: 16.01.2016
 * Twitter: @heyhihellobro
 */
public class LineCode extends JFrame {

    /* Global Settings */
    public static String WINDOW_TITLE = "Владимир Родин - LineCode";
    public static int WINDOW_WIDTH = 1200;
    public static int WINDOW_HEIGHT = 500;


    protected JPanel navigationPanel;
    protected JLabel radioLabel;

    protected JRadioButton hammingCodeRadioButton;
    protected JRadioButton reedMullerRadioButton;
    protected JRadioButton CRCRadioButton;
    protected JRadioButton hadamarRadioButton;


    protected JPanel inputPanel;
    protected JLabel inputLabel;
    protected JTextArea inputArea;

    protected JPanel outPanel;
    protected JLabel outLabel;
    protected JTextArea outArea;


    protected JPanel buttonPanel;
    protected JButton encodeBtn;
    protected JButton decodeBtn;

//    private JPanel topPanel;
//    private JLabel inputLabel;
//    private JTextArea inputTA;
//
//    private JPanel controlPanel;
//    private JButton encryptBtn, decryptBtn, openFileBtn;
//
//    private JPanel outputPanel;
//    private JLabel outputLabel;
//    private JTextArea outputTA;
//
//    final JTextField textIn = new JTextField();
//    final JTextField textOut = new JTextField();
//    final JTextField textKey = new JTextField();

    LineCode() {
        initUserInterface();
    }

    private void initUserInterface() {

        /* Initialising new window */
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        generateMenuBar();
        generateContent();
    }

    private void generateContent() {

        /* Выбор методов */
        navigationPanel = new JPanel(new FlowLayout());
        radioLabel = new JLabel("Methods: ");
        navigationPanel.add(radioLabel, BorderLayout.NORTH);

        hammingCodeRadioButton = new JRadioButton("Hamming Code");
        reedMullerRadioButton = new JRadioButton("Reed Muller");
        CRCRadioButton = new JRadioButton("CRC32");
        hadamarRadioButton = new JRadioButton("Hadamar Matrix");

        hammingCodeRadioButton.setSelected(true);

        ButtonGroup radioMethods = new ButtonGroup();
        radioMethods.add(hammingCodeRadioButton);
        radioMethods.add(reedMullerRadioButton);
        radioMethods.add(CRCRadioButton);
        radioMethods.add(hadamarRadioButton);

        navigationPanel.add(hammingCodeRadioButton);
        //navigationPanel.add(reedMullerRadioButton);
        navigationPanel.add(CRCRadioButton);
        navigationPanel.add(hadamarRadioButton);

        add(navigationPanel, BorderLayout.NORTH);

        /* Входные данные */
        inputPanel = new JPanel(new FlowLayout());
        inputLabel = new JLabel("Input string: ");
        inputArea = new JTextArea(10, 40);

        inputPanel.add(inputLabel);
        inputPanel.add(inputArea);

        add(inputPanel, BorderLayout.WEST);

        /* Кнопки */
        buttonPanel = new JPanel(new FlowLayout());

        encodeBtn = new JButton("Encode");
        encodeBtn.setMargin(new Insets(13, 13, 13, 13));
        encodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (hammingCodeRadioButton.isSelected()) {

                    String temp = inputArea.getText();
                    System.out.println(temp);

                    String code = convertToBinary(temp);
                    generateHammingCode(code);


                } else if (CRCRadioButton.isSelected()) {
                    String temp = inputArea.getText();
                    System.out.println(temp);

                    byte[] bytes = null;

                    bytes = temp.getBytes();

                    CRC32 crc32 = new CRC32();
                    crc32.update(bytes);


                    System.out.println(crc32.getValue());

                    outArea.setText("" + Long.toHexString(crc32.getValue()));

                } else if (hadamarRadioButton.isSelected()) {
                    String temp = inputArea.getText();
                    System.out.println(temp);

                    int N = Integer.parseInt(temp);

                    HadamardMatrix had = new HadamardMatrix();
                    outArea.setText(had.doHadamarMatrix(N));
                }
            }
        });


        decodeBtn = new JButton("Decode");
        decodeBtn.setMargin(new Insets(13, 13, 13, 13));

        buttonPanel.add(encodeBtn);
        //buttonPanel.add(decodeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        /* Выходные данные */
        outPanel = new JPanel(new FlowLayout());
        outLabel = new JLabel(" :Output");
        outArea = new JTextArea(10, 40);

        outPanel.add(outArea);
        outPanel.add(outLabel);

        add(outPanel, BorderLayout.EAST);


    }

    public String convertToBinary(String string) {

        byte[] bytes = string.getBytes();
        StringBuilder binary = new StringBuilder();

        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }

        return String.valueOf(binary);
    }

    public void generateHammingCode(String code) {

        int powerOfTwo[] = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};
        int lengthOfHammingCode = 0;
        int i = 0;
        boolean controlBits[] = new boolean[code.length() * 2 + 1];
        do {
            controlBits[powerOfTwo[i]] = true;
            i++;
        } while (powerOfTwo[i] <= (code.length() + 1));


        lengthOfHammingCode = code.length() + i;


        /* Введенный код с контрольными битами */
        StringBuilder codeWithControlBytes = new StringBuilder();

        boolean hammingCode[] = new boolean[lengthOfHammingCode];
        int j = 0;
        for (i = 0; i < lengthOfHammingCode; i++) { // Устанавливаем биты для промежуточного кода.
            if (!controlBits[i + 1]) {
                hammingCode[i] = (code.charAt(j) == '1');
                j++;
            }
        }

        codeWithControlBytes.append("Промежуточный код с контрольными битами: [");

        System.out.print("Промежуточный код с контрольными битами: [");
        for (int k = 0; k < lengthOfHammingCode; k++) {
            char c = (hammingCode[k]) ? '1' : '0';
            if (controlBits[k + 1]) {
                System.out.print("!");
                codeWithControlBytes.append("!");
            }
            System.out.print(c);
            codeWithControlBytes.append(c);
        }
        System.out.print("]\n");
        codeWithControlBytes.append("]\n");



        /* Подсчет значений контрольных бит */
        for (i = 0; i < (lengthOfHammingCode - code.length()); i++) { // Цикл проходится по всем контрольным битам.
            boolean nextBit = false;
            for (j = powerOfTwo[i] - 1; j < lengthOfHammingCode; j += powerOfTwo[i] * 2) {
                for (int n = j; n < j + powerOfTwo[i]; n++) {
                    if (n > lengthOfHammingCode - 1) break;
                    nextBit ^= hammingCode[n]; // Ксорим со следующим битом
                }
            }
            hammingCode[powerOfTwo[i] - 1] = nextBit; // Записываем значение i-того бита
        }

        StringBuilder codeWithBytes = new StringBuilder();
        StringBuilder outText = new StringBuilder();

        codeWithBytes.append("Код Хемминга с контрольными битами: [");

        System.out.print("Код Хемминга с контрольными битами: [");
        for (int k = 0; k < lengthOfHammingCode; k++) {
            char c = (hammingCode[k]) ? '1' : '0';
            System.out.print(c);
            codeWithBytes.append(c);
            outText.append(c);
        }
        System.out.print("]\n");

        codeWithBytes.append("]\n");

        outArea.setText(String.valueOf(outText));

        JOptionPane.showMessageDialog(LineCode.this, codeWithControlBytes + "\n" + codeWithBytes);

    }

    private void generateMenuBar() {

        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        //JMenu tools = new JMenu("Tools");
        JMenu help = new JMenu("About");


        /* Exit Item */
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_E);
        exitItem.setToolTipText("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        help.setMnemonic(KeyEvent.VK_F1);
        JMenuItem versionItem = new JMenuItem("Info about program");
        versionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initVersionWindow();
            }
        });
        help.add(versionItem);


        JMenuItem copyItem = new JMenuItem("Copy output text");
        copyItem.setToolTipText("Copy output text");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textToCopy = outArea.getText();
                StringSelection selection = new StringSelection(textToCopy);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });

        file.add(exitItem);
        edit.add(copyItem);
        menubar.add(file);
        menubar.add(edit);
        //menubar.add(tools);
        menubar.add(help);
        setJMenuBar(menubar);

    }


    private void initVersionWindow() {
        JFrame frame = new JFrame("About LineCode");
        frame.setLayout(new GridLayout(1, 1, 5, 5));
        JLabel label = new JLabel("<html><span style='font-size: 16px; text-align: center;'>Version: 1.0 Beta <br> " +
                "Author: Vladimir Rodin <br>" +
                "Twitter: @heyhihellbro <br>" +
                "Website: <a href='http://rodin.xyz/'>http://rodin.xyz/</a> </span></html>");
        label.setHorizontalAlignment(JLabel.CENTER);


        TitledBorder titled = new TitledBorder("About LineCode");
        label.setBorder(titled);


        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(label);
        frame.setSize(800, 400);
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LineCode lineCode = new LineCode();
                lineCode.setVisible(true);
            }
        });
    }


}
