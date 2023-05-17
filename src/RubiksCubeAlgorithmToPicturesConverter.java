import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class RubiksCubeAlgorithmToPicturesConverter {
    private static final int IMAGES_PER_LINE = 4;
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    private static final int CANVAS_PADDING = 10;

    public static void main(String[] args) {
        try {
            List<String> algorithms = readAlgorithmsFromFile("algorithms.txt");

            for (int i = 0; i < algorithms.size(); i++) {
                String algorithm = algorithms.get(i);
                List<BufferedImage> pictures = convertAlgorithmToPictures(algorithm);

                BufferedImage combinedPicture = combinePictures(pictures);
                savePictureToFile(combinedPicture, "combined_picture_" + (i + 1) + ".png");
            }

            System.out.println("Conversion completed successfully.");
        } catch (IOException e) {
            System.out.println("Error reading algorithms from file: " + e.getMessage());
        }
    }

    private static List<String> readAlgorithmsFromFile(String filePath) throws IOException {
        List<String> algorithms = new ArrayList<>();

        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            algorithms.add(line);
        }

        reader.close();

        return algorithms;
    }

    private static List<BufferedImage> convertAlgorithmToPictures(String algorithm) {
        List<BufferedImage> pictures = new ArrayList<>();

        String[] moves = algorithm.split(" ");
        for (String move : moves) {
            int numMoves = getNumMoves(move);
            String moveType = move.substring(0, 1);
            String direction = move.substring(1);

            for (int i = 0; i < numMoves; i++) {
                BufferedImage picture = loadImage(moveType, direction);
                if (picture != null) {
                    pictures.add(picture);
                }
            }
        }

        return pictures;
    }

    private static int getNumMoves(String move) {
        if (move.length() == 2 && move.charAt(1) == '2') {
            return 2;
        } else {
            return 1;
        }
    }

    private static BufferedImage loadImage(String moveType, String direction) {
        String imagePath = "images/" + moveType + direction + ".png";

        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                return ImageIO.read(imageFile);
            } else if (direction.equals("2")) {
                return loadImage(moveType, ""); // Add the picture for the move once
            } else {
                System.out.println("Image file not found: " + imagePath);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static BufferedImage combinePictures(List<BufferedImage> pictures) {
        int numRows = (int) Math.ceil((double) pictures.size() / IMAGES_PER_LINE);
        int canvasWidth = IMAGES_PER_LINE * (IMAGE_WIDTH + CANVAS_PADDING) + CANVAS_PADDING;
        int canvasHeight = numRows * (IMAGE_HEIGHT + CANVAS_PADDING) + CANVAS_PADDING;

        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, canvasWidth, canvasHeight);

        int x = CANVAS_PADDING;
        int y = CANVAS_PADDING;

        for (int i = 0; i < pictures.size(); i++) {
            BufferedImage picture = pictures.get(i);
            graphics.drawImage(picture, x, y, IMAGE_WIDTH, IMAGE_HEIGHT, null);

            x += IMAGE_WIDTH + CANVAS_PADDING;

            if ((i + 1) % IMAGES_PER_LINE == 0) {
                x = CANVAS_PADDING;
                y += IMAGE_HEIGHT + CANVAS_PADDING;
            }
        }

        graphics.dispose();

        return canvas;
    }

    private static void savePictureToFile(BufferedImage picture, String fileName) {
        try {
            File output = new File(fileName);
            ImageIO.write(picture, "png", output);

            System.out.println("Saved picture: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving picture to file: " + e.getMessage());
        }
    }
}