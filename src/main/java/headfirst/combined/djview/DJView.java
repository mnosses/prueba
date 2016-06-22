package headfirst.combined.djview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DJView implements ActionListener,  BeatObserver, BPMObserver, JUG1Observer,JUG2Observer, Runnable {
    BeatModelInterface model;
    ControllerInterface controller;
    JFrame viewFrame;
    JPanel viewPanel;
    BeatBar beatBar;
    JLabel bpmOutputLabel;
    JLabel anotadorOutputLabel;
    JLabel anotadorLabel;
    JFrame controlFrame;
    JPanel controlPanel;
    JLabel bpmLabel;
    JTextField bpmTextField;
    JButton setBPMButton;
    JButton increaseBPMButton;
    JButton decreaseBPMButton;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem startMenuItem;
    JMenuItem stopMenuItem;
    String[] option ={"AnotadorModel","BPMModel","HeartModel"};
    JComboBox<String> box= new JComboBox<String>(option);

    public DJView(ControllerInterface controller, BeatModelInterface model) {
        this.controller = controller;
        this.model = model;
        model.registerObserver((BeatObserver) this);
        model.registerObserver((BPMObserver) this);
        model.registerObserver((JUG1Observer) this);
        model.registerObserver((JUG2Observer) this);
    }

    public void createView() {
        // Create all Swing components here
        viewPanel = new JPanel(new GridLayout(1, 2));
        viewFrame = new JFrame("View");
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setSize(new Dimension(100, 80));
        bpmOutputLabel = new JLabel("offline", SwingConstants.CENTER);
        anotadorLabel = new JLabel("Jugador1  -  Jugador2", SwingConstants.CENTER);
        anotadorOutputLabel = new JLabel("0" + "  -  " + "0", SwingConstants.CENTER);
        beatBar = new BeatBar();
        beatBar.setValue(0);
        JPanel bpmPanel = new JPanel(new GridLayout(4, 2));
        bpmPanel.add(beatBar);
        bpmPanel.add(bpmOutputLabel);
        bpmPanel.add(anotadorLabel);
        bpmPanel.add(anotadorOutputLabel);
        viewPanel.add(bpmPanel);
        viewFrame.getContentPane().add(viewPanel, BorderLayout.CENTER);
        viewFrame.pack();
        viewFrame.setVisible(true);
    }


    public void createControls() {
        // Create all Swing components here
        JFrame.setDefaultLookAndFeelDecorated(true);
        controlFrame = new JFrame("Control");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setSize(new Dimension(100, 80));

        controlPanel = new JPanel(new GridLayout(1, 2));

        menuBar = new JMenuBar();
        menu = new JMenu("DJ Control");
        startMenuItem = new JMenuItem("Start");
        menu.add(startMenuItem);
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                controller.start();
            }
        });
        stopMenuItem = new JMenuItem("Stop");
        menu.add(stopMenuItem);
        stopMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                controller.stop();
            }
        });
        JMenuItem exit = new JMenuItem("Quit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        menu.add(exit);
        menuBar.add(menu);
        controlFrame.setJMenuBar(menuBar);
        bpmTextField = new JTextField(2);
        bpmLabel = new JLabel("Enter BPM:", SwingConstants.RIGHT);
        setBPMButton = new JButton("Set");
        setBPMButton.setSize(new Dimension(10, 40));
        increaseBPMButton = new JButton(">>");
        decreaseBPMButton = new JButton("<<");
        setBPMButton.addActionListener(this);
        increaseBPMButton.addActionListener(this);
        decreaseBPMButton.addActionListener(this);
        box.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        buttonPanel.add(decreaseBPMButton);
        buttonPanel.add(increaseBPMButton);

        JPanel enterPanel = new JPanel(new GridLayout(1, 2));
        enterPanel.add(bpmLabel);
        enterPanel.add(bpmTextField);
        JPanel insideControlPanel = new JPanel(new GridLayout(4, 1));

        insideControlPanel.add(enterPanel);
        insideControlPanel.add(setBPMButton);
        insideControlPanel.add(buttonPanel);
        insideControlPanel.add(box);
        controlPanel.add(insideControlPanel);

        bpmLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bpmOutputLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        controlFrame.getRootPane().setDefaultButton(setBPMButton);
        controlFrame.getContentPane().add(controlPanel, BorderLayout.CENTER);

        controlFrame.pack();
        controlFrame.setVisible(true);
    }

    public void enableStopMenuItem() {
        stopMenuItem.setEnabled(true);
    }

    public void disableStopMenuItem() {
        stopMenuItem.setEnabled(false);
    }

    public void enableStartMenuItem() {
        startMenuItem.setEnabled(true);
    }

    public void disableStartMenuItem() {
        startMenuItem.setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == setBPMButton) {
            int bpm = Integer.parseInt(bpmTextField.getText());
            controller.setBPM(bpm);
        } else if (event.getSource() == increaseBPMButton) {
            controller.increaseBPM();
        } else if (event.getSource() == decreaseBPMButton) {
            controller.decreaseBPM();
        } else if(event.getSource()==box){
            String selected= (String)box.getSelectedItem();
            switch (selected){
                case "BPMModel":{
                                    controller.stop();desregistrar();
                                    model= new BeatModel();
                                    controller=new BeatController(model,this);
                                    controller.start();
                                    registrar();
                                    break;}
                case "HeartModel":{
                                    controller.stop();desregistrar();
                                    HeartModelInterface nuevo= HeartModel.getInstace();
                                    model=new HeartAdapter(nuevo);
                                    model.on();
                                    controller=new HeartController(nuevo,this);
                                    controller.start();
                                    registrar();
                                     break;}
                case "AnotadorModel":{  controller.stop();desregistrar();
                                        MyAnotadorInterface nuevo=new MyAnotadorModel();
                                        model= new MyAnotadorAdapter(nuevo);
                                        controller=new MyAnotadorController(nuevo,this);
                                        controller.start();
                                        registrar();
                                         break;}
                default:{}
            }
    }}

    public void updateBPM() {
        if (model != null) {
            int bpm = model.getBPM();
            if (bpm == 0) {
                if (bpmOutputLabel != null) {
                    bpmOutputLabel.setText("offline");
                }
            } else {
                if (bpmOutputLabel != null) {
                    switch (model.getName()){
                        case "Anotador":{bpmOutputLabel.setText("Segundos: " + model.getBPM());break;}
                        case "Heart":{bpmOutputLabel.setText("Intentos: " + model.getBPM());break;}
                        case "Beat":{bpmOutputLabel.setText("BPM: " + model.getBPM());break;}

                    }
                }
            }
        }
    }

    public void updateJUG1() {
        if (model != null) {
            int uno = model.getJUG1();
            int dos = model.getJUG2();
            anotadorOutputLabel.setText(uno + " - " + dos);
        }
    }


    public void updateJUG2() {
        if (model != null) {
            int uno = model.getJUG1();
            int dos = model.getJUG2();
            anotadorOutputLabel.setText(uno + " - " + dos);
        }
    }


    public void updateBeat() {
        if (model.getName().equals("Anotador")) {
            beatBar.setMaximum(100);
            beatBar.setMinimum(0);
            beatBar.setControl(model.getName());
            new Thread(this).start();
        }
        else {
            if (beatBar != null) {
                beatBar.setControl(model.getName());
               beatBar.setValue(100);
            }
        }
    }

    public void run() {

        for (int i = 100; i > 0; i--) {
            if(model.getName().equals("Anotador")){
            beatBar.setValue(i);
            beatBar.repaint();
            try {
                Thread.sleep(10*model.getBPM());

            } catch (Exception e) { }  ;
        }}
    }
    public void desregistrar(){
        model.removeObserver((BeatObserver) this);
        model.removeObserver((BPMObserver) this);
        model.removeObserver((JUG1Observer) this);
        model.removeObserver((JUG2Observer) this);
    }

    public void registrar(){
        model.registerObserver((BeatObserver) this);
        model.registerObserver((BPMObserver) this);
        model.registerObserver((JUG1Observer) this);
        model.registerObserver((JUG2Observer) this);
        updateBeat();
        updateBPM();
        updateJUG1();
        updateJUG2();
    }
}