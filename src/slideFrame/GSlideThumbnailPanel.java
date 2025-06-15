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
    
    public enum ESlideButton {
        eAddSlide,
        eRemoveSlide,
        ePrevSlide,
        eNextSlide;
        
        public String getText() { 
            return GConstants.getSlideButtonText(this.name()); 
        }
        
        public Dimension getSize() { 
            return GConstants.getSlideButtonSize(this.name()); 
        }
        
        public String getActionCommand() { 
            return GConstants.getSlideButtonCommand(this.name()); 
        }
    }
    
    private JPanel thumbnailContainer;
    private JScrollPane scrollPane;
    private JPanel controlPanel;
    private Vector<ThumbnailItem> thumbnailItems;
    
    private int selectedIndex = -1;
    
    private GSlideManager slideManager;
    
    public GSlideThumbnailPanel() {
        this.selectedIndex = -1;
        
        this.thumbnailItems = new Vector<ThumbnailItem>();
        
        this.setLayout(new BorderLayout());
        this.setBackground(GConstants.getColor("thumbnailPanelBg"));
        this.setPreferredSize(new Dimension(GConstants.getPanelWidth(), 0));
        
        JLabel titleLabel = new JLabel(GConstants.getSlideTitleLabel());
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        this.thumbnailContainer = new JPanel();
        this.thumbnailContainer.setLayout(new BoxLayout(thumbnailContainer, BoxLayout.Y_AXIS));
        this.thumbnailContainer.setBackground(GConstants.getColor("thumbnailPanelBg"));
        
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
        controlPanel.setBackground(GConstants.getColor("thumbnailPanelBg"));
        
        JPanel buttonRow1 = createButtonRow(ESlideButton.eAddSlide, ESlideButton.eRemoveSlide);
        JPanel buttonRow2 = createButtonRow(ESlideButton.ePrevSlide, ESlideButton.eNextSlide);
        
        controlPanel.add(buttonRow1);
        controlPanel.add(buttonRow2);
        
        return controlPanel;
    }
    
    private JPanel createButtonRow(ESlideButton... buttons) {
        JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.setBackground(GConstants.getColor("thumbnailPanelBg"));
        
        for (ESlideButton buttonType : buttons) {
            JButton button = createSlideButton(buttonType);
            buttonRow.add(button);
        }
        
        return buttonRow;
    }
    
    private JButton createSlideButton(ESlideButton buttonType) {
        JButton button = new JButton(buttonType.getText());
        button.setPreferredSize(buttonType.getSize());
        button.setActionCommand(buttonType.getActionCommand());
        button.addActionListener(new SlideActionHandler());
        return button;
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
            String addCommand = GConstants.getSlideButtonCommand("eAddSlide");
            String removeCommand = GConstants.getSlideButtonCommand("eRemoveSlide");
            String prevCommand = GConstants.getSlideButtonCommand("ePrevSlide");
            String nextCommand = GConstants.getSlideButtonCommand("eNextSlide");
            
            if (command.equals(addCommand)) {
                slideManager.addSlide();
                refreshThumbnails();
            } else if (command.equals(removeCommand)) {
                if (slideManager.getSlideCount() > 1) {
                    slideManager.removeCurrentSlide();
                    refreshThumbnails();
                }
            } else if (command.equals(prevCommand)) {
                if (slideManager.hasPreviousSlide()) {
                    slideManager.previousSlide();
                    selectedIndex = slideManager.getCurrentSlideIndex();
                    updateSelection();
                }
            } else if (command.equals(nextCommand)) {
                if (slideManager.hasNextSlide()) {
                    slideManager.nextSlide();
                    selectedIndex = slideManager.getCurrentSlideIndex();
                    updateSelection();
                }
            }
        }
    }
}