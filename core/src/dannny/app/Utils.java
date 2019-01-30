package dannny.app;

import com.badlogic.gdx.graphics.OrthographicCamera;

class Utils {

    static float getAngle(OrthographicCamera cam) {
        return (float) (Math.atan2(cam.up.y, -cam.up.x) * 180 / Math.PI - 90f);
    }

    static void setAngle(OrthographicCamera cam, float ang) {
        if (Float.isNaN(ang))
            return;
        cam.up.set(0, 1, 0);
        cam.rotate(ang);
        cam.update();
    }
}
