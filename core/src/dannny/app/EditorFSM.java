package dannny.app;

import static dannny.app.Main.*;
import static dannny.app.InputHandler.InputEvents;

public class EditorFSM {
    enum EditorStates {Idle, DrawingEdge}

    private EditorStates state;

    EditorFSM() {
        state = EditorStates.Idle;
    }

    private VisualGraph.Point tmpPt;
    private int firstNodeNum = -1;
    private int secondNodeNum = -1;

    void doEvent(InputEvents e) {
        switch (e) {
            case TAP:
                switch (state) {
                    case Idle:
                        if (vg.selectedNode != -1) {
                            //node selected
                            tmpPt = vg.points.get(vg.selectedNode);
                            tmpPt.selected = true;
                            firstNodeNum = vg.selectedNode;
                            state = EditorStates.DrawingEdge;
                        } else {
                            //space selected
                            g.AddNode();
                            vg.AddPoint(InputHandler.activeTouch);
                            state = EditorStates.Idle;
                        }
                        break;
                    case DrawingEdge:
                        //other node selected
                        if (vg.selectedNode != -1) {
                            if (vg.selectedNode != firstNodeNum) {
                                secondNodeNum = vg.selectedNode;
                                g.AddEdge(firstNodeNum, secondNodeNum, rnd.nextInt(VisualGraph.MAX_EDGE_RND));
                                vg.points.get(firstNodeNum).selected = false;
                                vg.points.get(secondNodeNum).selected = true;
                                firstNodeNum = secondNodeNum;
                                state = EditorStates.DrawingEdge;
                            } else {
                                vg.points.get(firstNodeNum).selected = false;
                                state = EditorStates.Idle;
                            }
                        } else {
                            //tap space
                            int newNodeNum = g.AddNode();
                            vg.AddPoint(InputHandler.activeTouch);
                            g.AddEdge(firstNodeNum, newNodeNum, rnd.nextInt(VisualGraph.MAX_EDGE_RND));
                            vg.points.get(firstNodeNum).selected = false;
                            vg.points.get(newNodeNum).selected = true;
                            firstNodeNum = newNodeNum;
                            state = EditorStates.DrawingEdge;
                        }
                        break;
                }
                vg.selectedNode = -1;
                break;
            case LONG_PRESS:
                state = EditorStates.Idle;
                break;
        }
        System.out.println(state);
    }
}
