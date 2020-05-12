package YNWA;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class Main {
    JFrame theFrame;
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    float tempoFactor = 1;
    JLabel Speed;
    JProgressBar speedBar= new JProgressBar();


    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat",
            "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
            "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibroslap", "Low-mod Tom",
            "High Agogo", "Open Hi Conga"};

    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new Main().startUp();
    }

    public void startUp() {
        setUpMidi();
        buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Azamat Ibraimov's BeatBox BETA");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buildTrackAndStart();
                }
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    sequencer.stop();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(1);
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(1);
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(1);
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);





        URL iconURL = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        theFrame.setIconImage(icon.getImage());

        JMenuBar menuBar=new JMenuBar();

        JMenu menu=new JMenu("Menu");
        menuBar.add(menu);

        JMenuItem opengit=new JMenuItem("My GitHub");
        opengit.addActionListener(e -> {
            try {Desktop.getDesktop().browse(new URI("https://github.com/IbraimovAzamat"));
            } catch (Exception x)
            {JOptionPane.showMessageDialog(null,e);}
        });
        menu.add(opengit);

        JMenuItem about=new JMenuItem("About");
        about.addActionListener(e -> {
                JOptionPane.showMessageDialog(null,
                        "A simple Beatbox program!\n Create your own beats!\n #Java CS-102 project. Enjoy!\n Made by: Azamat Ibraimov.", "About",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        menu.add(about);


        JMenuItem exit=new JMenuItem("Exit");
        exit.addActionListener(e -> {
            System.exit(1);
            } );
        menu.add(exit);






        JMenu actionsmenu=new JMenu("Actions");
        menuBar.add(actionsmenu);
        JMenuItem menustart=new JMenuItem("Start");
        menustart.addActionListener(new MyStartListener());
        actionsmenu.add(menustart);

        JMenuItem menustop=new JMenuItem("Stop");
        menustop.addActionListener(new MyStopListener());
        actionsmenu.add(menustop);
        actionsmenu.addSeparator();

        JMenuItem menutempup=new JMenuItem("Up");
        menutempup.addActionListener(new MyUpTempoListener());
        actionsmenu.add(menutempup);

        JMenuItem menutempdown=new JMenuItem("Down");
        menutempdown.addActionListener(new MyUpTempoListener());
        actionsmenu.add(menutempdown);

        theFrame.setJMenuBar(menuBar);



        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start = new JButton("Start                ");
        start.addActionListener(new MyStartListener());
        start.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        buttonBox.add(start);



        JButton stop = new JButton("Stop                 ");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("TempoUp       ");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("TempoDown  ");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton copy = new JButton("Copy               ");
        copy.addActionListener(new MyCopyListener());
        buttonBox.add(copy);

        JButton ClearAll = new JButton("Clear               ");
        ClearAll.addActionListener(new MyStopListener());
        ClearAll.addActionListener(e -> {
            for (JCheckBox chb : checkboxList) {
                chb.setSelected(false);
            } });
        buttonBox.add(ClearAll);

        Speed = new JLabel("     Speed: 50%");
        Speed.setVisible(true);
        buttonBox.add(Speed);
        speedBar.setMaximumSize(new Dimension(200,10));
        speedBar.setMinimum(0);
        speedBar.setMaximum(100);
        speedBar.setValue(50);
        buttonBox.add(speedBar);

        String instruct = "Zdes\nmogla\nbit\nvasha\nreklama!";
        JTextArea Instructions = new JTextArea(instruct);
        Instructions.setSize(10,10);
        buttonBox.add(Instructions);



        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        ArrayList<Integer> trackList;
        sequence.deleteTrack(track);
        track = sequence.createTrack();


        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<>();

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(key);
                } else {
                    trackList.add(null);
                }
            }
            makeTracks(trackList);
        }

        track.add(Midi.makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }
    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }
    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            tempoFactor = sequencer.getTempoFactor();
            double speed = tempoFactor * 1.03;
            Speed.setText(Format.formatSpeedString(speed));
            sequencer.setTempoFactor((float) (speed));
            speedBar.setValue(Format.formatSpeedInt(speed));
        }
    }
    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            tempoFactor = sequencer.getTempoFactor();
            double speed = tempoFactor * .97;
            Speed.setText(Format.formatSpeedString(speed));
            sequencer.setTempoFactor((float) (speed));
            speedBar.setValue(Format.formatSpeedInt(speed));
        }
    }

    public class MyCopyListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            for (int i = 0; i < checkboxList.size(); i += 16) {
                for (int j = i; j < i + 4; j++) {
                    if (checkboxList.get(j).isSelected()) {
                        (checkboxList.get(j + 4)).setSelected(true);
                        (checkboxList.get(j + 8)).setSelected(true);
                        (checkboxList.get(j + 12)).setSelected(true);
                    }
                }
            }
        }
    }


    public void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num;
                track.add(Midi.makeEvent(144, 9, numKey, 100, i));
                track.add(Midi.makeEvent(128, 9, numKey, 100, i + 1));
            }
        }
    }
}
