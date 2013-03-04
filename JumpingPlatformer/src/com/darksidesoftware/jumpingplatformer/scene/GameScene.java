package com.darksidesoftware.jumpingplatformer.scene;

import java.io.IOException;
//import java.util.jar.Attributes;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
//import org.andengine.util.HorizontalAlign;
//import org.andengine.util.color.Color;
//import org.andengine.util.level.LevelLoader;
import org.andengine.util.level.simple.SimpleLevelLoader;

import org.xml.sax.Attributes;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.darksidesoftware.jumpingplatformer.base.BaseScene;
import com.darksidesoftware.jumpingplatformer.manager.SceneManager;
import com.darksidesoftware.jumpingplatformer.manager.SceneManager.SceneType;
import com.darksidesoftware.jumpingplatformer.object.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	//========================================================================
	// Variables
	//========================================================================
	
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private PhysicsWorld physicsWorld;
	private Player player;
	private boolean firstTouch = false;
	private Text gameOverText;
	private boolean gameOverDisplayed = false;
	private Text gameFinishText;
	private boolean gameFinishDisplayed = false;
	private int numCoins = 0;
	
	private static final int COIN_VALUE = 10;
	// variables need for level loader as we will be loading out level
	// using xml
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	
	//========================================================================
	// overridden methods
	//========================================================================
	
	@Override
	public void createScene() {
		// TODO Auto-generated method stub
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(1);
		createGameOverText();
		//createGameFinishText();
		setOnSceneTouchListener(this);
		
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		camera.setHUD(null);
		camera.setCenter(400,240);
		camera.setChaseEntity(null);
		//TODO code responsible for disposing scene
		// removing all game scene objects
	}
	
	//========================================================================
	// other methods
	//========================================================================
	
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			if(!firstTouch) {
				player.setRunning();
				firstTouch = true;
			}
			/*else if (numCoins == (score / COIN_VALUE)) {
				player.stopRunning();
				displayGameOverText();
			}*/
			else {
				player.jump();
			}
		}
		return false;
	}
	
	private void loadLevel(int levelID) {
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) {
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes,
					final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
				
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				
				//TODO later is to specify camera bounds and create invisible walls
				// on the beginning and on the end of the level
				camera.setBounds(0, 0, width, height); // here we set camera bounds
				camera.setBoundsEnabled(true);
				
				return GameScene.this;
			}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY) {
			
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent,
					final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
				
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1)) {
					levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
					PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2)) {
					levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform2");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3)) {
					levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform3");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN)) {
					numCoins++;
					levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom) {
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) {
							super.onManagedUpdate(pSecondsElapsed);
							
							if (player.collidesWith(this)) {
								addToScore(COIN_VALUE);
								this.setVisible(false);
								this.setIgnoreUpdate(true);
							}
							

						}
					};
					levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
				}
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
					player = new Player(x, y, vbom, camera, physicsWorld) {
						@Override
						public void onDie() {
							if(!gameOverDisplayed) {
								displayGameOverText();
							}
						}
					};
					levelObject = player;
				}
				else {
					throw new IllegalArgumentException();
				}
				
				levelObject.setCullingEnabled(true);
				
				return levelObject;
			}
		});
		
		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}
	
	public void createBackground() {
		setBackground(new Background(Color.BLUE));
	}
	
	private void createHUD() {
		gameHUD = new HUD();
		
		// create score text here
		scoreText = new Text(120, 450, resourcesManager.font,
				"Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		// should be scoreText.setAnchorCenter(0, 0); but it doesnt exist!! look it up!!
		//scoreText.setPosition(0, 0);
		scoreText.setSkewCenter(0, 0);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		
		camera.setHUD(gameHUD);
	}
	
	private void addToScore(int i) {
		score += i;
		scoreText.setText("Score: " + score);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		registerUpdateHandler(physicsWorld);
	}
	
	private void createGameOverText() {
		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
	}
	
	private void createGameFinishText() {
		gameFinishText = new Text(0, 0, resourcesManager.font, "You Finished!!", vbom);
	}
	
	private void displayGameOverText() {
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;
	}
	
	private void displayGameFinishText() {
		camera.setChaseEntity(null);
		gameFinishText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameFinishText);
		gameFinishDisplayed = true;
	}

}
