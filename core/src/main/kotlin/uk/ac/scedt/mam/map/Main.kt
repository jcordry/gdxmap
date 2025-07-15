package uk.ac.scedt.mam.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
//import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
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

class Character(var x: Float, var y: Float) {
    private val sprite = Sprite(Texture("circle1.png".toInternalFile(), true))
    fun render(sb : SpriteBatch) {
        sb.use {
            it.draw(sprite, x, y)
        }
    }
}

class MapScreen : KtxScreen {
//    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(Linear, Linear) }
    private val batch = SpriteBatch()
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport // Is this the best for this exercise?
    private val player = Character(32f, 32f)
    private val tileSize = 32f
    private lateinit var wallLayer : TiledMapTileLayer
    private var cooldown = 0f
    private var cooldownMax = 0.1f
    private var targetx = 0f
    private var targety = 0f

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
        wallLayer = map.layers.get("walls") as TiledMapTileLayer
    }


    private fun input() {
        targetx = player.x
        targety = player.y
        if (Gdx.input.isKeyPressed(Keys.W)) {
            targety += 32f
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
           targety -= 32f
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            targetx -= 32f
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            targetx += 32f
        }

        // We need a timer to avoid processing the input before a certain delay (e.g.: 50ms)
        cooldown += Gdx.graphics.deltaTime
        if (cooldown > cooldownMax) {
            cooldown = 0f
            if (!isBlocked(targetx, targety)) {
                player.x = targetx
                player.y = targety
            }
        }
    }

    fun isBlocked(x: Float, y: Float): Boolean {
        val tileX = (x / tileSize).toInt() // should deal with player coordinates in game terms instead of pixels
        val tileY = (y / tileSize).toInt()
        val cell = wallLayer.getCell(tileX, tileY)
        return cell != null
    }

    private fun display () {
        clearScreen(red = 0f, green = 0f, blue = 0f)
        camera.update()
        renderer.setView(camera)
        renderer.render()
        player.render(batch)
    }

    override fun render(delta: Float) {
        input()
//        logic()
        display()
    }

    private fun logic() {
        TODO("Not yet implemented")
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
