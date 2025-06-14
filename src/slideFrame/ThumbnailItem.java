package slideFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import global.GConstants;

public class ThumbnailItem extends JPanel {
    private static final long serialVersionUID = 1L;
    
    //components
    private JLabel numberLabel;
    
    //attributes
    private GSlide slide;
    private int slideIndex;
    private boolean isSelected = false;
    
    //association
    private GSlideThumbnailPanel thumbnailPanel;
    
    public ThumbnailItem(GSlide slide, int slideIndex) {
        //attributes
        this.slide = slide;
        this.slideIndex = slideIndex;
        this.isSelected = false;
        
        //components
        this.setPreferredSize(new Dimension(
            GConstants.GSlidePanel.THUMBNAIL_WIDTH, 
            GConstants.GSlidePanel.THUMBNAIL_HEIGHT + 30
        ));
        this.setMaximumSize(new Dimension(
            GConstants.GSlidePanel.THUMBNAIL_WIDTH, 
            GConstants.GSlidePanel.THUMBNAIL_HEIGHT + 30
        ));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 슬라이드 번호 라벨
        this.numberLabel = new JLabel(String.valueOf(slideIndex + 1));
        numberLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(numberLabel, BorderLayout.SOUTH);
        
        // 썸네일 클릭 이벤트
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thumbnailPanel != null) {
                    thumbnailPanel.onThumbnailClicked(ThumbnailItem.this.slideIndex);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    setBackground(new Color(230, 230, 230));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
            }
        });
    }
    
    public void associate(GSlideThumbnailPanel thumbnailPanel) {
        this.thumbnailPanel = thumbnailPanel;
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            this.setBackground(new Color(200, 220, 255));
            this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        } else {
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        try {
            int thumbX = 10;
            int thumbY = 10;
            int thumbWidth = GConstants.GSlidePanel.THUMBNAIL_WIDTH - 20;
            int thumbHeight = GConstants.GSlidePanel.THUMBNAIL_HEIGHT - 20;
            
            g2d.setColor(Color.WHITE);
            g2d.fillRect(thumbX, thumbY, thumbWidth, thumbHeight);
            
            g2d.setColor(Color.GRAY);
            g2d.drawRect(thumbX, thumbY, thumbWidth, thumbHeight);
            
            if (slide != null && slide.getDrawingPanel() != null) {
                g2d.setColor(Color.BLACK);
                int shapeCount = slide.getShapeCount();
                String text = shapeCount + "개 도형";
                g2d.drawString(text, thumbX + 10, thumbY + 20);
                
                if (shapeCount > 0) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(thumbX + 20, thumbY + 30, 20, 20);
                    
                    if (shapeCount > 1) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect(thumbX + 50, thumbY + 40, 15, 15);
                    }
                }
            }
            
        } finally {
            g2d.dispose();
        }
    }
}