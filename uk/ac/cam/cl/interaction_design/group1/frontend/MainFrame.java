package uk.ac.cam. cl.interaction_design.group1.frontend;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    JButton addBtn;
    JFrame search=new Search(this);

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==addBtn)
        {


            search.setVisible(true);

            this.setVisible(false);


        }
    }
    public MainFrame() {


        super("saved locations");


        setSize(350, 600);
        setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel background=new Background();
        setContentPane(background);
        background.setLayout(new BorderLayout());

        add(makeTop(), BorderLayout.NORTH);
        add(makeCenter(), BorderLayout.CENTER);
        add(makeBack(), BorderLayout.SOUTH);
        setVisible(true);
    }

    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createLineBorder(Color.white);
        Border tb = BorderFactory.createTitledBorder(etch, title);
        component.setBorder(tb);
    }

    public JPanel makeTop() {
        JPanel top = new JPanel();


        JLabel text = new JLabel("Choose Location", SwingConstants.CENTER);
        text.setOpaque(false);
        text.setPreferredSize(new Dimension(350, 40));
        text.setFont(new Font("Courier New", Font.PLAIN, 25));
        text.setForeground(Color.white);
        top.add(text);
        top.setOpaque(false);
        //addBorder(top, "fg");
        return top;

    }

    public JPanel makeBack() {
        JPanel bottom = new JPanel();
        JButton back = new JButton("Back");
        back.setPreferredSize(new Dimension(this.getWidth() / 3, 40));
        bottom.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.LINE_START;
        bottom.add(back, c);
        bottom.setOpaque(false);
        //addBorder(bottom, "fg");

        return bottom;
    }

    public JPanel makeCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout());

        JPanel southB=new JPanel();
        southB.setOpaque(false);
        addBtn = new CircleButton("+");
        addBtn.addActionListener(this::actionPerformed);
        southB.add(addBtn);
        center.add(southB,BorderLayout.SOUTH);

        JPanel padding=new JPanel();
        //padding.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()/8));
        padding.setOpaque(false);
        center.add(padding,BorderLayout.NORTH);

        JPanel locations=new JPanel();
        locations.setLayout(new GridLayout(5,2,25,10));
        locations.setOpaque(false);
        JButton cal=new JButton("Cambridge");
        locations.add(cal);
        center.add(locations,BorderLayout.CENTER);
        return center;










    }
}
