package com.darksidesoftware.jumpingplatformer.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;
//import org.andengine.util.color.Color;
// doesnt exist :(---> import org.andengine.util.adt.color.Color; IT DOES NOW!!!

import com.darksidesoftware.jumpingplatformer.base.BaseScene;
import com.darksidesoftware.jumpingplatformer.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		attachChild(new Text(150, 240, resourcesManager.font, "Loading...", vbom));
	}
	
	@Override
	public void onBackKeyPressed() {
		return;
	}
	
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}
	
	@Override
	public void disposeScene() {
		
	}
}
