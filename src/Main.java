/**
 * Created by parsa on 6/2/17.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import javax.imageio.*;
import javax.imageio.stream.*;

class  Main extends JFrame implements ActionListener{

    ImgArea ia;
    JFileChooser chooser;
    JMenuBar mainmenu;
    JMenu menu;
    JMenu editmenu;
    JMenuItem mopen;
    JMenuItem msaveas;
    JMenuItem msave;
    JMenuItem mexit;
    JMenuItem mbright;
    JMenuItem mcompress;
    JMenuItem mresize;
    JMenuItem mrotate;
    JMenuItem mtransparent;
    JMenuItem maddtext;
    JMenuItem mcancel;
    String filename;
    Main(){
        ia=new ImgArea();
        Container cont=getContentPane();
        cont.add(ia,BorderLayout.CENTER );
        mainmenu=new JMenuBar();
        menu=new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        mopen=new JMenuItem("Open...");
        mopen.setMnemonic(KeyEvent.VK_O);
        mopen.addActionListener(this);

        msaveas=new JMenuItem("Save as...");
        msaveas.setMnemonic(KeyEvent.VK_S);
        msaveas.addActionListener(this);

        msave=new JMenuItem("Save");
        msave.setMnemonic(KeyEvent.VK_V);
        msave.addActionListener(this);

        mexit=new JMenuItem("Exit");
        mexit.setMnemonic(KeyEvent.VK_X);
        mexit.addActionListener(this);
        menu.add(mopen);
        menu.add(msaveas);
        menu.add(msave);
        menu.add(mexit);

        editmenu=new JMenu("Edit");
        editmenu.setMnemonic(KeyEvent.VK_E);
        mbright=new JMenuItem("Image brightness");
        mbright.setMnemonic(KeyEvent.VK_B);
        mbright.addActionListener(this);

        maddtext=new JMenuItem("Add text on image");
        maddtext.setMnemonic(KeyEvent.VK_A);
        maddtext.addActionListener(this);

        mresize=new JMenuItem("Image resize");
        mresize.setMnemonic(KeyEvent.VK_R);
        mresize.addActionListener(this);

        mcompress=new JMenuItem("Image compression");
        mcompress.setMnemonic(KeyEvent.VK_P);
        mcompress.addActionListener(this);

        mrotate=new JMenuItem("Image rotation");
        mrotate.setMnemonic(KeyEvent.VK_T);
        mrotate.addActionListener(this);

        mtransparent=new JMenuItem("Image transparency");
        mtransparent.setMnemonic(KeyEvent.VK_T);
        mtransparent.addActionListener(this);

        mcancel=new JMenuItem("Cancel editing");
        mcancel.setMnemonic(KeyEvent.VK_L);
        mcancel.addActionListener(this);

        editmenu.add(maddtext);
        editmenu.add(mbright);
        editmenu.add(mcompress);
        editmenu.add(mresize);
        editmenu.add(mrotate);
        editmenu.add(mtransparent);
        editmenu.add(mcancel);

        mainmenu.add(menu);
        mainmenu.add(editmenu);
        setJMenuBar(mainmenu);

        setTitle("Image Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
        setVisible(true);

        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif","bmp","png");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        enableSaving(false);
        ia.requestFocus();
    }

    ////start the ImageBrightness class
    //The ImageBrightness class represents the interface to allow the user to make the image
    //brighter or darker by changing the value of the image slider
    //The ImageBrightness class is in the Main class
    public class ImageBrightness extends JFrame implements ChangeListener{
        JSlider slider;

        ImageBrightness(){
            addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    dispose();

                }
            });
            Container cont=getContentPane();
            slider=new JSlider(-10,10,0);
            slider.setEnabled(false);
            slider.addChangeListener(this);
            cont.add(slider,BorderLayout.CENTER);
            slider.setEnabled(true);
            setTitle("Image brightness");
            setPreferredSize(new Dimension(300,100));
            setVisible(true);
            pack();
            enableSlider(false);
        }
        public void enableSlider(boolean enabled){
            slider.setEnabled(enabled);
        }
        public void stateChanged(ChangeEvent e){
            ia.setValue(slider.getValue()/10.0f);
            ia.setActionSlided(true);
            ia.filterImage();
            ia.repaint();
            enableSaving(true);

        }

    } ////end of the ImageBrightness class

    ////start the ImageResize class
    //The ImageResize class represents the interface that allows you to resize the image
    //by making changes to its width and height
    //The ImageResize class is in the Main class
    public class ImageResize extends JFrame implements ActionListener {
        JPanel panel;
        JTextField txtWidth;
        JTextField txtHeight;
        JButton btOK;
        ImageResize(){
            setTitle("Image resize");
            //setDefaultCloseOperation(EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(400,100));

            btOK=new JButton("OK");
            btOK.setBackground(Color.BLACK);
            btOK.setForeground(Color.BLUE);
            btOK.addActionListener(this);

            txtWidth=new JTextField(4);
            txtWidth.addKeyListener(new KeyList());
            txtHeight=new JTextField(4);
            txtHeight.addKeyListener(new KeyList());
            panel=new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(new JLabel("Width:"));
            panel.add(txtWidth);
            panel.add(new JLabel("Height:"));

            panel.add(txtHeight);
            panel.add(btOK);
            panel.setBackground(Color.GRAY);
            add(panel, BorderLayout.CENTER);
            setVisible(true);
            pack();
            enableComponents(false);
        }
        //This method can be invoked to  enable the text boxes of image width and height
        public void enableComponents(boolean enabled){
            txtWidth.setEnabled(enabled);
            txtHeight.setEnabled(enabled);
            btOK.setEnabled(enabled);
        }
        //This method works when you click the OK button to resize the image
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==btOK){
                ia.setActionResized(true);
                ia.resizeImage(Integer.parseInt(txtWidth.getText()),Integer.parseInt(txtHeight.getText()));
                enableSaving(true);
                ia.repaint();
            }
        }
        //Restrict the key presses
        //Only number, backspace, and delete keys are allowed
        public class KeyList extends KeyAdapter{
            public void keyTyped(KeyEvent ke){

                char c = ke.getKeyChar();
                int intkey=(int)c;
                if(!(intkey>=48 && intkey<=57 || intkey==8 || intkey==127))
                {
                    ke.consume(); //hide the unwanted key

                }

            }

        }
    }////end of the ImageResize class

    ////start the TextAdd class
    //The TextAdd class represents the interface that allows you to add your text to the image
    //In this interface you can input your text, select color, font name, and font size of the text
    //The TextAdd class is in the Main class
    public class TextAdd extends JFrame implements ActionListener {
        JPanel panel;
        JTextArea txtText;
        JComboBox cbFontNames;
        JComboBox cbFontSizes;
        JButton btOK;
        JButton btSetColor;
        String seFontName;
        Color colorText;
        int seFontSize;
        TextAdd(){
            colorText=null;
            setTitle("Add text to the image");
            //setDefaultCloseOperation(EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(400,150));

            btOK=new JButton("OK");
            btOK.setBackground(Color.BLACK);
            btOK.setForeground(Color.BLUE);
            btOK.addActionListener(this);

            btSetColor=new JButton("Set text color");
            btSetColor.setBackground(Color.BLACK);
            btSetColor.setForeground(Color.WHITE);
            btSetColor.addActionListener(this);

            txtText=new JTextArea(1,30);
            cbFontNames=new JComboBox();
            cbFontSizes=new JComboBox();
            panel=new JPanel();
            panel.setLayout(new GridLayout(4,1));
            panel.add(new JLabel("Text:"));
            panel.add(txtText);
            panel.add(new JLabel("Font Name:"));
            panel.add(cbFontNames);
            panel.add(new JLabel("Font Size:"));
            panel.add(cbFontSizes);
            panel.add(btSetColor);
            panel.add(btOK);
            panel.setBackground(Color.GRAY);
            add(panel, BorderLayout.CENTER);
            setVisible(true);
            pack();
            listFonts();
        }


        public void actionPerformed(ActionEvent e){
            if(e.getSource()==btOK){ //the button OK is clicked so the text is ready to place on the image
                ia.setActionDraw(true);
                String textDraw=txtText.getText();
                String fontName=cbFontNames.getSelectedItem().toString();
                int fontSize=Integer.parseInt(cbFontSizes.getSelectedItem().toString());
                ia.setText(textDraw,fontName,fontSize,colorText);
                dispose();
            }
            else if(e.getSource()==btSetColor){ //show color chooser dialog for color selection
                JColorChooser jser=new JColorChooser();
                colorText=jser.showDialog(this,"Color Chooser",Color.BLACK);

            }
        }

        //The listFonts method get all available fonts from the system
        public void listFonts(){
            //get the available font names and add them to the font names combobox
            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts=ge.getAvailableFontFamilyNames();
            for(String f:fonts)
                cbFontNames.addItem(f);
            //Initialize font sizes
            for(int i=8;i<50;i++)
                cbFontSizes.addItem(i);

        }
    } ////end of the TextAdd class

    //handling events of sub-menu items on the main program interface
    public void actionPerformed(ActionEvent e){

        JMenuItem source = (JMenuItem)(e.getSource());
        if(source.getText().compareTo("Open...")==0)
        {
            setImage();
            ia.repaint();
            validate();

        }
        else if(source.getText().compareTo("Save as...")==0)
        {
            showSaveFileDialog();

        }
        else if(source.getText().compareTo("Save")==0)
        {

            ia.saveToFile(filename);
        }
        else if(source.getText().compareTo("Add text on image")==0)
        {
            new TextAdd();
        }

        else if(source.getText().compareTo("Image brightness")==0)
        {

            ImageBrightness ib=new ImageBrightness();
            if(ImgArea.imageLoaded)
                ib.enableSlider(true);
        }
        else if(source.getText().compareTo("Image compression")==0)
        {
            if(ImgArea.imageLoaded){
                ia.setActionCompressed(true);
                enableSaving(true);
            }
        }

        else if(source.getText().compareTo("Image resize")==0)
        {

            ImageResize ir=new ImageResize();
            if(ImgArea.imageLoaded)
                ir.enableComponents(true);
        }
        else if(source.getText().compareTo("Image rotation")==0)
        {

            if(ImgArea.imageLoaded){
                ia.rotateImage();
                enableSaving(true);
            }
        }

        else if(source.getText().compareTo("Image transparency")==0){
            if(ImgArea.c==null){
                JOptionPane dialog=new JOptionPane();
                dialog.showMessageDialog(this,"Click the background area of the image first","Error",JOptionPane.ERROR_MESSAGE);
            }
            else if(ImgArea.imageLoaded){
                ia.makeTransparency(ImgArea.c);
                enableSaving(true);
            }
        }

        else if(source.getText().compareTo("Cancel editing")==0) {
            ia.setImgFileName(filename);
            ia.reset();
        }

        else if(source.getText().compareTo("Exit")==0)
            System.exit(0);


    }

    //The setImage method has code to open the file dialog so the user can choose
    //the file to show on the program interface
    public void setImage(){

        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            filename=chooser.getSelectedFile().toString();
            ia.prepareImage(filename);
        }

    }

    //The showSaveFileDialog method has code to display the save file dialog
    //It is invoked when the user select Save as... sub-menu item
    public void showSaveFileDialog(){
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String filen=chooser.getSelectedFile().toString();
            ia.saveToFile(filen);

        }
    }


    //The enableSaving method defines code to enable or  disable saving sub-menu items
    public void enableSaving(boolean f){
        msaveas.setEnabled(f);
        msave.setEnabled(f);

    }

}