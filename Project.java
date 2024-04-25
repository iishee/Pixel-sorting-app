import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Project implements ActionListener {

    private JFrame window;
    private JButton button;
    private JButton sortButton;
    private JButton downloadButton;
    private JLabel imageLabel;

    public Project() {
        window = new JFrame();
        window.setTitle("Image Sorter");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 800);
        window.setLocationRelativeTo(null);
        window.setLayout(new FlowLayout());

        button = new JButton("Select File");
        window.add(button);
        button.addActionListener(this);

        sortButton = new JButton("Sort");
        window.add(sortButton);
        sortButton.addActionListener(this);

        downloadButton = new JButton("Download");
        window.add(downloadButton);
        downloadButton.addActionListener(this);

        imageLabel = new JLabel();
        window.add(imageLabel);

        window.setVisible(true);
    }

    public void show() {
        window.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage image = ImageIO.read(file);
                    ImageIcon icon = new ImageIcon(image);
                    imageLabel.setIcon(icon);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(window, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == sortButton) {
            sortImagePixels();
        } else if (e.getSource() == downloadButton) {
            saveImage();
        }
    }

    private void sortImagePixels() {
        ImageIcon icon = (ImageIcon) imageLabel.getIcon();
        if (icon == null) {
            JOptionPane.showMessageDialog(window, "No image loaded", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Image image = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

        Arrays.sort(pixels);

        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        imageLabel.setIcon(new ImageIcon(bufferedImage));
    }

    private void saveImage() {
        ImageIcon icon = (ImageIcon) imageLabel.getIcon();
        if (icon == null) {
            JOptionPane.showMessageDialog(window, "No image loaded", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        try {
            // Create a temporary file
            File tempFile = File.createTempFile("sorted_image", ".jpg");

            // Write the image to the temporary file
            ImageIO.write(bufferedImage, "jpg", tempFile);

            // Open the temporary file in the default image viewer
            Desktop.getDesktop().open(tempFile);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(window, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Project::new);
    }
}