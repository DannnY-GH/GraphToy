package dannny.app;

import static dannny.app.InputHandler.InputEvents;
import static dannny.app.Main.vg;

public class AlgoFSM {
    enum AlgoStates {Idle, SelectedN1, SelectedN2}

    private AlgoStates state;
    private int N1 = -1, N2 = -1;

    AlgoFSM() {
        state = AlgoStates.Idle;
    }

    void doEvent(InputEvents e) {
        switch (e) {
            case TAP:
                switch (state) {
                    case Idle:
                        if (vg.selectedNode != -1) {
                            N1 = vg.selectedNode;
                            vg.points.get(vg.selectedNode).selected = true;
                            state = AlgoStates.SelectedN1;
                        }
                        break;
                    case SelectedN1:
                        if (vg.selectedNode != -1) {
                            if (vg.selectedNode != N1) {
                                N2 = vg.selectedNode;
                                vg.points.get(vg.selectedNode).selected = true;
                                state = AlgoStates.SelectedN2;
                            } else {
                                N1 = -1;
                                vg.points.get(vg.selectedNode).selected = false;
                                state = AlgoStates.Idle;
                            }
                        }
                        break;
                    case SelectedN2:
                        if (vg.selectedNode != -1) {
                            if (vg.selectedNode == N2) {
                                N2 = -1;
                                vg.points.get(vg.selectedNode).selected = false;
                                state = AlgoStates.SelectedN1;
                            }
                        }
                        break;
                }
                vg.selectedNode = -1;
                break;
        }
        System.out.println(state);
    }
}
