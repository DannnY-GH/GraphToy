package dannny.app;

import static dannny.app.InputHandler.InputEvents;

public class MasterFSM {
    enum MasterStates {AlgoFSM, EditorFSM}

    private MasterStates state;
    private AlgoFSM algoFSM;
    private EditorFSM edtFSM;

    MasterFSM() {
        state = MasterStates.EditorFSM;
        algoFSM = new AlgoFSM();
        edtFSM = new EditorFSM();
    }

    void doEvent(InputEvents e) {
        switch (e) {
            case RMU:
                switch (state) {
                    case AlgoFSM:
                        state = MasterStates.EditorFSM;
                        break;
                    case EditorFSM:
                        state = MasterStates.AlgoFSM;
                        break;
                }
                break;
            default:
                switch (state) {
                    case AlgoFSM:
                        algoFSM.doEvent(e);
                        break;
                    case EditorFSM:
                        edtFSM.doEvent(e);
                        break;
                }
        }
    }

}
