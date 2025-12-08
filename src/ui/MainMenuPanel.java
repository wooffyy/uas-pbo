package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Import untuk ActionListener

// Panel Menu awal
public class MainMenuPanel extends JPanel {

    private static final Color TITLE_COLOR = new Color(255, 200, 0);
    private static final Color BUTTON_BG = new Color(80, 0, 0);
    private static final Color BUTTON_FG = Color.WHITE;

    // Objek Image untuk latar belakang
    private Image backgroundImage;

    // Panel di tengah harus transparan agar gambar latar terlihat
    private final JPanel centerPanel;

    public MainMenuPanel(UIWindow parentFrame) {
        setLayout(new BorderLayout());


        // Memuat gambar dari direktori yang sama dengan class MainMenuPanel (package ui)
        backgroundImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("bgUI.png"));
        super.setBackground(Color.BLACK);


        // 2. Setup Panel Tengah
        centerPanel = new JPanel(new GridBagLayout());
        // JANGAN gunakan setBackground(Color.BLACK). Ganti menjadi transparan:
        centerPanel.setOpaque(false); // **PENTING:** agar latar belakang panel induk terlihat.

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul Game
        JLabel titleLabel = new JLabel("ESCAPE FROM PINJOL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 60));
        titleLabel.setForeground(TITLE_COLOR);

        Dimension buttonSize = new Dimension(300, 70);

        // Tombol Start
        JButton startButton = new JButton("START: RISIKO NYAWA");
        startButton.setPreferredSize(buttonSize);
        startButton.setBackground(BUTTON_BG);
        startButton.setForeground(BUTTON_FG);
        startButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        startButton.setBorder(BorderFactory.createLineBorder(TITLE_COLOR, 2));


        // Tombol Collection
        JButton collectionButton = new JButton("SPECIAL CARD COLLECTION");
        collectionButton.setPreferredSize(buttonSize);
        collectionButton.setBackground(BUTTON_BG); // Mengubah BG agar konsisten
        collectionButton.setForeground(TITLE_COLOR);
        collectionButton.setFont(new Font("Monospaced", Font.PLAIN, 18));
        collectionButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Menambahkan fungsionalitas tombol Collection


        // Penempatan menggunakan GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        centerPanel.add(startButton, gbc);

        gbc.gridy = 2;
        centerPanel.add(collectionButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Metode Overridden untuk menggambar gambar latar belakang.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Panggil super untuk membersihkan area

        // Pastikan gambar sudah dimuat
        if (backgroundImage != null) {
            // Menggambar gambar, meregangkannya agar sesuai dengan ukuran panel.
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}