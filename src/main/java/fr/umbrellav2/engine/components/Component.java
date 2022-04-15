package fr.umbrellav2.engine.components;

import fr.umbrellav2.engine.GameObject;

public abstract class Component {

    private GameObject parentGameObject;

    public abstract void init();

    public abstract void update();

    public GameObject getParentGameObject() {
        return parentGameObject;
    }

    public void setParentGameObject(GameObject parentGameObject) {
        this.parentGameObject = parentGameObject;
    }
}
