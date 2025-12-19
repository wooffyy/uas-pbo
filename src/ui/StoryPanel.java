package ui;

import ui.util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class StoryPanel extends JPanel {

    private static final Color TEXT_BOX_BG = new Color(20, 10, 10, 200); // Semi-transparent dark red
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Font TEXT_FONT = new Font("Monospaced", Font.PLAIN, 25);
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);

    private BufferedImage backgroundImage;
    private String storyText;
    private int currentScreen;
    private Runnable onNextCallback;

    // Story content array
    // Story content arrays
    public static final String[] INTRO_STORY_TEXTS = {
            // Screen 1
            "Lampu neon berkedip pelan di langit-langit kasino bawah tanah.\n" +
                    "Meja kartu berjajar rapi, chip berserakan, dan layar digital raksasa menampilkan satu angka besar:\n\n"
                    +
                    "TOTAL HUTANG: TERUS MENINGKAT\n\n" +
                    "Alit berdiri di tengah ruangan. Ia tidak datang untuk berjudi. Ia datang karena tidak punya pilihan lain. Hutang yang awalnya untuk makan, sewa, dan bertahan hidup… berubah menjadi jerat tanpa ujung setelah terhubung dengan aplikasi pinjaman ilegal.Satu-satunya jalan keluar yang tersisa adalah permainan ini.\n"
                    +
                    // Screen 2
                    "Kasino ini tidak menggunakan kekerasan fisik.\n" +
                    "Yang dipertaruhkan adalah keputusan, kartu, dan waktu.\n\n" +
                    "Setiap ronde permainan dibagi menjadi dua fase utama:\n\n" +
                    "Phase 1 — Trick-Taking Card Battle\n\n" +
                    "Phase 2 — Shop & Bidding\n\n" +
                    "Semua yang terjadi di sini bertujuan satu:\n" +
                    "mengubah kartu menjadi alat bayar hutang.",

            // Screen 3
            "Permainan kartu dimulai dengan deck standar 52 kartu.\n\n" +
                    "4 suit\n\n" +
                    "A, 2–10, J, Q, K\n\n" +
                    "Di awal setiap ronde:\n\n" +
                    "Pemain (Alit) mendapat 13 kartu acak\n\n" +
                    "Dealer juga mendapat 13 kartu acak\n\n" +
                    "Tidak ada trump suit tetap.\n" +
                    "Suit pertama yang dimainkan dalam sebuah trick menjadi suit wajib.",

            // Screen 4
            "Setiap ronde terdiri dari 13 trick.\n\n" +
                    "Urutan selalu sama:\n\n" +
                    "1. Dealer memainkan satu kartu lebih dulu\n\n" +
                    "2. Pemain membalas dengan satu kartu\n\n" +
                    "3. Dealer menutup trick dengan satu kartu\n\n" +
                    "Penentuan pemenang:\n\n" +
                    "1. Jika pemain bisa mengikuti suit → kartu tertinggi di suit tersebut menang\n\n" +
                    "2. Jika pemain tidak bisa mengikuti suit → pemain otomatis kalah trick\n\n" +
                    "3. Pemenang trick mengambil dua kartu dan menyimpannya ke Trick Pile.",

            // Screen 5
            "Setiap kartu memiliki nilai poin:\n\n" +
                    "A = 1 poin\n\n" +
                    "2–10 = sesuai angka\n\n" +
                    "J, Q, K = 10 poin\n\n" +
                    "Total poin dari semua kartu di Trick Pile akan menjadi:\n\n" +
                    "1. Sumber daya untuk membayar hutang\n\n" +
                    "2. Modal untuk Shop dan Bidding\n\n" +
                    "3. Menang Phase 1 berarti keuntungan besar.\n" +
                    "4. Namun kalah tidak menghentikan permainan, belum.",

            // Screen 6
            "Jika Phase 1 kalah:\n\n" +
                    "1. Nyawa berkurang 1\n\n" +
                    "2. Permainan tetap lanjut ke Phase 2\n\n" +
                    "Tidak ada pilihan untuk mundur.\n" +
                    "Kasino ini tidak mengenal belas kasihan.",

            // Screen 7
            "Setiap ronde:\n\n" +
                    "1. Hutang bertambah bunga\n\n" +
                    "2. Denda keterlambatan terus berjalan\n\n" +
                    "Kemungkinan hasil:\n\n" +
                    "1. Semakin lama permainan berlangsung:\n\n" +
                    "2. Tekanan hutang meningkat\n\n" +
                    "3. Risiko makin tinggi\n\n" +
                    "4. Kesalahan kecil bisa berujung kematian",

            // Screen 8
            "Sisa poin dapat digunakan di Shop.\n\n" +
                    "Shop menyediakan kartu efek common:\n\n" +
                    "1. Harga tetap\n\n" +
                    "2. Tidak ada bidding\n\n" +
                    "3. Efek langsung membantu ronde berikutnya\n\n" +
                    "Contoh efek:\n\n" +
                    "1. Tambahan kartu di ronde selanjutnya\n\n" +
                    "2. Pertukaran kartu acak sebelum ronde dimulai\n\n" +
                    "3. Membuka satu kartu milik dealer\n\n" +
                    "Shop adalah tempat bertahan hidup.\n" +
                    "Bukan tempat menjadi kuat.",

            // Screen 9
            "Tiga kartu melayang di arena khusus.\n\n" +
                    "Setiap kartu memiliki:\n\n" +
                    "Tier: Rare, Super Rare, atau Legendary\n\n" +
                    "Jenis efek terlihat, Kekuatan efek disembunyikan.\n\n" +
                    "Pemain dan dealer:\n\n" +
                    "1. Memilih jumlah poin untuk bidding\n\n" +
                    "2. Poin yang digunakan hilang setelah bidding\n\n" +
                    "Penentuan:\n\n" +
                    "1. Bid tertinggi menang kartu\n\n" +
                    "2. Jika seri → re-bid dengan taruhan 2×",

            // Screen 10
            "Lampu kasino meredup.\n" +
                    "Chip emas berjatuhan di atas meja. Sosok pertama muncul sebagai Intro Boss, dia adalah Dit the Electric. Ia bukan yang terkuat, tapi ia memastikan hanya mereka yang siap kehilangan segalanya yang boleh lanjut. Permainan resmi dimulai di sini. Kartu dikocok. Hutang terus berdetak. Dan Alit melangkah ke pertarungan pertamanya.\n\n"
    };

    public static final String[] STAGE_2_STORY_TEXTS = {
            // SCREEN 11
            "Meja pertama telah dilewati. Tidak ada sorakan kemenangan, tidak ada penyesalan kekalahan. Kasino hanya mencatat satu hal: Alit masih hidup.\n\n"
                    +
                    "Angka hutang di layar tidak berkurang signifikan. Sebagian besar poin habis untuk pembayaran minimal. Yang tersisa hanyalah cukup untuk bertahan… bukan untuk menang.\n\n"
                    +
                    "Lorong kasino berikutnya lebih sunyi. Di dinding, terpampang rekaman pemain-pemain lama yang gagal—mereka yang mencoba bermain aman, dan mereka yang terlalu berani. Semua berakhir sama.",

            // SCREEN 12
            "Permainan berlanjut, tetapi ritmenya berubah. Tidak ada lagi kesan pengenalan atau toleransi. Sistem kini mengasumsikan bahwa pemain telah memahami risikonya.\n\n"
                    +
                    "Setiap kartu yang dimainkan terasa lebih berat. Setiap keputusan membawa konsekuensi jangka panjang. Hutang tidak lagi sekadar angka—ia menjadi batas waktu yang terus menyempit.\n\n"
                    +
                    "Kasino mulai memaksa pemain untuk menunjukkan satu hal: apakah ia mampu berkomitmen pada pilihannya sendiri.",

            // SCREEN 13
            "Area berikutnya tampak terlalu teratur. Meja disusun simetris. Tumpukan kartu sejajar sempurna. Tidak ada elemen acak yang terlihat di permukaan.\n\n"
                    +
                    "Nama SURYA KOMANDO terpampang di tengah meja.\n\n" +
                    "Tidak seperti penjaga sebelumnya, Surya Komando tidak mengandalkan keberuntungan. Ia adalah representasi dari sistem yang percaya bahwa kebebasan memilih hanyalah sumber kesalahan.",

            // SCREEN 14
            "Surya Komando dikenal sebagai arsitek sistem penagihan berbasis tekanan bertahap. Ia merancang mekanisme yang memaksa pengguna aplikasi pinjaman untuk mengeluarkan sumber daya terbaik mereka lebih awal, meninggalkan mereka tanpa pilihan saat krisis datang.\n\n"
                    +
                    "Dalam sistemnya, pemain tidak dibiarkan menyimpan kartu kuat untuk nanti. Semua potensi harus dikeluarkan segera, sebelum sempat menjadi ancaman.\n\n"
                    +
                    "Bagi Surya Komando, kontrol bukan berarti menang cepat—melainkan memastikan lawan kalah perlahan.",

            // SCREEN 15
            "Sejak pertarungan ini dimulai, sistem mencatat tiga trick pertama sebagai fase observasi. Setelah itu, kontrol penuh diambil alih.\n\n"
                    +
                    "Forced Commitment aktif setelah 3 trick pertama Phase 1.\n\n" +
                    "Selama 2 trick berikutnya, pemain dipaksa memainkan kartu tertinggi yang dimiliki, tanpa pengecualian. Tidak ada ruang untuk bluff, tidak ada ruang untuk menahan kekuatan.\n\n"
                    +
                    "Kemampuan ini dirancang untuk menghancurkan perencanaan jangka panjang. Pemain yang terbiasa menyimpan kartu kuat akan kehilangan kendali atas ritme permainan.",

            // SCREEN 16
            "Tekanan berubah bentuk. Jika sebelumnya pemain takut kalah, kini pemain takut menang terlalu cepat. Kartu bernilai tinggi tidak lagi menjadi aset—mereka menjadi beban yang akan dipaksa keluar pada saat terburuk.\n\n"
                    +
                    "Setiap trick setelah aktivasi Forced Commitment terasa seperti kehilangan pilihan. Sistem tidak peduli apakah keputusan itu tepat atau tidak. Yang penting, keputusan itu dibuat sekarang.\n\n"
                    +
                    "Di meja ini, Surya Komando tidak mengalahkan pemain dengan kartu. Ia mengalahkan pemain dengan struktur.",

            // SCREEN 17
            "Saat pertarungan berakhir, tidak ada kepuasan. Bahkan kemenangan terasa hampa. Sebagian besar poin kembali tersedot ke pembayaran hutang, menyisakan ruang gerak yang semakin sempit.\n\n"
                    +
                    "Lorong berikutnya menampilkan grafik-grafik nilai, bunga, dan simulasi kerugian. Semua menunjukkan satu hal: tahap berikutnya tidak lagi memaksa pilihan… melainkan menggerogoti nilai itu sendiri.\n\n"
                    +
                    "Di ujung lorong, satu meja menunggu. Sistem ekonomi yang tidak menghancurkan keputusan—melainkan hasilnya."
    };

    public static final String[] STAGE_3_STORY_TEXTS = {
            // SCREEN 18
            "Meja Surya Komando telah dilewati, namun bekasnya masih terasa. Kartu-kartu bernilai tinggi tidak lagi terasa aman untuk disimpan, dan setiap ronde dimulai dengan rasa kehilangan yang tertunda.\n\n"
                    +
                    "Pembayaran hutang kembali menyerap sebagian besar poin. Angka total memang turun, tetapi bunga terus menggerogoti hasil yang seharusnya menjadi napas lega. Permainan ini tidak pernah memberi ruang untuk pulih sepenuhnya.\n\n"
                    +
                    "Kasino kini tidak lagi menekan pilihan. Ia mulai mengikis hasil.",

            // SCREEN 19
            "Lorong berikutnya dipenuhi layar grafik yang bergerak perlahan. Angka-angka naik dan turun, tetapi setiap kurva berakhir pada garis yang sama: penurunan bertahap.\n\n"
                    +
                    "Sistem di area ini tidak memaksa pemain membuat keputusan buruk. Ia membiarkan pemain membuat keputusan yang terlihat benar, lalu mengurangi nilainya sedikit demi sedikit hingga tidak lagi berarti.\n\n"
                    +
                    "Di ujung lorong, satu meja tampak sederhana. Terlalu sederhana untuk tempat seberbahaya ini.",

            // SCREEN 20
            "Nama RICKY CHINDO tercetak rapi di permukaan meja.\n\n" +
                    "Tidak ada ornamen. Tidak ada intimidasi visual. Meja ini menyerupai meja rapat, bukan meja judi. Segala sesuatu tampak efisien dan terkendali.\n\n"
                    +
                    "Ricky Chindo bukan penjudi, bukan pula pengawas lapangan. Ia adalah simbol optimasi. Seseorang yang percaya bahwa nilai selalu bisa diperas lebih jauh, selama sistemnya berjalan cukup lama.",

            // SCREEN 21
            "Ricky Chindo dikenal sebagai perancang skema penyesuaian nilai internal pada aplikasi pinjaman. Ia tidak menaikkan bunga secara agresif. Ia menurunkan nilai uang itu sendiri.\n\n"
                    +
                    "Dalam sistemnya, pengguna tetap membayar jumlah yang sama, tetap merasa berusaha, tetapi setiap unit usaha bernilai lebih rendah dari sebelumnya. Secara teknis, semuanya sah. Secara praktis, tidak ada yang pernah benar-benar lunas.\n\n"
                    +
                    "Bagi Ricky Chindo, kemenangan tidak diukur dari seberapa besar yang diambil, tetapi dari seberapa sedikit yang tersisa.",

            // SCREEN 22
            "Kemampuan Value Drain aktif saat trick ke-5 Phase 1 dimulai.\n\n" +
                    "Sejak titik itu hingga akhir Phase 1, setiap kartu yang dimainkan pemain mengalami pengurangan nilai sebesar 1 poin, dengan batas minimum 1.\n\n"
                    +
                    "Kartu yang seharusnya menjadi penyelamat berubah menjadi sekadar penunda kekalahan. Akumulasi poin menjadi semakin lambat, sementara kewajiban pembayaran tetap meningkat.",

            // SCREEN 23
            "Value Drain tidak mencuri kartu. Ia mencuri makna dari kartu tersebut. Setiap kemenangan terasa lebih ringan, setiap usaha terasa kurang dihargai.\n\n"
                    +
                    "Pemain masih bisa menang trick. Masih bisa mengumpulkan poin. Namun perbandingan antara usaha dan hasil mulai terasa timpang.\n\n"
                    +
                    "Di meja ini, kekalahan jarang terasa langsung. Ia hadir sebagai kelelahan yang menumpuk.",

            // SCREEN 24
            "Ketika pertarungan berakhir, sistem kembali melakukan perhitungan. Pembayaran minimal tetap wajib dilakukan, tetapi kali ini sisa poin hampir tidak pernah cukup untuk membangun strategi selanjutnya.\n\n"
                    +
                    "Hutang masih ada. Nyawa mungkin tersisa. Namun efisiensi telah diambil.\n\n" +
                    "Kasino membuka jalur terakhir. Tidak ada lagi layar tutorial. Tidak ada lagi simulasi. Yang tersisa hanyalah satu meja, dan satu sistem yang berdiri di atas semua yang sebelumnya.",

            // SCREEN 25
            "Lorong terakhir tidak dipenuhi grafik atau rekaman pemain gagal. Ia kosong, seolah sistem tidak lagi perlu meyakinkan siapa pun.\n\n"
                    +
                    "Di kejauhan, satu meja menyala lebih terang dari yang lain. Tidak ada nama asing di sana. Hanya satu hal yang terasa jelas: semua mekanik sebelumnya mengarah ke titik ini.\n\n"
                    +
                    "Pertarungan berikutnya bukan tentang taktik atau nilai. Ia tentang dominasi penuh."
    };

    public static final String[] STAGE_4_STORY_TEXTS = {
            // SCREEN 26
            "Meja terakhir berdiri terpisah dari seluruh kasino. Tidak ada chip berwarna, tidak ada dekorasi berlebihan. Permukaannya bersih, hampir steril, seolah tempat ini tidak dibuat untuk berjudi, melainkan untuk memastikan hasil.\n\n"
                    +
                    "Angka hutang masih terpampang di layar. Nilainya tidak nol, dan tidak pernah dirancang untuk menjadi nol. Semua mekanik sebelumnya mengarah ke meja ini, mengikis pilihan, nilai, dan waktu, hingga yang tersisa hanyalah inti dari permainan itu sendiri.\n\n"
                    +
                    "Di sinilah sistem berhenti bersembunyi.",

            // SCREEN 27
            "Sosok di seberang meja tidak terasa asing. Wajahnya sama. Gerakannya sama. Bahkan cara memegang kartu terasa identik.\n\n"
                    +
                    "Future Alit adalah hasil dari jalur yang tidak diambil—atau mungkin jalur yang tidak disadari sedang ditempuh sejak awal. Ia adalah versi Alit yang menerima sistem pinjaman, mempelajarinya, lalu memutarnya kembali ke arah yang lebih efisien dan lebih kejam.\n\n"
                    +
                    "Kasino ini bukan ciptaan orang luar. Ia adalah ekosistem yang dibangun dari satu keputusan: bertahan hidup dengan cara apa pun.",

            // SCREEN 28
            "Future Alit pernah berada di titik yang sama. Hutang, tekanan, dan permainan tanpa jalan keluar. Namun alih-alih melawan sistem, ia memilih untuk memahaminya sepenuhnya.\n\n"
                    +
                    "Dengan memahami alur kartu, probabilitas, dan psikologi pemain, ia menciptakan aplikasi pinjaman yang tidak hanya mengambil uang, tetapi juga mengarahkan perilaku. Kasino ini adalah bentuk paling jujur dari sistem tersebut—tanpa UI ramah, tanpa ilusi bantuan.\n\n"
                    +
                    "Bagi Future Alit, kekayaan bukan hasil kemenangan besar, melainkan akumulasi dari kekalahan orang lain yang terstruktur.",

            // SCREEN 29
            "Kemampuan Dominance aktif sejak trick pertama Phase 1.\n\n" +
                    "Sejak awal pertarungan:\n\n" +
                    "Future Alit menentukan suit trump sementara.\n\n" +
                    "Setiap kali Future Alit memainkan suit tersebut, kartunya selalu dianggap lebih tinggi, terlepas dari nilai nominal.\n\n"
                    +
                    "Pengecualian hanya terjadi jika pemain memainkan kartu dengan selisih nilai ≥ 3.\n\n" +
                    "Ability ini tidak menghapus aturan permainan. Ia menekuknya, menciptakan kondisi di mana sistem selalu berpihak pada pengendali awal.",

            // SCREEN 30
            "Semua tekanan terkumpul di satu ronde. Forced Commitment telah mengajarkan kehilangan kontrol. Value Drain telah mengajarkan hilangnya hasil. Dominance menggabungkan keduanya menjadi satu struktur utuh.\n\n"
                    +
                    "Setiap kartu yang dimainkan terasa seperti pengakuan. Tidak ada lagi strategi jangka panjang. Tidak ada lagi harapan membalikkan keadaan di ronde berikutnya.\n\n"
                    +
                    "Di meja ini, kemenangan bukan tentang skor. Ia tentang menolak menjadi bagian dari sistem itu sendiri."
    };

    public static final String[] ENDING_LOSE_TEXT = {
            // SCREEN 31 (ENDING 1: KEKALAHAN)
            "Nyawa terakhir habis. Hutang tidak lagi relevan, karena tidak ada subjek yang tersisa untuk menagihnya.\n\n"
                    +
                    "Kasino tidak runtuh. Aplikasi tidak berhenti. Sistem hanya mengganti satu nama dengan nama berikutnya.\n\n"
                    +
                    "Permainan berakhir.\n" +
                    "GAME OVER."
    };

    public static final String[] ENDING_WIN_BUT_LOSE_TEXT = {
            // SCREEN 31 (ENDING 2: MENANG, NAMUN TETAP KALAH)
            "kartu terakhir jatuh dengan sunyi. Dominance runtuh. Sistem kehilangan pengendalinya.\n\n"
                    +
                    "Namun perhitungan akhir tetap berjalan.\n\n" +
                    "Total poin tidak cukup untuk menutup hutang sepenuhnya. Bunga yang terakumulasi melampaui kemenangan terakhir. Secara teknis, pemain menang. Secara sistem, ia tetap gagal.\n\n"
                    +
                    "Kasino mematikan lampu. Aplikasi menutup akun. Hutang berhenti… karena tidak ada lagi yang bisa ditarik.\n\n"
                    +
                    "Permainan berakhir.\n" +
                    "ENDING: NULL."
    };

    public static final String[] EPILOGUE_TEXT = {
            // SCREEN 32 (EPILOG)
            "Permainan berakhir, tetapi sistemnya tidak. Kasino hanyalah bentuk lain dari mekanisme yang sama: hutang yang dimulai dari kebutuhan, lalu berubah menjadi kendali.\n\n"
                    +
                    "Kartu dan aturan di meja ini mencerminkan dunia nyata—nilai bisa dikurangi, pilihan bisa dipaksa, dan keunggulan selalu berpihak pada sistem.\n\n"
                    +
                    "Tidak semua permainan diciptakan untuk dimenangkan. Beberapa hanya ada untuk memastikan pemain terus bermain.\n\n"
                    +
                    "Kadang, jalan keluar terbaik bukan memenangkan permainan,\n" +
                    "melainkan menyadari bahwa permainan itu sendiri adalah jebakan."
    };

    public StoryPanel(String text, int screenNumber, Runnable onNextCallback) {
        this.currentScreen = screenNumber;
        this.storyText = text;
        this.onNextCallback = onNextCallback;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Load background image
        loadBackgroundImage();

        // Create NEXT button
        JButton nextButton = createNextButton();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Start BGM
        SoundManager.getInstance().play("MainMenu");
    }

    private void loadBackgroundImage() {
        try {
            // Try multiple paths
            File bgFile = new File("src/ui/component/bgUI2.png");
            if (!bgFile.exists()) {
                bgFile = new File("../ui/component/bgUI2.png");
            }
            if (!bgFile.exists()) {
                bgFile = new File("ui/component/bgUI2.png");
            }

            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
            } else {
                System.err.println("Background image not found. Tried: src/ui/component/bgUI2.png");
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JButton createNextButton() {
        JButton button = new JButton("NEXT");
        button.setFont(BUTTON_FONT);
        button.setBackground(new Color(40, 40, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (onNextCallback != null) {
                onNextCallback.run();
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw semi-transparent text box
        int margin = 50;
        int boxX = margin;
        int boxY = margin;
        int boxWidth = getWidth() - (margin * 2);
        int boxHeight = getHeight() - (margin * 2) - 100; // Leave space for button

        g2d.setColor(TEXT_BOX_BG);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Draw border
        g2d.setColor(new Color(100, 50, 50, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Draw text (centered vertically)
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(TEXT_FONT);

        int textX = boxX + 40;
        int textWidth = boxWidth - 80;

        // Calculate text height to center it vertically
        int textHeight = calculateTextHeight(g2d, storyText, textWidth);
        int textY = boxY + (boxHeight - textHeight) / 2;
        if (textY < boxY + 40)
            textY = boxY + 40; // Minimum top margin

        drawJustifiedText(g2d, storyText, textX, textY, textWidth);
    }

    private int calculateTextHeight(Graphics2D g2d, String text, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        // Split by double newlines to match drawJustifiedText logic
        String[] paragraphs = text.split("\n\n");

        int totalHeight = 0;

        for (String paragraph : paragraphs) {
            String cleanPara = paragraph.replace("\n", " ").trim();
            if (cleanPara.isEmpty()) {
                totalHeight += fm.getHeight() / 2;
                continue;
            }

            // Word wrapping calculation
            int lineCount = 0;
            // First word always starts a line if cleanPara is not empty
            // We can simulate wrapping or use a simpler approximation if strict accuracy
            // isn't critical
            // BUT for centering, strict accuracy IS important.

            // Re-implement word wrap logic for height calc
            int lineWidth = 0;
            boolean firstLine = true;

            // If paragraph has text, it has at least one line
            lineCount = 1;

            for (String word : cleanPara.split(" ")) {
                int wordWidth = fm.stringWidth(word);
                int spaceWidth = fm.stringWidth(" ");

                if (firstLine) {
                    lineWidth += wordWidth;
                    firstLine = false;
                } else {
                    if (lineWidth + spaceWidth + wordWidth > maxWidth) {
                        lineCount++;
                        lineWidth = wordWidth; // Next line starts with this word
                    } else {
                        lineWidth += spaceWidth + wordWidth;
                    }
                }
            }

            totalHeight += lineCount * fm.getHeight();
            // Add paragraph spacing match drawJustifiedText
            totalHeight += fm.getHeight() / 2;
        }

        // Remove the last paragraph spacing to be exact?
        // drawJustifiedText adds it at the end of loop, so checking if I should too.
        // Yes, best to match exactly.

        return totalHeight;
    }

    private void drawJustifiedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        // Split by double newlines to get logical paragraphs
        String[] paragraphs = text.split("\n\n");

        int currentY = y;

        for (String paragraph : paragraphs) {
            // Replace single newlines with spaces to let text wrap naturally
            String cleanPara = paragraph.replace("\n", " ").trim();

            if (cleanPara.isEmpty()) {
                continue;
            }

            // 1. Word wrapping
            java.util.List<String[]> lines = new java.util.ArrayList<>();
            java.util.List<String> currentLine = new java.util.ArrayList<>();
            int lineWidth = 0;

            for (String word : cleanPara.split(" ")) {
                int wordWidth = fm.stringWidth(word);
                int spaceWidth = fm.stringWidth(" ");

                if (lineWidth + wordWidth + (currentLine.isEmpty() ? 0 : spaceWidth) > maxWidth) {
                    lines.add(currentLine.toArray(new String[0]));
                    currentLine.clear();
                    lineWidth = 0;
                }

                currentLine.add(word);
                lineWidth += wordWidth + spaceWidth;
            }

            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toArray(new String[0]));
            }

            // 2. Draw lines
            for (int i = 0; i < lines.size(); i++) {
                String[] words = lines.get(i);

                // Draw normal left-aligned if:
                // - Last line of paragraph
                // - Single word line
                // - Only one line in paragraph (unfortunately short paragraphs won't justify,
                // but that's standard)
                boolean isLastLine = (i == lines.size() - 1);

                if (isLastLine || words.length == 1) {
                    g2d.drawString(String.join(" ", words), x, currentY);
                } else {
                    // Justify this line
                    int totalWordWidth = 0;
                    for (String w : words)
                        totalWordWidth += fm.stringWidth(w);

                    int totalSpace = maxWidth - totalWordWidth;
                    int gaps = words.length - 1;

                    if (gaps > 0) {
                        float spaceWidth = (float) totalSpace / gaps;
                        float accumulatedX = x;

                        for (int w = 0; w < words.length; w++) {
                            g2d.drawString(words[w], (int) accumulatedX, currentY);
                            accumulatedX += fm.stringWidth(words[w]);
                            if (w < gaps) {
                                accumulatedX += spaceWidth;
                            }
                        }
                    } else {
                        g2d.drawString(words[0], x, currentY);
                    }
                }
                currentY += fm.getHeight();
            }

            // Add paragraph spacing (e.g., half line height)
            currentY += fm.getHeight() / 2;
        }
    }

    public static int getTotalScreens() {
        return INTRO_STORY_TEXTS.length;
    }
}
