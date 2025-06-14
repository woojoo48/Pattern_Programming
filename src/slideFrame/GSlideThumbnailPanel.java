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
    
    // ✨ 새로 추가: 슬라이드 버튼 enum
    public enum ESlideButton {
        eAddSlide("+", new Dimension(40, 30), "ADD_SLIDE"),
        eRemoveSlide("-", new Dimension(40, 30), "REMOVE_SLIDE"),
        ePrevSlide("이전", new Dimension(60, 30), "PREV_SLIDE"),
        eNextSlide("다음", new Dimension(60, 30), "NEXT_SLIDE");
        
        private String text;
        private Dimension size;
        private String actionCommand;
        
        private ESlideButton(String text, Dimension size, String actionCommand) {
            this.text = text;
            this.size = size;
            this.actionCommand = actionCommand;
        }
        
        public String getText() { return this.text; }
        public Dimension getSize() { return this.size; }
        public String getActionCommand() { return this.actionCommand; }
    }
    
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
    
    // ✨ 개선된 createControlPanel
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(240, 240, 240));
        
        // 첫 번째 줄: + - 버튼들
        JPanel buttonRow1 = createButtonRow(ESlideButton.eAddSlide, ESlideButton.eRemoveSlide);
        
        // 두 번째 줄: 이전/다음 버튼들  
        JPanel buttonRow2 = createButtonRow(ESlideButton.ePrevSlide, ESlideButton.eNextSlide);
        
        controlPanel.add(buttonRow1);
        controlPanel.add(buttonRow2);
        
        return controlPanel;
    }
    
    // ✨ 새로운 헬퍼 메서드: 버튼 행 생성
    private JPanel createButtonRow(ESlideButton... buttons) {
        JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.setBackground(new Color(240, 240, 240));
        
        // for문으로 간단하게!
        for (ESlideButton buttonType : buttons) {
            JButton button = createSlideButton(buttonType);
            buttonRow.add(button);
        }
        
        return buttonRow;
    }
    
    // ✨ 새로운 헬퍼 메서드: 개별 버튼 생성
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