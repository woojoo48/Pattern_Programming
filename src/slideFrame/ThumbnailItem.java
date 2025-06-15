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
            GConstants.getThumbnailWidth(), 
            GConstants.getThumbnailHeight() + 20
        ));
        this.setMaximumSize(new Dimension(
            GConstants.getThumbnailWidth(), 
            GConstants.getThumbnailHeight() + 20
        ));
        this.setLayout(new BorderLayout());
        this.setBackground(GConstants.getColor("backgroundColor"));
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        this.slideNumberLabel = new JLabel(String.valueOf(slideIndex + 1));
        slideNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slideNumberLabel.setVerticalAlignment(SwingConstants.CENTER);
        slideNumberLabel.setFont(GConstants.getFont("slideNumberFont"));
        slideNumberLabel.setForeground(GConstants.getColor("slideNumberColor"));
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
                    setBackground(GConstants.getColor("thumbnailHoverBg"));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    setBackground(GConstants.getColor("backgroundColor"));
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
            this.setBackground(GConstants.getColor("selectedThumbnailBg"));
            this.setBorder(BorderFactory.createLineBorder(GConstants.getColor("thumbnailBorderSelected"), 2));
        } else {
            this.setBackground(GConstants.getColor("backgroundColor"));
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int thumbX = 10;
        int thumbY = 10;
        int thumbWidth = GConstants.getThumbnailWidth() - 20;
        int thumbHeight = GConstants.getThumbnailHeight() - 30;
        
        g.setColor(GConstants.getColor("thumbnailBorder"));
        g.drawRect(thumbX, thumbY, thumbWidth, thumbHeight);
    }
}