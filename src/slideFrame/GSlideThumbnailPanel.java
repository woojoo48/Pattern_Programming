package slideFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import global.GConstants;


public class GSlideThumbnailPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    //components
    private JPanel thumbnailContainer;
    private JScrollPane scrollPane;
    private JPanel controlPanel;
    private Vector<ThumbnailItem> thumbnailItems;
    
    //attributes
    private int selectedIndex = -1;
    
    //association
    private GSlideManager slideManager;
    
    public GSlideThumbnailPanel() {
        //attributes
        this.selectedIndex = -1;
        
        //components
        this.thumbnailItems = new Vector<ThumbnailItem>();
        

        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 240, 240));
        this.setPreferredSize(new Dimension(GConstants.GSlidePanel.PANEL_WIDTH, 0));
        
        JLabel titleLabel = new JLabel("슬라이드");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        this.thumbnailContainer = new JPanel();
        this.thumbnailContainer.setLayout(new BoxLayout(thumbnailContainer, BoxLayout.Y_AXIS));
        this.thumbnailContainer.setBackground(new Color(240, 240, 240));
        
        this.scrollPane = new JScrollPane(thumbnailContainer);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, BorderLayout.CENTER);
        

        this.controlPanel = createControlPanel();
        this.add(controlPanel, BorderLayout.SOUTH);
    }
    
    public void initialize() {
        this.refreshThumbnails();
    }
    
    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(240, 240, 240));
        
        JPanel buttonRow1 = new JPanel(new FlowLayout());
        buttonRow1.setBackground(new Color(240, 240, 240));
        
        JButton addBtn = new JButton("+");
        addBtn.setPreferredSize(new Dimension(40, 30));
        addBtn.setActionCommand("ADD_SLIDE");
        addBtn.addActionListener(new SlideActionHandler());
        
        JButton deleteBtn = new JButton("-");
        deleteBtn.setPreferredSize(new Dimension(40, 30));
        deleteBtn.setActionCommand("REMOVE_SLIDE");
        deleteBtn.addActionListener(new SlideActionHandler());
        
        buttonRow1.add(addBtn);
        buttonRow1.add(deleteBtn);
        
        JPanel buttonRow2 = new JPanel(new FlowLayout());
        buttonRow2.setBackground(new Color(240, 240, 240));
        
        JButton prevBtn = new JButton("이전");
        prevBtn.setPreferredSize(new Dimension(60, 30));
        prevBtn.setActionCommand("PREV_SLIDE");
        prevBtn.addActionListener(new SlideActionHandler());
        
        JButton nextBtn = new JButton("다음");
        nextBtn.setPreferredSize(new Dimension(60, 30));
        nextBtn.setActionCommand("NEXT_SLIDE");
        nextBtn.addActionListener(new SlideActionHandler());
        
        buttonRow2.add(prevBtn);
        buttonRow2.add(nextBtn);
        
        controlPanel.add(buttonRow1);
        controlPanel.add(buttonRow2);
        
        return controlPanel;
    }

    public void refreshThumbnails() {
        if (slideManager == null) return;
        
        thumbnailContainer.removeAll();
        thumbnailItems.clear();
        
        for (int i = 0; i < slideManager.getSlideCount(); i++) {
            GSlide slide = slideManager.getSlide(i);
            ThumbnailItem item = new ThumbnailItem(slide, i);
            item.associate(this);
            thumbnailItems.add(item);
            thumbnailContainer.add(item);
        }
        
        this.selectedIndex = slideManager.getCurrentSlideIndex();
        updateSelection();
        
        thumbnailContainer.revalidate();
        thumbnailContainer.repaint();
    }
    

    public void onThumbnailClicked(int slideIndex) {
        if (slideManager != null) {
            slideManager.switchToSlide(slideIndex);
            this.selectedIndex = slideIndex;
            updateSelection();
        }
    }
    

    private void updateSelection() {
        for (int i = 0; i < thumbnailItems.size(); i++) {
            ThumbnailItem item = thumbnailItems.get(i);
            item.setSelected(i == selectedIndex);
        }
    }
    

    private class SlideActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (slideManager == null) return;
            
            String command = e.getActionCommand();
            
            switch(command) {
                case "ADD_SLIDE":
                    slideManager.addSlide();
                    refreshThumbnails();
                    break;
                    
                case "REMOVE_SLIDE":
                    if (slideManager.getSlideCount() > 1) {
                        slideManager.removeCurrentSlide();
                        refreshThumbnails();
                    }
                    break;
                    
                case "PREV_SLIDE":
                    if (slideManager.hasPreviousSlide()) {
                        slideManager.previousSlide();
                        selectedIndex = slideManager.getCurrentSlideIndex();
                        updateSelection();
                    }
                    break;
                    
                case "NEXT_SLIDE":
                    if (slideManager.hasNextSlide()) {
                        slideManager.nextSlide();
                        selectedIndex = slideManager.getCurrentSlideIndex();
                        updateSelection();
                    }
                    break;
            }
        }
    }
}