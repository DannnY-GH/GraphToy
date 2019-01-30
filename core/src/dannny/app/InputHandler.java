package dannny.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static dannny.app.Main.RND_GR_E;
import static dannny.app.Main.RND_GR_V;
import static dannny.app.Main.g;
import static dannny.app.Main.masterFSM;
import static dannny.app.Main.vg;
import static dannny.app.Main.cam;
import static dannny.app.Utils.*;

public class InputHandler implements GestureDetector.GestureListener, InputProcessor {


    public enum InputEvents {TAP, RMU, LONG_PRESS}

    static Vector3 activeTouch = new Vector3();
    static boolean deletingNode = false;
    private static final float MODE_ZONE_SIDE = 0.2f * Gdx.graphics.getHeight();
    private Vector3 tmpV3 = new Vector3();
    private Vector2 tmpV2 = new Vector2(0, 0);
    private Vector2 initial = new Vector2();
    private Vector2 current = new Vector2();
    private Vector3 currentCamPos = new Vector3();
    private boolean longPressed = false;
    private int tweakMode = 1;
    private float currentCamZoom;
    private float currentCamAngle;
    private boolean startPinch = false;

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        activeTouch.set(x, y, 0);
        cam.unproject(activeTouch);
        //determine if touched node
        for (int i = 0; i < vg.g.getNodesCount(); i++) {
            VisualGraph.Point pt = vg.points.get(i);
            float dx = pt.pos.x - activeTouch.x;
            float dy = pt.pos.y - activeTouch.y;
            if (dx * dx + dy * dy <= VisualGraph.NODE_RADIUS * VisualGraph.NODE_RADIUS) {
                vg.selectedNode = i;
                break;
            }
        }
        Gdx.app.log("INFO", String.valueOf(x) + " " + String.valueOf(y));
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (longPressed) {
            //avoiding delay after longPress
            longPressed = false;
            return false;
        }
        if (count == 1) {
            if (x < MODE_ZONE_SIDE && y < MODE_ZONE_SIDE) {
                tweakMode = (tweakMode + 1) % 3;
                return false;
            }
            if (button == Input.Buttons.RIGHT)
                Main.masterFSM.doEvent(InputEvents.RMU);
            else
                Main.masterFSM.doEvent(InputEvents.TAP);
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        if (vg.selectedNode != -1) {
            deletingNode = true;
            g.DeleteNode(vg.selectedNode);
            vg.points.remove(vg.selectedNode);
            masterFSM.doEvent(InputEvents.LONG_PRESS);
            vg.selectedNode = -1;
            deletingNode = false;
        } else
            vg.isUpdating = !vg.isUpdating;
        //because of pinch blocking after longPress fired
        longPressed = true;
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (vg.selectedNode == -1) {
            tmpV2.set(-deltaX, deltaY);
            tmpV2.rotate(-getAngle(cam));
            cam.translate(tmpV2.scl(currentCamZoom));
        } else {
            cam.unproject(tmpV3.set(x, y, 0));
            vg.points.get(vg.selectedNode).pos.set(tmpV3);
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        currentCamZoom = cam.zoom;
        vg.selectedNode = -1;
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (!startPinch) {
            currentCamAngle = getAngle(cam);
            currentCamPos.set(cam.position);
            currentCamZoom = cam.zoom;
            startPinch = true;
        }
        initial.set(initialPointer2).sub(initialPointer1);
        current.set(pointer2).sub(pointer1);
        //zooming camera
        cam.zoom = (initial.len() / current.len()) * currentCamZoom;
        //rotating camera
        float dAng = (float) (Math.acos(initial.dot(current) / (initial.len() * current.len())) * 180 / Math.PI);
        dAng *= 2.0f;
        if (initial.crs(current) < 0)
            setAngle(cam, currentCamAngle + dAng);
        else
            setAngle(cam, currentCamAngle - dAng);
        //moving camera
        initial.set(initialPointer1).add(initialPointer2);
        current.set(pointer1).add(pointer2);
        tmpV2.set(current).sub(initial).scl(0.5f);
        tmpV2.set(-tmpV2.x, tmpV2.y).rotate(-getAngle(cam)).scl(currentCamZoom);
        cam.position.set(tmpV2.add(currentCamPos.x, currentCamPos.y), currentCamPos.z);
        cam.update();
        return false;
    }

    @Override
    public void pinchStop() {
        startPinch = false;
    }

    @Override
    public boolean scrolled(int amount) {
        cam.zoom += 0.4 * amount;
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 1000);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        final int dK = 100;
        final float dC = 1.0f;
        final int dT = 20;
        switch (keycode) {
            case Input.Keys.MINUS:
                if (Gdx.input.isKeyPressed(Input.Keys.K))
                    vg.K -= dK;
                if (Gdx.input.isKeyPressed(Input.Keys.C))
                    vg.C -= dC;
                if (Gdx.input.isKeyPressed(Input.Keys.T))
                    vg.T -= dT;
                break;
            case Input.Keys.EQUALS:
                if (Gdx.input.isKeyPressed(Input.Keys.K))
                    vg.K += dK;
                if (Gdx.input.isKeyPressed(Input.Keys.C))
                    vg.C += dC;
                if (Gdx.input.isKeyPressed(Input.Keys.T))
                    vg.T += dT;
                break;
            case Input.Keys.VOLUME_DOWN:
                switch (tweakMode) {
                    case 0:
                        vg.K -= dK;
                        break;
                    case 1:
                        vg.C -= dC;
                        break;
                    case 2:
                        vg.T -= dT;
                        break;
                }
                break;
            case Input.Keys.VOLUME_UP:
                switch (tweakMode) {
                    case 0:
                        vg.K += dK;
                        break;
                    case 1:
                        vg.C += dC;
                        break;
                    case 2:
                        vg.T += dT;
                        break;
                }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                vg.isUpdating = !vg.isUpdating;
                break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.BACK:
                g.vertices.clear();
                vg.points.clear();
                break;
            case Input.Keys.MENU:
            case Input.Keys.R:
                g = new Graph(RND_GR_V);
                g.RandomInit(RND_GR_V, RND_GR_E);
                vg = new VisualGraph(g);
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
}
