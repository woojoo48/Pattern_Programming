package slideFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import global.GConstants;

public class ThumbnailItem extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JLabel slideNumberLabel;
    private GSlide slide;
    private int slideIndex;
    private boolean isSelected = false;
    private GSlideThumbnailPanel thumbnailPanel;
    
    public ThumbnailItem(GSlide slide, int slideIndex) {
        this.slide = slide;
        this.slideIndex = slideIndex;
        this.isSelected = false;
        
        this.setPreferredSize(new Dimension(
            GConstants.GSlidePanel.THUMBNAIL_WIDTH, 
            GConstants.GSlidePanel.THUMBNAIL_HEIGHT + 20
        ));
        this.setMaximumSize(new Dimension(
            GConstants.GSlidePanel.THUMBNAIL_WIDTH, 
            GConstants.GSlidePanel.THUMBNAIL_HEIGHT + 20
        ));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        this.slideNumberLabel = new JLabel(String.valueOf(slideIndex + 1));
        slideNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slideNumberLabel.setVerticalAlignment(SwingConstants.CENTER);
        slideNumberLabel.setFont(new Font("Arial", Font.BOLD, 32));
        slideNumberLabel.setForeground(Color.DARK_GRAY);
        this.add(slideNumberLabel, BorderLayout.CENTER);
        
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
        
        int thumbX = 10;
        int thumbY = 10;
        int thumbWidth = GConstants.GSlidePanel.THUMBNAIL_WIDTH - 20;
        int thumbHeight = GConstants.GSlidePanel.THUMBNAIL_HEIGHT - 30;
        
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(thumbX, thumbY, thumbWidth, thumbHeight);
    }
}