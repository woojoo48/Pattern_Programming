package global;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GConstants {
    private static final String XML_FILE_NAME = "GConstants.xml";
    private static boolean isInitialized = false;
    
    private static GConstants instance;
    
    
    //loaded data
    private static int screenX, screenY, screenWidth, screenHeight;
    private static String fileMenuLabel, editMenuLabel;
    private static String defaultFilePath, defaultExtension, defaultFileName, extensionFilter;
    private static int panelWidth, thumbnailWidth, thumbnailHeight;
    private static String slideTitleLabel;
    private static Map<String, String> fileMenuLabels = new HashMap<>();
    private static Map<String, String> fileMenuMethods = new HashMap<>();
    private static Map<String, String> editMenuLabels = new HashMap<>();
    private static Map<String, String> editMenuMethods = new HashMap<>();
    private static Map<String, String> shapeToolLabels = new HashMap<>();
    private static Map<String, String> slideButtonTexts = new HashMap<>();
    private static Map<String, Dimension> slideButtonSizes = new HashMap<>();
    private static Map<String, String> slideButtonCommands = new HashMap<>();
    private static Map<String, String> slideNames = new HashMap<>();
    private static Map<String, String> fileMessages = new HashMap<>();
    private static int pasteOffset, anchorWidth, anchorHeight, rotationHandleOffset, maxHistorySize;
    private static Map<String, Color> colors = new HashMap<>();
    private static Map<String, Font> fonts = new HashMap<>();
    private static Map<String, String> keyBindings = new HashMap<>();
    private static Map<String, Integer> mouseSettings = new HashMap<>();
    private static Map<String, String> uiTexts = new HashMap<>();
    
    public static GConstants getInstance() {
        if (instance == null) {
            instance = new GConstants();
            instance.initialize();
        }
        return instance;
    }
    
    private GConstants() {
    }
    
    public void initialize() {
        if (!isInitialized) {
            readFromFile(XML_FILE_NAME);
            isInitialized = true;
        }
    }
    
    private void readFromFile(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            File file = new File(fileName);
            if (!file.exists()) {
                file = new File("src/" + fileName);
            }
            if (!file.exists()) {
                return;
            }
            
            Document document = builder.parse(file);
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    
                    switch (nodeName) {
                        case "EMainFrame":
                            parseMainFrame(node);
                            break;
                        case "EMenu":
                            parseMenu(node);
                            break;
                        case "EFileMenuItem":
                            parseFileMenuItem(node);
                            break;
                        case "EEditMenuItem":
                            parseEditMenuItem(node);
                            break;
                        case "EShapeTool":
                            parseShapeTool(node);
                            break;
                        case "ESlidePanel":
                            parseSlidePanel(node);
                            break;
                        case "ESlideNames":
                            parseSlideNames(node);
                            break;
                        case "EFileMessages":
                            parseFileMessages(node);
                            break;
                        case "ETransformer":
                            parseTransformer(node);
                            break;
                        case "ECommand":
                            parseCommand(node);
                            break;
                        case "EColors":
                            parseColors(node);
                            break;
                        case "EFonts":
                            parseFonts(node);
                            break;
                        case "EKeyBindings":
                            parseKeyBindings(node);
                            break;
                        case "EMouse":
                            parseMouse(node);
                            break;
                        case "EUITexts":
                            parseUITexts(node);
                            break;
                    }
                }
            }
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private void parseMainFrame(Node node) {
            screenX = Integer.parseInt(getAttributeValue(node, "eX", "0"));
            screenY = Integer.parseInt(getAttributeValue(node, "eY", "0"));
            screenWidth = Integer.parseInt(getAttributeValue(node, "eW", "1200"));
            screenHeight = Integer.parseInt(getAttributeValue(node, "eH", "800"));
    }
    
    private void parseMenu(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String label = getAttributeValue(child, "label", "");
                
                switch (nodeName) {
                    case "eFileMenu":
                        fileMenuLabel = label;
                        break;
                    case "eEditMenu":
                        editMenuLabel = label;
                        break;
                }
            }
        }
    }
    
    private void parseFileMenuItem(Node node) {
        defaultFilePath = getAttributeValue(node, "defaultPathName", "");
        defaultExtension = getAttributeValue(node, "defaultExtension", "presentation");
        defaultFileName = getAttributeValue(node, "defaultFileName", "presentation.presentation");
        extensionFilter = getAttributeValue(node, "extensionFilter", "Presentation Files (*.presentation)");
        
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String label = getAttributeValue(child, "label", "");
                String methodName = getAttributeValue(child, "methodName", "");
                
                fileMenuLabels.put(nodeName, label);
                fileMenuMethods.put(nodeName, methodName);
            }
        }
    }
    
    private void parseEditMenuItem(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String label = getAttributeValue(child, "label", "");
                String methodName = getAttributeValue(child, "methodName", "");
                
                editMenuLabels.put(nodeName, label);
                editMenuMethods.put(nodeName, methodName);
            }
        }
    }
    
    private void parseShapeTool(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String label = getAttributeValue(child, "label", "");
                shapeToolLabels.put(nodeName, label);
            }
        }
    }
    
    private void parseSlidePanel(Node node) {
            panelWidth = Integer.parseInt(getAttributeValue(node, "panelWidth", "250"));
            thumbnailWidth = Integer.parseInt(getAttributeValue(node, "thumbnailWidth", "150"));
            thumbnailHeight = Integer.parseInt(getAttributeValue(node, "thumbnailHeight", "100"));
            slideTitleLabel = getAttributeValue(node, "titleLabel", "슬라이드");
            
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = child.getNodeName();
                    String text = getAttributeValue(child, "text", "");
                    int width = Integer.parseInt(getAttributeValue(child, "width", "40"));
                    int height = Integer.parseInt(getAttributeValue(child, "height", "30"));
                    String command = getAttributeValue(child, "actionCommand", "");
                    
                    slideButtonTexts.put(nodeName, text);
                    slideButtonSizes.put(nodeName, new Dimension(width, height));
                    slideButtonCommands.put(nodeName, command);
                }
            }
    }
    
    private void parseSlideNames(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String value = getAttributeValue(child, "value", "");
                slideNames.put(nodeName, value);
            }
        }
    }
    
    private void parseFileMessages(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String value = getAttributeValue(child, "value", "");
                fileMessages.put(nodeName, value);
            }
        }
    }
    
    private void parseTransformer(Node node) {
            pasteOffset = Integer.parseInt(getAttributeValue(node, "pasteOffset", "20"));
            anchorWidth = Integer.parseInt(getAttributeValue(node, "anchorWidth", "10"));
            anchorHeight = Integer.parseInt(getAttributeValue(node, "anchorHeight", "10"));
            rotationHandleOffset = Integer.parseInt(getAttributeValue(node, "rotationHandleOffset", "-30"));
    }
    
    private void parseCommand(Node node) {
            maxHistorySize = Integer.parseInt(getAttributeValue(node, "maxHistorySize", "100"));
    }
    
    private void parseColors(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                    int r = Integer.parseInt(getAttributeValue(child, "r", "255"));
                    int g = Integer.parseInt(getAttributeValue(child, "g", "255"));
                    int b = Integer.parseInt(getAttributeValue(child, "b", "255"));
                    colors.put(nodeName, new Color(r, g, b));
            }
        }
    }
    
    private void parseFonts(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                    String family = getAttributeValue(child, "family", "Arial");
                    int style = Integer.parseInt(getAttributeValue(child, "style", "1"));
                    int size = Integer.parseInt(getAttributeValue(child, "size", "12"));
                    fonts.put(nodeName, new Font(family, style, size));
            }
        }
    }
    
    private void parseKeyBindings(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String key = getAttributeValue(child, "key", "");
                String modifier = getAttributeValue(child, "modifier", "");
                
                keyBindings.put(nodeName + "_key", key);
                keyBindings.put(nodeName + "_modifier", modifier);
            }
        }
    }
    
    private void parseMouse(Node node) {
            mouseSettings.put("doubleClickThreshold", Integer.parseInt(getAttributeValue(node, "doubleClickThreshold", "2")));
            mouseSettings.put("singleClickThreshold", Integer.parseInt(getAttributeValue(node, "singleClickThreshold", "1")));
    }
    
    private void parseUITexts(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                String value = getAttributeValue(child, "value", "");
                uiTexts.put(nodeName, value);
            }
        }
    }
    
    private String getAttributeValue(Node node, String attributeName, String defaultValue) {
        Node attr = node.getAttributes().getNamedItem(attributeName);
        return attr != null ? attr.getNodeValue() : defaultValue;
    }
    
    //mainFrame
    public static int getScreenX() { return screenX; }
    public static int getScreenY() { return screenY; }
    public static int getScreenWidth() { return screenWidth; }
    public static int getScreenHeight() { return screenHeight; }
    
    //menus
    public static String getFileMenuLabel() { return fileMenuLabel; }
    public static String getEditMenuLabel() { return editMenuLabel; }
    
    //file
    public static String getDefaultFilePath() { return defaultFilePath; }
    public static String getDefaultExtension() { return defaultExtension; }
    public static String getDefaultFileName() { return defaultFileName; }
    public static String getExtensionFilter() { return extensionFilter; }
    
    //slide panel
    public static int getPanelWidth() { return panelWidth; }
    public static int getThumbnailWidth() { return thumbnailWidth; }
    public static int getThumbnailHeight() { return thumbnailHeight; }
    public static String getSlideTitleLabel() { return slideTitleLabel; }
    
    public static int getPasteOffset() { return pasteOffset; }
    public static int getAnchorWidth() { return anchorWidth; }
    public static int getAnchorHeight() { return anchorHeight; }
    public static int getRotationHandleOffset() { return rotationHandleOffset; }
    
    //command manager
    public static int getMaxHistorySize() { return maxHistorySize; }
    
    //filemenu
    public static String getFileMenuItemLabel(String key) { return fileMenuLabels.get(key); }
    public static String getFileMenuItemMethod(String key) { return fileMenuMethods.get(key); }
    
    //editmenu
    public static String getEditMenuItemLabel(String key) { return editMenuLabels.get(key); }
    public static String getEditMenuItemMethod(String key) { return editMenuMethods.get(key); }
    
    public static String getShapeToolLabel(String key) { return shapeToolLabels.get(key); }
    
    // 슬라이드 버튼
    public static String getSlideButtonText(String key) { return slideButtonTexts.get(key); }
    public static Dimension getSlideButtonSize(String key) { return slideButtonSizes.get(key); }
    public static String getSlideButtonCommand(String key) { return slideButtonCommands.get(key); }
    
    public static String getSlideName(String key) { return slideNames.get(key); }
    
    public static String getFileMessage(String key) { return fileMessages.get(key); }
    
    public static Color getBackgroundColor() { return colors.get("backgroundColor"); }
    public static Color getThumbnailPanelBg() { return colors.get("thumbnailPanelBg"); }
    public static Color getSelectedThumbnailBg() { return colors.get("selectedThumbnailBg"); }
    public static Color getThumbnailHoverBg() { return colors.get("thumbnailHoverBg"); }
    public static Color getThumbnailBorderSelected() { return colors.get("thumbnailBorderSelected"); }
    public static Color getAnchorFillColor() { return colors.get("anchorFillColor"); }
    public static Color getThumbnailBorder() { return colors.get("thumbnailBorder"); }
    public static Color getSlideNumberColor() { return colors.get("slideNumberColor"); }
    
    public static Font getSlideNumberFont() { return fonts.get("slideNumberFont"); }
    
    public static String getKeyBinding(String key) { return keyBindings.get(key); }
    
    public static int getDoubleClickThreshold() { return mouseSettings.getOrDefault("doubleClickThreshold", 2); }
    public static int getSingleClickThreshold() { return mouseSettings.getOrDefault("singleClickThreshold", 1); }
    
    public static String getUIText(String key) { return uiTexts.get(key); }
    
    
    public enum EFileMenuItem {
        eNew, eOpen, eSave, eSaveAs, eQuit;
        
        public String getName() {
            return GConstants.getFileMenuItemLabel(this.name());
        }
        
        public String getMethodName() {
            return GConstants.getFileMenuItemMethod(this.name());
        }
    }
    
    public enum EEditMenuItem {
        eUndo, eRedo, eCopy, ePaste, eDelete, eGroup, eUngroup, 
        eBringToFront, eSendToBack, eBringForward, eSendBackward;
        
        public String getName() {
            return GConstants.getEditMenuItemLabel(this.name());
        }
        
        public String getMethodName() {
            return GConstants.getEditMenuItemMethod(this.name());
        }
    }
}