package transformers;

import java.awt.Graphics2D;
import java.util.Vector;
import commands.Command;
import commands.CommandManager;
import shapes.GShape;

public abstract class GTransformer implements Command {
    protected GShape shape;
    protected Vector<GShape> groupShapes;
    protected CommandManager commandManager;
    
    public GTransformer(GShape shape) {
        this.shape = shape;
    }
    
    public void setAllShapes(Vector<GShape> groupShapes) {
        this.groupShapes = groupShapes;
    }
    
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }
    
    public abstract void start(Graphics2D g2D, int x, int y); 
    public abstract void drag(Graphics2D g2D, int x, int y);
    public abstract void addPoint(Graphics2D graphics, int x, int y);
    
    public void finish(Graphics2D g2D, int x, int y) {
        finishTransform(g2D, x, y);
        
        if (commandManager != null && shouldSaveToHistory()) {
            commandManager.addToHistory(this);
        }
    }
    
    protected abstract void finishTransform(Graphics2D g2D, int x, int y);
    protected abstract boolean shouldSaveToHistory();
}