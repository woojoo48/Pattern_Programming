package frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import frames.GShapeToolBar.EShapeTool;
import shapes.GShape;
import shapes.GShape.EAnchor;
import shapes.GShape.EPoints;
import transformers.GDrawer;
import transformers.GMover;
import transformers.GResizer;
import transformers.GRotater;
import transformers.GTransformer;

public class GDrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    public enum EDrawingState {
        eIdle,
        e2P,
        eNP
    }
    
    // 기본 필드들
    private Vector<GShape> shapes;
    private GTransformer transformer;
    private GShape currentShape;
    private GShape selectedShape;
    private boolean bUpdated;
    private EShapeTool eShapeTool;
    private EDrawingState eDrawingState;
    
    // ✨ 간단한 히스토리 관리 (GHistoryManager 삭제!)
    private ArrayList<Vector<GShape>> history;
    private int historyIndex;
    private static final int MAX_HISTORY = 50;
    
    // ✨ 간단한 그룹화 관리
    private int nextGroupId = 1;
    
    public GDrawingPanel() {
        this.setBackground(Color.WHITE);
        
        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        
        this.currentShape = null;
        this.selectedShape = null;
        this.shapes = new Vector<GShape>();
        this.eShapeTool = null;
        this.eDrawingState = EDrawingState.eIdle;
        this.bUpdated = false;
        
        // ✨ 간단한 히스토리 초기화
        this.history = new ArrayList<>();
        this.historyIndex = -1;
        saveState(); // 초기 상태 저장
    }

    public void initialize() {
        this.shapes.clear();
        this.history.clear();
        this.historyIndex = -1;
        saveState(); // 초기 상태 저장
        this.repaint();
    }
    
    // Getter and Setter
    public Vector<GShape> getShape() {
        return this.shapes;
    }
    
    public void setShapes(Vector<GShape> shapes) {
        this.shapes = shapes;
        this.history.clear();
        this.historyIndex = -1;
        saveState(); // 새 상태로 히스토리 초기화
    }
    
    public void setEShapeTool(EShapeTool eShapeTool) {
        this.eShapeTool = eShapeTool;
        System.out.println("도구 변경됨: " + eShapeTool); // ✨ 디버그
    }
    
    // ✨ 디버그용 메서드 추가
    public void printDebugInfo() {
        System.out.println("=== GDrawingPanel 디버그 정보 ===");
        System.out.println("현재 도구: " + eShapeTool);
        System.out.println("현재 상태: " + eDrawingState);
        System.out.println("도형 개수: " + shapes.size());
        System.out.println("선택된 도형: " + (selectedShape != null ? selectedShape.getClass().getSimpleName() : "없음"));
        System.out.println("Transformer: " + (transformer != null ? transformer.getClass().getSimpleName() : "없음"));
        System.out.println("================================");
    }
    
    public boolean isUpdated() {
        return this.bUpdated;
    }
    
    public void setBUpdated(boolean bUpdated) {
        this.bUpdated = bUpdated;
    }
    
    // ===== ✨ 간단한 히스토리 관리 =====
    
    private void saveState() {
        // ✨ 임시로 디버그를 위해 히스토리 저장 비활성화
        System.out.println("saveState 호출됨 - 현재 도형 수: " + shapes.size());
        
        // 현재 지점 이후 히스토리 삭제
        while (history.size() > historyIndex + 1) {
            history.remove(history.size() - 1);
        }
        
        // 새 상태 추가 (얕은 복사로 충분)
        Vector<GShape> newState = new Vector<>(shapes);
        history.add(newState);
        historyIndex++;
        
        // 크기 제한
        if (history.size() > MAX_HISTORY) {
            history.remove(0);
            historyIndex--;
        }
        
        System.out.println("히스토리 저장됨 - 히스토리 크기: " + history.size());
    }
    
    public void undo() {
        if (historyIndex > 0) {
            historyIndex--;
            shapes = new Vector<>(history.get(historyIndex));
            clearAllSelection();
            setBUpdated(true);
            repaint();
            System.out.println("Undo 실행됨");
        } else {
            System.out.println("더 이상 실행 취소할 수 없습니다.");
        }
    }
    
    public void redo() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            shapes = new Vector<>(history.get(historyIndex));
            clearAllSelection();
            setBUpdated(true);
            repaint();
            System.out.println("Redo 실행됨");
        } else {
            System.out.println("더 이상 다시 실행할 수 없습니다.");
        }
    }
    
    public boolean canUndo() {
        return historyIndex > 0;
    }
    
    public boolean canRedo() {
        return historyIndex < history.size() - 1;
    }
    
    // ===== ✨ 간단한 그룹화 관리 =====
    
    public void groupSelectedShapes() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.size() < 2) {
            System.out.println("그룹화하려면 2개 이상의 도형을 선택해야 합니다.");
            return;
        }
        
        saveState(); // 히스토리 저장
        
        // 선택된 도형들에게 같은 groupId 부여
        int groupId = nextGroupId++;
        for (GShape shape : selected) {
            shape.setGroupId(groupId);
            shape.setSelected(false);
        }
        
        // 그룹 전체 선택
        selectGroup(groupId);
        setBUpdated(true);
        repaint();
        System.out.println(selected.size() + "개 도형을 그룹화했습니다.");
    }
    
    public void ungroupSelectedShape() {
        if (selectedShape == null || !selectedShape.isGrouped()) {
            System.out.println("그룹을 해제하려면 그룹을 선택해야 합니다.");
            return;
        }
        
        saveState(); // 히스토리 저장
        
        int groupId = selectedShape.getGroupId();
        Vector<GShape> ungrouped = new Vector<>();
        
        // 같은 그룹의 모든 도형 해제
        for (GShape shape : shapes) {
            if (shape.getGroupId() == groupId) {
                shape.setGroupId(-1); // 그룹 해제
                shape.setSelected(true); // 선택 상태로
                ungrouped.add(shape);
            }
        }
        
        selectedShape = ungrouped.isEmpty() ? null : ungrouped.lastElement();
        setBUpdated(true);
        repaint();
        System.out.println("그룹을 해제하여 " + ungrouped.size() + "개 도형으로 분리했습니다.");
    }
    
    public boolean canGroup() {
        return getSelectedShapes().size() >= 2;
    }
    
    public boolean canUngroup() {
        return selectedShape != null && selectedShape.isGrouped();
    }
    
    private void selectGroup(int groupId) {
        clearAllSelection();
        for (GShape shape : shapes) {
            if (shape.getGroupId() == groupId) {
                shape.setSelected(true);
                selectedShape = shape; // 마지막 것을 대표로
            }
        }
    }
    
    private Vector<GShape> getSelectedShapes() {
        Vector<GShape> selected = new Vector<>();
        for (GShape shape : shapes) {
            if (shape.isSelected()) {
                selected.add(shape);
            }
        }
        return selected;
    }
    
    // ===== 도형 순서 변경 메서드들 =====
    
    public void bringToFront() {
        if (selectedShape == null) {
            System.out.println("선택된 도형이 없습니다.");
            return;
        }
        
        saveState();
        
        if (selectedShape.isGrouped()) {
            // 그룹 전체 이동
            moveGroupToPosition(selectedShape.getGroupId(), shapes.size());
        } else {
            // 개별 도형 이동
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(selectedShape);
            }
        }
        
        setBUpdated(true);
        repaint();
        System.out.println("도형을 맨 앞으로 이동했습니다.");
    }
    
    public void sendToBack() {
        if (selectedShape == null) {
            System.out.println("선택된 도형이 없습니다.");
            return;
        }
        
        saveState();
        
        if (selectedShape.isGrouped()) {
            // 그룹 전체 이동
            moveGroupToPosition(selectedShape.getGroupId(), 0);
        } else {
            // 개별 도형 이동
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(0, selectedShape);
            }
        }
        
        setBUpdated(true);
        repaint();
        System.out.println("도형을 맨 뒤로 이동했습니다.");
    }
    
    public void bringForward() {
        if (selectedShape == null) return;
        
        saveState();
        
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex >= shapes.size() - 1) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex + 1, selectedShape);
        setBUpdated(true);
        repaint();
    }
    
    public void sendBackward() {
        if (selectedShape == null) return;
        
        saveState();
        
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex <= 0) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex - 1, selectedShape);
        setBUpdated(true);
        repaint();
    }
    
    private void moveGroupToPosition(int groupId, int position) {
        Vector<GShape> groupShapes = new Vector<>();
        
        // 그룹 도형들 수집 및 제거
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).getGroupId() == groupId) {
                groupShapes.add(0, shapes.remove(i));
            }
        }
        
        // 지정된 위치에 삽입
        for (int i = 0; i < groupShapes.size(); i++) {
            shapes.add(Math.min(position + i, shapes.size()), groupShapes.get(i));
        }
    }
    
    // ===== 기본 메서드들 =====
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GShape shape : shapes) {
            shape.draw((Graphics2D) g);
        }
    }
    
    private GShape onShape(int x, int y) {
        // 뒤에서부터 검사 (위에 있는 도형 우선)
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                return shapes.get(i);
            }
        }
        return null;
    }
    
    private void clearAllSelection() {
        for (GShape shape : shapes) {
            shape.setSelected(false);
        }
        selectedShape = null;
    }
    
    private void selectShapeOrGroup(GShape clickedShape, boolean isMultiSelect) {
        if (isMultiSelect) {
            // Ctrl + 클릭: 토글 선택
            clickedShape.setSelected(!clickedShape.isSelected());
            if (clickedShape.isSelected()) {
                selectedShape = clickedShape;
            }
        } else {
            // 일반 클릭
            if (clickedShape.isGrouped()) {
                selectGroup(clickedShape.getGroupId());
            } else {
                clearAllSelection();
                clickedShape.setSelected(true);
                selectedShape = clickedShape;
            }
        }
    }
    
    // Transform 메서드들
    private void startTransform(int x, int y, boolean isMultiSelect) {
        if (eShapeTool == EShapeTool.eSelect) {
            GShape clickedShape = onShape(x, y);
            
            if (clickedShape == null) {
                if (!isMultiSelect) {
                    clearAllSelection();
                }
            } else {
                selectShapeOrGroup(clickedShape, isMultiSelect);
                
                if (!isMultiSelect && selectedShape != null) {
                    saveState(); // Transform 시작 전 히스토리 저장
                    
                    EAnchor selectedAnchor = clickedShape.getESeletedAnchor();
                    if (selectedAnchor == EAnchor.eMM) {
                        transformer = new GMover(selectedShape);
                    } else if (selectedAnchor == EAnchor.eRR) {
                        transformer = new GRotater(selectedShape);
                    } else {
                        transformer = new GResizer(selectedShape);
                    }
                    
                    // ✨ 그룹 작업을 위해 전체 도형 리스트 전달
                    transformer.setAllShapes(shapes);
                    transformer.start((Graphics2D) getGraphics(), x, y);
                }
            }
        } else {
            // ✨ 새 도형 그리기
            // saveState(); // 임시로 주석 처리
            
            currentShape = eShapeTool.newShape();
            shapes.add(currentShape);
            transformer = new GDrawer(currentShape);
            transformer.start((Graphics2D) getGraphics(), x, y);
            
            System.out.println("새 도형 생성: " + currentShape.getClass().getSimpleName()); // 디버그
        }
    }
    
    private void keepTransform(int x, int y) {
        if (transformer != null) {
            transformer.drag((Graphics2D) getGraphics(), x, y);
            repaint();
        }
    }
    
    private void addPoint(int x, int y) {
        if (transformer != null) {
            transformer.addPoint((Graphics2D) getGraphics(), x, y);
        }
    }
    
    private void finishTransform(int x, int y) {
        if (transformer != null) {
            transformer.finish((Graphics2D) getGraphics(), x, y);
            
            if (eShapeTool == EShapeTool.eSelect) {
                // Select 모드에서의 특별 처리
                if (selectedShape == null && transformer instanceof GDrawer) {
                    shapes.remove(shapes.size() - 1);
                    for (GShape shape : shapes) {
                        if (currentShape != null && currentShape.contains(shape)) {
                            shape.setSelected(true);
                        } else {
                            shape.setSelected(false);
                        }
                    }
                }
            } else {
                // ✨ 새 도형 그리기 완료 - 선택 상태로 설정
                if (currentShape != null) {
                    clearAllSelection();
                    currentShape.setSelected(true);
                    selectedShape = currentShape;
                    System.out.println("도형 그리기 완료: " + currentShape.getClass().getSimpleName()); // 디버그
                }
            }
            
            setBUpdated(true);
            repaint();
        }
    }
    
    private void changeCursor(int x, int y) {
        if (eShapeTool == EShapeTool.eSelect) {
            GShape hoveredShape = onShape(x, y);
            if (hoveredShape == null) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                EAnchor eAnchor = hoveredShape.getESeletedAnchor();
                setCursor(eAnchor.getCursor());
            }
        }
    }
    
    // 마우스 이벤트 처리
    private class MouseHandler implements MouseListener, MouseMotionListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                mouse1Clicked(e);
            } else if (e.getClickCount() == 2) {
                mouse2Clicked(e);
            }
        }
        
        private void mouse1Clicked(MouseEvent e) {
            boolean isMultiSelect = e.isControlDown();
            
            if (eDrawingState == EDrawingState.eIdle) {
                if (eShapeTool.getEPoints() == EPoints.e2P) {
                    startTransform(e.getX(), e.getY(), isMultiSelect);
                    if (!isMultiSelect && selectedShape != null && transformer != null) {
                        eDrawingState = EDrawingState.e2P;
                    } else if (!isMultiSelect && eShapeTool != EShapeTool.eSelect) {
                        // ✨ 새 도형 그리기 상태로 변경
                        eDrawingState = EDrawingState.e2P;
                    }
                } else if (eShapeTool.getEPoints() == EPoints.eNP) {
                    if (!isMultiSelect) {
                        startTransform(e.getX(), e.getY(), false);
                        eDrawingState = EDrawingState.eNP;
                    }
                }
            } else if (eDrawingState == EDrawingState.e2P) {
                finishTransform(e.getX(), e.getY());
                eDrawingState = EDrawingState.eIdle;
            } else if (eDrawingState == EDrawingState.eNP) {
                addPoint(e.getX(), e.getY());
            }
            
            if (isMultiSelect) {
                repaint();
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            if (eDrawingState == EDrawingState.e2P || eDrawingState == EDrawingState.eNP) {
                keepTransform(e.getX(), e.getY());
            } else if (eDrawingState == EDrawingState.eIdle) {
                changeCursor(e.getX(), e.getY());
            }
        }
        
        private void mouse2Clicked(MouseEvent e) {
            if (eDrawingState == EDrawingState.eNP) {
                finishTransform(e.getX(), e.getY());
                eDrawingState = EDrawingState.eIdle;
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {}
        
        @Override
        public void mouseDragged(MouseEvent e) {}
        
        @Override
        public void mouseReleased(MouseEvent e) {}
        
        @Override
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}
    }
}