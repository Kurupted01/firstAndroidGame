package com.darksidesoftware.jumpingplatformer.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.darksidesoftware.jumpingplatformer.base.BaseScene;
import com.darksidesoftware.jumpingplatformer.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {
	
	//========================================================================
	// Variables
	//========================================================================
	
	private Sprite splash;
	
	@Override
	public void createScene() {
		splash = new Sprite(400, 240, resourcesManager.splash_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		splash.setScale(1.5f);
		splash.setPosition(400, 240);
		//splash.setPosition(300, 120);
		attachChild(splash);
	}
	
	@Override
	public void onBackKeyPressed() {
		
	}
	
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}
}
