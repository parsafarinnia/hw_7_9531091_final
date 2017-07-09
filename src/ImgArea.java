/**
 * Created by parsa on 6/2/17.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;

import java.io.*;

import javax.imageio.*;
import javax.imageio.stream.*;

class ImgArea extends Canvas {

    Image orImg;
    BufferedImage orBufferedImage;
    BufferedImage bimg;
    BufferedImage bimg1;
    float e;
    float radian;
    Dimension ds;
    int mX;
    int mY;
    int x;
    int y;
    int x1, x2, y1, y2;
    static boolean imageLoaded;
    boolean actionSlided;
    boolean actionResized;
    boolean actionCompressed;
    boolean actionTransparent;
    boolean actionRotated;
    boolean actionDraw;
    boolean drawn;
    MediaTracker mt;
    static Color c;
    Color colorTextDraw;
    Robot rb;
    boolean dirHor;
    String imgFileName;
    String fontName;
    int fontSize;
    String textToDraw;

    public ImgArea() {

        addMouseListener(new Mousexy());
        addKeyListener(new KList());
        try {
            rb = new Robot();
        } catch (AWTException e) {
        }

        ds = getToolkit().getScreenSize();
        mX = (int) ds.getWidth() / 2;
        mY = (int) ds.getHeight() / 2;

    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (imageLoaded) {

            if (actionSlided || actionResized || actionTransparent || actionRotated || drawn) {
                x = mX - bimg.getWidth() / 2;
                y = mY - bimg.getHeight() / 2;
                g2d.translate(x, y);
                g2d.drawImage(bimg, 0, 0, null); //draw the iamge

            } else {
                x = mX - orBufferedImage.getWidth() / 2;
                y = mY - orBufferedImage.getHeight() / 2;
                g2d.translate(x, y);
                g2d.drawImage(orBufferedImage, 0, 0, null); //draw image
            }
        }
        g2d.dispose();

    }

    class Mousexy extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            Color color = rb.getPixelColor(e.getX(), e.getY()); //get the color at the clicked point
            try {
                setColor(color);
                if (actionDraw) {
                    if (actionSlided || actionResized || actionTransparent || actionRotated || drawn)
                        addTextToImage(e.getX() - x, e.getY() - y, bimg);
                    else
                        addTextToImage(e.getX() - x, e.getY() - y, orBufferedImage);


                }

            } catch (Exception ie) {
            }


        }


    }

    class KList extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 27) {
                actionDraw = false;
                textToDraw = "";
                fontName = "";
                fontSize = 0;
            }
        }
    }

    public void addTextToImage(int x, int y, BufferedImage img) {
        BufferedImage bi = (BufferedImage) createImage(img.getWidth(), img.getHeight());
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.setFont(new Font(fontName, Font.BOLD, fontSize));
        g2d.setPaint(colorTextDraw);
        g2d.drawImage(img, 0, 0, null);
        g2d.drawString(textToDraw, x, y);
        bimg = bi;
        drawn = true;
        g2d.dispose();
        repaint();
    }

    public void setColor(Color color) {
        c = color;
    }

    public void setImgFileName(String fname) {
        imgFileName = fname;
    }

    public void initialize() {
        imageLoaded = false;
        actionSlided = false;
        actionResized = false;
        actionCompressed = false;
        actionTransparent = false;
        actionRotated = false;
        actionDraw = false;
        drawn = false;
        dirHor = false;
        c = null;
        radian = 0.0f;
        e = 0.0f;
    }

    public void reset() {
        if (imageLoaded) {
            prepareImage(imgFileName);
            repaint();
        }

    }

    public void makeImageRotate(BufferedImage image, int w, int h, double degree) {

        BufferedImage bi = (BufferedImage) createImage(w, h);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        radian = (float) Math.toRadians(degree);//angle
        g2d.translate(w / 2, h / 2);
        g2d.rotate(radian);
        g2d.translate(-h / 2, -w / 2);
        g2d.drawImage(image, 0, 0, null);
        bimg = bi;
        g2d.dispose();


    }

    public void rotateImage(double degree) {
        BufferedImage bi;
        if (actionSlided || actionResized || actionTransparent || actionRotated || drawn) {
            bi = bimg;
        } else {
            bi = orBufferedImage;
        }

        makeImageRotate(bi, bi.getHeight(), bi.getWidth(), degree);

        actionRotated = true;
        repaint();

    }

    public void makeCompression(File outFileName) {
        try {
            ImageWriter imgWriter = (ImageWriter) ImageIO.getImageWritersByFormatName("jpg").next();

            ImageOutputStream imgOutStrm = ImageIO.createImageOutputStream(outFileName);

            imgWriter.setOutput(imgOutStrm);

            IIOImage iioImg;
            if (actionSlided || actionResized) { //bimg not a blank buffered image
                iioImg = new IIOImage(bimg, null, null);
            } else {
                iioImg = new IIOImage(orBufferedImage, null, null); //otherwise compress the original buffered image
            }

            ImageWriteParam jpgWriterParam = imgWriter.getDefaultWriteParam();

            jpgWriterParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            jpgWriterParam.setCompressionQuality(0.7f);

            imgWriter.write(null, iioImg, jpgWriterParam);

            imgOutStrm.close();
            imgWriter.dispose();
        } catch (Exception e) {
        }

    }

    public void resizeImage(int w, int h) {
        BufferedImage bi = (BufferedImage) createImage(w, h);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();

        if (actionSlided || actionTransparent || actionRotated || drawn)
            g2d.drawImage(bimg, 0, 0, w, h, null);
        else
            g2d.drawImage(orImg, 0, 0, w, h, null);
        bimg = bi;
        g2d.dispose();

    }

    public void prepareImage(String filename) {
        initialize();
        try {
            mt = new MediaTracker(this);
            orImg = Toolkit.getDefaultToolkit().getImage(filename);
            mt.addImage(orImg, 0);
            mt.waitForID(0);
            int width = orImg.getWidth(null);
            int height = orImg.getHeight(null);
            orBufferedImage = createBufferedImageFromImage(orImg, width, height, false);
            bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageLoaded = true; //now the image is loaded
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    public void filterImage() {
        float[] elements = {0.0f, 1.0f, 0.0f, -1.0f, e, 1.0f, 0.0f, 0.0f, 0.0f};
        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bimg = new BufferedImage(orBufferedImage.getWidth(), orBufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        cop.filter(orBufferedImage, bimg);


    }

    public void setValue(float value) {
        e = value;
    }

    public void setActionSlided(boolean value) {
        actionSlided = value;
    }

    public void setActionResized(boolean value) {
        actionResized = value;
    }

    public void setActionCompressed(boolean value) {
        actionCompressed = value;
    }

    public void setActionDraw(boolean value) {
        actionDraw = value;

    }

    public BufferedImage createBufferedImageFromImage(Image image, int width, int height, boolean tran) {
        BufferedImage dest;
        if (tran)
            dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        else
            dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }

    public void makeTransparency(final Color col) {
        ImageFilter filter = new RGBImageFilter() {
            int imageRGB = col.getRGB();

            public final int filterRGB(int x, int y, int rgb) {
                if (rgb == imageRGB) {

                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip;
        if (actionSlided || actionResized)
            ip = new FilteredImageSource(bimg.getSource(), filter);
        else
            ip = new FilteredImageSource(orImg.getSource(), filter);

        Image img = getToolkit().createImage(ip);
        try {
            mt.addImage(img, 0);
            mt.waitForID(0);
            bimg = createBufferedImageFromImage(img, img.getWidth(null), img.getHeight(null), true);
            actionTransparent = true;
            repaint();
        } catch (Exception e) {
        }
    }

    public void saveToFile(String filename) {
        String ftype = filename.substring(filename.lastIndexOf('.') + 1);
        try {
            if (actionCompressed)
                makeCompression(new File(filename));
            else if (actionSlided || actionResized || actionTransparent || actionRotated || drawn)
                ImageIO.write(bimg, ftype, new File(filename));
        } catch (IOException e) {
            System.out.println("Error in saving the file");
        }
    }

    public void setText(String text, String fName, int fSize, Color color) {
        textToDraw = text;
        fontName = fName;
        fontSize = fSize;
        if (color == null)
            colorTextDraw = new Color(0, 0, 0);
        else
            colorTextDraw = color;
    }
}
