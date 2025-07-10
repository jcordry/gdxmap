package uk.ac.scedt.mam.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.graphics.use

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(MapScreen())
        setScreen<MapScreen>()
    }
}

class MapScreen : KtxScreen {
//    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(Linear, Linear) }
    private val batch = SpriteBatch()
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport // Is this correct?

    override fun show() {
        super.show()
        camera = OrthographicCamera()
        viewport = FitViewport(800f, 600f, camera)
        viewport.apply()
        Gdx.files.internal("tilemap01.tmx")
        val mapLoader = TmxMapLoader()
        map = mapLoader.load("tilemap01.tmx")
        renderer = OrthogonalTiledMapRenderer(map, 1f)
//        camera.setToOrtho(true)
        camera.position.set(viewport.worldWidth/2f, viewport.worldHeight/2f, 0f)
    }

    override fun render(delta: Float) {
        clearScreen(red = 0f, green = 0f, blue = 0f)
        camera.update()
        renderer.setView(camera)
        renderer.render()
        batch.use {
//            it.draw(image, 100f, 160f)
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
    }

    override fun dispose() {
//        image.disposeSafely()
        renderer.disposeSafely()
        batch.disposeSafely()
    }
}
