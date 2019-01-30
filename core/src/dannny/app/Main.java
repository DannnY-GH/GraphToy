package dannny.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.Random;

public class Main implements ApplicationListener {
    static final int RND_GR_V = 200;
    static final int RND_GR_E = 200;
    private static final int WIDTH = 8000;
    private static final int HEIGHT = 4800;
    private static final long TIME_QUANTUM = 3;
    private static final int FONT_SIZE = 10;
    private static final Color backCL = new Color(0x112F41FF);
    private static final Color usualCircleCL = new Color(0xED553B9F);
    private static final Color vipCircleCL = new Color(0xED553B);
    private static final Color oneCircleCL = new Color(0xF2B134);
    private static final Color edgeCL = new Color(0xF2B134FF);
    private static final Color edgeTextCL = new Color(0x4FB99FFF);
    private static final Color nodeTextCL = new Color(0xABD100FF);
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Vector2 beg = new Vector2();
    private Vector2 end = new Vector2();
    private Vector3 tmp = new Vector3();
    private long startTime;
    public static MasterFSM masterFSM = new MasterFSM();
    static VisualGraph vg;
    static Random rnd = new Random();
    static OrthographicCamera cam;
    static Graph g;
    File file = new File("in.txt");

    @Override
    public void create() {
        InputMultiplexer im = new InputMultiplexer();
        InputHandler controller = new InputHandler();
        GestureDetector gd = new GestureDetector(controller);
        gd.setLongPressSeconds(0.2f);
        im.addProcessor(gd);
        im.addProcessor(controller);
        Gdx.input.setInputProcessor(im);
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        shapeRenderer = new ShapeRenderer();
        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.position.set(0, 0, 0.0f);
        cam.direction.set(0, 0, -1);
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Comic_Sans_MS.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = FONT_SIZE;
        font = generator.generateFont(parameter);

        g = new Graph(RND_GR_V);
        g.RandomInit(RND_GR_V, RND_GR_E);
        vg = new VisualGraph(g);
    }

    @Override
    public void render() {
        if (InputHandler.deletingNode)
            return;
        cam.update();
        long elapsedTime = TimeUtils.timeSinceMillis(startTime);
        if (elapsedTime > TIME_QUANTUM && vg.isUpdating) {
            startTime = System.currentTimeMillis();
            vg.Update();
        }
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //background color
        Gdx.gl.glClearColor(backCL.r, backCL.g, backCL.b, 1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //begin rendering
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.OLIVE);
        //origin
        shapeRenderer.rect(0, 0, 20, 20);
        //edges
        shapeRenderer.setColor(edgeCL);
        int N = g.getNodesCount();
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < g.vertices.get(u).size(); v++) {
                //vec3 to vec2
                tmp.set(vg.points.get(u).pos);
                beg.set(tmp.x, tmp.y);
                tmp.set(vg.points.get(g.vertices.get(u).get(v).index).pos);
                end.set(tmp.x, tmp.y);
                shapeRenderer.rectLine(beg, end, VisualGraph.EDGE_THICK);
            }
        }
        //nodes
        int nodesCount = g.getNodesCount();
        for (int i = 0; i < nodesCount; i++) {
            VisualGraph.Point pt = vg.points.get(i);
            //smooth edge connection
            shapeRenderer.setColor(edgeCL);
            shapeRenderer.circle(pt.pos.x, pt.pos.y, VisualGraph.EDGE_THICK / 2f, 100);
            //type of circle
            if (pt.selected)
                shapeRenderer.setColor(oneCircleCL);
            else
                shapeRenderer.setColor(usualCircleCL);
            shapeRenderer.circle(pt.pos.x, pt.pos.y, VisualGraph.NODE_RADIUS, 100);
        }
        shapeRenderer.end();
        //node numbers
        font.setColor(nodeTextCL);
        font.getData().setScale(2);
        batch.begin();
        for (int u = 0; u < nodesCount; u++)
            font.draw(batch, String.valueOf(u), vg.points.get(u).pos.x - 15, vg.points.get(u).pos.y + 10);
        //edge numbers
        font.getData().setScale(1.4f);
        font.setColor(edgeTextCL);
        for (int u = 0; u < nodesCount; u++) {
            LinkedList<Node> adj = g.vertices.get(u);
            for (int v = 0; v < adj.size(); v++) {
                Vector3 beg = vg.points.get(u).pos;
                Vector3 end = vg.points.get(adj.get(v).index).pos;
                font.draw(batch, String.valueOf(adj.get(v).cost), (beg.x + end.x) / 2, (beg.y + end.y) / 2);
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, 0);
        cam.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
